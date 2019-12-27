package com.yurets_y.payment_statistic.model.parser;

import com.yurets_y.payment_statistic.model.entity.PaymentList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("htmlDocParser")
public class HtmlDocParser implements DocParser {
    @Override
    public PaymentList parseFromFile(File file) throws IOException {
        Document document = Jsoup.parse(file, "UTF-8");

        if(!file.getName().toLowerCase().endsWith(".html")){
            throw new IOException("Неизвесный формат файла!!!");
        }
        PaymentList paymentList = parseFromJsoup(document);
        paymentList.setBackupFile(file);
        return paymentList;

    }

    private PaymentList parseFromJsoup(Document document){
        PaymentList paymentList = new PaymentList();
        Iterator<Element> stringIterator = document.select("tr").iterator();

        while(stringIterator.hasNext()){
            List<String> cellList = parseChartRow(stringIterator.next());

            if(cellList.size()<=0){ continue;}

            String first = cellList.get(0);

            if(first.contains("Перелік")){
                paymentList.setNumber(getListNumb(first));
                paymentList.setDate(getListDate(first));
            }

//            if(arrayElementIsMathes(cellList,"Код платника:(\\d*)",1)){
//                paymentList.setPaymentCode(getPaymentCode(cellList));
//            }
//
//            paymentList.setOpeningBalance(getOpeningBalance(cellList));
//            paymentList.setClosingBalance(getClosingBalance(cellList));
//            paymentList.setTotalCosts(getTotalPayment(cellList));
//            //фильтр ненужных строк
//            if(listIsContains(exclusionList,first)){
//                continue;
//            }
//
//            if((cellList.size() == 1)){
//                paymentList.addDataToTableMap(currentChartType,currentChartData);
//                currentChartData = new LinkedList<>();
//                currentChartType = first;
//                continue;
//            }
//            if(!arrayElementIsMathes(cellList,"\\d{2}\\.\\d{2}\\.\\d{4}",0)){
//                continue;
//            }
//            currentChartData.add(cellList);
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



    /**
     * Получение списка игнорируемых строк.
     * @return список игнорируемых строк
     */
    private List<String> exclusionList(){
        return Arrays.asList(
                "",
                "ФІЛІЯ.+",
                "Перелік.+",
                "Назва.+",
                "Номер.+",
                "Сальдо.+",
                "\\W",
//                "Дата",
                "Розрахунковий рахунок.+",
//                "Всього.+",
                "В т\\.ч.+",
                "в т\\.ч.+",
                "ПДВ",
                "Разом",
                "За додатковi.+");
    }

    private int getListNumb(String string) {
        Pattern pattern = Pattern.compile("\\d{8}");
        Matcher m = pattern.matcher(string);
        if (m.find()) {
            return Integer.parseInt(m.group());
        }
        return -1;
    }


    private Date getListDate(String string) {
        Pattern pattern = Pattern.compile("\\d{2}\\.\\d{2}\\.\\d{2}");
        Matcher m = pattern.matcher(string);

        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");

        if (m.find()) {
            try{
                return format.parse(m.group());
            }catch (ParseException e){
                throw new RuntimeException("Ошибка получения даты перечня");
            }

        }
        return new Date();
    }
}
