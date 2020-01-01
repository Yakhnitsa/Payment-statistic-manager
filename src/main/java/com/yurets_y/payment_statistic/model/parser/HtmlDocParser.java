package com.yurets_y.payment_statistic.model.parser;

import com.yurets_y.payment_statistic.model.entity.PaymentDetails;
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
    final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
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

            String paymentCodePattern = "Код платника:(\\d*)";
            if(cellList.size() >=2 && cellList.get(1).matches(paymentCodePattern)){
                paymentList.setPaymentCode((int) getLongFromPattern(cellList.get(1),paymentCodePattern));
            }

            String openBalancePattern = "Сальдо на початок.+:.+?(\\d+[,.]\\d+)";
            if(cellList.size() >1 && cellList.get(1).matches(openBalancePattern)){
                paymentList.setOpeningBalance(getLongFromPattern(cellList.get(1),openBalancePattern));

            }

//            if(first.matches("Вiдомостi плати за користування вагонами")){
//                List<List<String>> table = getTable(stringIterator);
//                table.forEach(System.out::println);
//            }

            String numberPattern = "-?(\\d+[,.]\\d+)";
            if(cellList.size() >= 4 && cellList.get(2).matches("Сальдо на кінець.+")){
                paymentList.setClosingBalance(getLongFromPattern(cellList.get(3),numberPattern));
            }
            if(cellList.size() >= 2){
                if(first.matches("Разом")){
                    paymentList.setPaymentVsTaxes(getLongFromPattern(cellList.get(1),numberPattern));
                }
                else if(first.matches("Всього проведено платежів")){
                    paymentList.setPayments(getLongFromPattern(cellList.get(1),numberPattern));
                }else if(first.matches("ПДВ")){
                    paymentList.setPaymentTaxes(getLongFromPattern(cellList.get(1),numberPattern));
                }

            }
            addDepartureList(paymentList,first,stringIterator);

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

    private int getPaymentCode(List<String> chartRow){
        if(chartRow.size()>1){
            Matcher matcher = Pattern
                    .compile("Код платника:(\\d*)")
                    .matcher(chartRow.get(1));
            if(matcher.matches()){
                return Integer.parseInt(matcher.group(1));
            }
        }
        return -1;
    }

    private long getLongFromPattern(String matchedString,String stringPattern){
        Pattern pattern = Pattern.compile(stringPattern);
        Matcher matcher = pattern.matcher(matchedString);
        if(matcher.matches()){
            String numbString = matcher.group(1).replaceAll("[,.]","");
            return Long.parseLong(numbString);
        }

        return -1L;
    }

    private List<List<String>> getTable(Iterator<Element> iterator) {
        List<List<String>> table = new ArrayList<>();
        List<String> row = parseChartRow(iterator.next());
        if (row.size() < 1) return table;
        while (true) {
            table.add(row);
            row = parseChartRow(iterator.next());
            if(row.size()>= 5 && (row.get(4).equals("Всього")||row.get(3).equals("Всього"))) return table;
            if (row.size() < 1) return table;

        }
    }
    private void addDepartureList(PaymentList paymentList,String type,Iterator<Element> iterator){
        if(type.matches("Вiдправлення.?") || type.matches("Прибуття.*")){
            List<String> row = parseChartRow(iterator.next());
            if(row.size() < 1 ) return;
            while (true) {
                try{
                    if (row.get(0).matches("Дата")) {
                        row = parseChartRow(iterator.next());
                        continue;
                    }
                    if(row.get(4).equals("Всього")) return;
                    PaymentDetails pd = new PaymentDetails();
                    pd.setPaymentList(paymentList);
                    pd.setDate(DATE_FORMAT.parse(row.get(0)));
                    pd.setStationCode(Integer.parseInt(row.get(1)));
                    pd.setStationName(row.get(2));
                    pd.setDocumentNumber(row.get(3));

                }catch (ParseException e){
                    throw new RuntimeException("Ошибка парсинга строки " + row.toString());
                }

            }
        }
    }
    static enum TableTypes{
        DEPARTURE(""),
        INTERNATIONAL_DEPARTURE(""),
        ARRIVAL(""),
        STATEMENTS(""),
        FUNDED(""),
        PAYMENTS("");
        private String name;
        TableTypes(String name){
            this.name = name;
        }


        public String getName(){
            return name;
        }
    }

}


