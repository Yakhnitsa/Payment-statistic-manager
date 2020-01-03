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
    final String NUMBER_PATTERN = "-?(\\d+[,.]\\d+)";
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

            if(cellList.size() >= 4 && cellList.get(2).matches("Сальдо на кінець.+")){
                paymentList.setClosingBalance(getLongFromPattern(cellList.get(3),NUMBER_PATTERN));
            }
            if(cellList.size() >= 2){
                if(first.matches("Разом")){
                    paymentList.setPaymentVsTaxes(getLongFromPattern(cellList.get(1),NUMBER_PATTERN));
                }
                else if(first.matches("Всього проведено платежів")){
                    paymentList.setPayments(getLongFromPattern(cellList.get(1),NUMBER_PATTERN));
                }else if(first.matches("ПДВ")){
                    paymentList.setPaymentTaxes(getLongFromPattern(cellList.get(1),NUMBER_PATTERN));
                }

            }
            List<PaymentDetails> pdList = getPaymentDetailsByType(first,stringIterator);
            paymentList.addAll(pdList);

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

        if (m.find()) {
            try{
                return DATE_FORMAT.parse(m.group());
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
    private List<PaymentDetails> getPaymentDetailsByType(String type, Iterator<Element> iterator){
        switch (type){
            case "Вiдправлення":
            case "Вiдправлення - мiжнародне сполучення":
            case "Прибуття":
                return getTransportPayments(type, iterator);
            case "Вiдомостi плати за користування вагонами":
            case "Накопичувальні карточки":
                return getStationPayments(type,iterator);
            case "Платіжні доручення":
                return getPayments(type,iterator);
            default:
                return new ArrayList<>();
        }
//        if(type.matches("Вiдправлення.*?") || type.matches("Прибуття.*")){
//            List<String> row = parseChartRow(iterator.next());
//            if(row.size() < 1 ) return;
//            while (iterator.hasNext()) {
//                try{
//                    if (row.get(0).matches("Дата")) {
//                        row = parseChartRow(iterator.next());
//                        continue;
//                    }
//                    if(row.get(4).equals("Всього")) return;
//                    PaymentDetails pd = new PaymentDetails();
//                    pd.setType(type);
//                    pd.setPaymentList(paymentList);
//                    pd.setDate(DATE_FORMAT.parse(row.get(0)));
//                    pd.setStationCode(Integer.parseInt(row.get(1)));
//                    pd.setStationName(row.get(2));
//                    pd.setDocumentNumber(row.get(3));
//                    pd.setPayment(getLongFromPattern(row.get(4),NUMBER_PATTERN));
//                    pd.setAdditionalPayment(getLongFromPattern(row.get(5),NUMBER_PATTERN));
//                    pd.setTaxPayment(getLongFromPattern(row.get(6),NUMBER_PATTERN));
//                    pd.setTotalPayment(getLongFromPattern(row.get(7),NUMBER_PATTERN));
//
//                    paymentList.addDetail(pd);
//                    row = parseChartRow(iterator.next());
//
//                }catch (ParseException e){
//                    throw new RuntimeException("Ошибка парсинга строки " + row.toString());
//                }
//
//            }
//        }else if(type.matches())
    }

    private List<PaymentDetails> getTransportPayments(String type, Iterator<Element> iterator) {
        List<String> row = parseChartRow(iterator.next());
        List<PaymentDetails> paymentDetailsList = new ArrayList<>();
        if(row.size() < 1 ) return paymentDetailsList;
        while (iterator.hasNext()) {
            try{
                if (row.get(0).matches("Дата")) {
                    row = parseChartRow(iterator.next());
                    continue;
                }
                if(row.get(4).equals("Всього")) return paymentDetailsList;
                PaymentDetails pd = new PaymentDetails();
                pd.setType(type);
                pd.setDate(DATE_FORMAT.parse(row.get(0)));
                pd.setStationCode(Integer.parseInt(row.get(1)));
                pd.setStationName(row.get(2));
                pd.setDocumentNumber(row.get(3));
                pd.setPayment(getLongFromPattern(row.get(4),NUMBER_PATTERN));
                pd.setAdditionalPayment(getLongFromPattern(row.get(5),NUMBER_PATTERN));
                pd.setTaxPayment(getLongFromPattern(row.get(6),NUMBER_PATTERN));
                pd.setTotalPayment(getLongFromPattern(row.get(7),NUMBER_PATTERN));

                paymentDetailsList.add(pd);
                row = parseChartRow(iterator.next());

            }catch (ParseException e){
                throw new RuntimeException("Ошибка парсинга строки " + row.toString());
            }
        }
        return paymentDetailsList;
    }

    private List<PaymentDetails> getStationPayments(String type, Iterator<Element> iterator) {
        List<String> row = parseChartRow(iterator.next());
        List<PaymentDetails> paymentDetailsList = new ArrayList<>();
        if(row.size() < 1 ) return paymentDetailsList;
        while (iterator.hasNext()) {
            try{
                if (row.get(0).matches("Дата")) {
                    row = parseChartRow(iterator.next());
                    continue;
                }
                if(row.get(3).equals("Всього")) return paymentDetailsList;
                PaymentDetails pd = new PaymentDetails();
                pd.setType(type);
                pd.setDate(DATE_FORMAT.parse(row.get(0)));
                pd.setStationCode(Integer.parseInt(row.get(1)));
                pd.setStationName(row.get(2));
                pd.setDocumentNumber(row.get(3));
                pd.setPaymentCode(row.get(4));
                pd.setPaymentDescription(row.get(5));
                pd.setPayment(getLongFromPattern(row.get(6),NUMBER_PATTERN));
                pd.setTaxPayment(getLongFromPattern(row.get(7),NUMBER_PATTERN));
                pd.setTotalPayment(getLongFromPattern(row.get(8),NUMBER_PATTERN));

                paymentDetailsList.add(pd);
                row = parseChartRow(iterator.next());

            }catch (ParseException e){
                throw new RuntimeException("Ошибка парсинга строки " + row.toString());
            }
        }
        return paymentDetailsList;
    }

    private List<PaymentDetails> getPayments(String type, Iterator<Element> iterator) {
        List<String> row = parseChartRow(iterator.next());
        List<PaymentDetails> paymentDetailsList = new ArrayList<>();
        if(row.size() < 1 ) return paymentDetailsList;
        while (iterator.hasNext()) {
            try{
                if (row.get(0).matches("Дата")) {
                    row = parseChartRow(iterator.next());
                    continue;
                }
                if(row.get(2).equals("Всього")) return paymentDetailsList;
                PaymentDetails pd = new PaymentDetails();
                pd.setType(type);
                pd.setDate(DATE_FORMAT.parse(row.get(0)));
                pd.setDocumentNumber(row.get(1));
                pd.setPaymentCode(row.get(2));
                pd.setTotalPayment(getLongFromPattern(row.get(3),NUMBER_PATTERN));
                paymentDetailsList.add(pd);
                row = parseChartRow(iterator.next());

            }catch (ParseException e){
                throw new RuntimeException("Ошибка парсинга строки " + row.toString());
            }
        }
        return paymentDetailsList;
    }
}


