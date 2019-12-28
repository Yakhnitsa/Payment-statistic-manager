package com.yurets_y.payment_statistic.model.parser;

import com.yurets_y.payment_statistic.model.elements.PaymentList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Admin on 20.02.2018.
 */

@Service("documentParserBaseHtml")
public class DocumentParserBaseHTML implements DocumentParser {

     @Override
    public PaymentList parseFromURI(String uri) throws IOException {
        File uriFile = new File(java.net.URI.create(uri));
        return parseFromFile(uriFile);
    }

    @Override
    public PaymentList parseFromFile(File file) throws IOException {

        Document document = Jsoup.parse(file, "UTF-8");

        if(!file.getName().toLowerCase().endsWith(".html")){
            throw new IOException("Неизвесный формат файла!!!");
        }
        PaymentList paymentList = parseFromDocument(document);
        paymentList.setFile(file);
        return paymentList;
    }
    private PaymentList parseFromXMLDocument(Document document){
        Iterator<Element> stringIterator = document.select("tr").iterator();
        String currentChartType = "";
        List<List<String>> currentChartData = new LinkedList<List<String>>();
        PaymentList paymentList = new PaymentList();
        //TODO Создать полностью новый метод по прасингу из XML формата
        return paymentList;
    }

    private PaymentList parseFromDocument(Document document){
        // Список игнорируемых строк
        List<String> exclusionList = Arrays.asList(
                "",
                "ФІЛІЯ.+",
                "Перелік.+",
                "Назва.+",
                "Номер.+",
                "Сальдо.+",
                "\\W",
                "Дата",
                "Розрахунковий рахунок.+",
                "Всього.+",
                "В т\\.ч.+",
                "в т\\.ч.+",
                "ПДВ",
                "Разом",
                "За додатковi.+");

        PaymentList paymentList = new PaymentList();

        Iterator<Element> stringIterator = document.select("tr").iterator();
        String currentChartType = "";
        List<List<String>> currentChartData = new LinkedList<List<String>>();

        while(stringIterator.hasNext()){
            List<String> cellList = parseChartRow(stringIterator.next());
            if(cellList.size()<=0){ continue;}

            String first = cellList.get(0);


            if(first.contains("Перелік")){
                paymentList.setListName(cellList.get(0));
            }

            if(arrayElementIsMathes(cellList,"Код платника:(\\d*)",1)){
                paymentList.setPaymentCode(getPaymentCode(cellList));
            }

            paymentList.setOpeningBalance(getOpeningBalance(cellList));
            paymentList.setClosingBalance(getClosingBalance(cellList));
            paymentList.setTotalCosts(getTotalPayment(cellList));
            //фильтр ненужных строк
            if(listIsContains(exclusionList,first)){
                continue;
            }

            if((cellList.size() == 1)){
                paymentList.addDataToTableMap(currentChartType,currentChartData);
                currentChartData = new LinkedList<>();
                currentChartType = first;
                continue;
            }
            if(!arrayElementIsMathes(cellList,"\\d{2}\\.\\d{2}\\.\\d{4}",0)){
                continue;
            }
            currentChartData.add(cellList);
        }
        return paymentList;
    }

    private List<String> parseChartRow(Element tableString){

        List<String> cellList = new ArrayList<String>();
        Iterator<Element> cellIterator = tableString.select("th").iterator();
        while(cellIterator.hasNext()){
            cellList.add(cellIterator.next().text());
        }
        cellIterator = tableString.select("tcol").iterator();
        while(cellIterator.hasNext()){
            cellList.add(cellIterator.next().text());
        }
        return cellList;
    }

    private boolean listIsContains(List<String> patternList, String sample){
        for(String pattern: patternList){
            if(sample.matches(pattern))
                return true;
        }
        return false;
    }

    private boolean arrayElementIsMathes(List<String> testedList, String pattern, int index){
        if(testedList.size() <= index){
            return false;
        }
        return testedList.get(index).matches(pattern);
    }
    
    private double getOpeningBalance(List<String> chartRow){
        if(chartRow.get(0).matches("Сальдо на початок.+")) {
            try {
                String numb = chartRow.get(0).split(":\\s?-?")[1];
                return Double.parseDouble(numb.replaceAll(",","."));
            } catch (NumberFormatException|ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }

        return -1;
    }

    private double getClosingBalance(List<String> chartRow){
        if(chartRow.size()>=4 && chartRow.get(2).matches("Сальдо на кінець.+")) {
            try {
                String numb = chartRow.get(3).split("-")[1];
                return Double.parseDouble(numb.replaceAll(",","."));
            } catch (ArrayIndexOutOfBoundsException|NumberFormatException e) {
            }
        }

        return -1;

    }

    private double getTotalPayment(List<String> chartRow){
        if(chartRow.size()>= 2 && chartRow.get(0).matches("Разом")) {
            return Double.parseDouble(chartRow.get(1).replaceAll(",","."));
        }

        return -1;
    }

    private int getPaymentCode(List<String> chartRow){
        if(chartRow.size()>1){
            String regexp = "Код платника:(\\d*)";
            Pattern pattern = Pattern.compile(regexp);
            Matcher matcher = pattern.matcher(chartRow.get(1));
            if(matcher.matches()){
                return Integer.parseInt(matcher.group(1));
            }
        }
        return -1;
    }

}
