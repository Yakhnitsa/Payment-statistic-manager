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
    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    private final String NUMBER_PATTERN = "(-?\\d+[,.]\\d+)";
    private final String LIST_DATE_PATTERN = "\\d{2}\\.\\d{2}\\.\\d{2}";
    private final String LIST_NUMBER_PATTERN = "\\d{8}";

    @Override
    public PaymentList parseFromFile(File file) throws IOException {
        Document document = Jsoup.parse(file, "UTF-8");

        if (!file.getName().toLowerCase().endsWith(".html")) {
            throw new IOException("Неизвесный формат файла!!!");
        }
        PaymentList paymentList = parseFromJsoup(document);
        paymentList.setBackupFile(file);
        return paymentList;

    }

    private PaymentList parseFromJsoup(Document document) {
        PaymentList paymentList = new PaymentList();
        Iterator<Element> stringIterator = document.select("tr").iterator();

        while (stringIterator.hasNext()) {
            List<String> cellList = parseChartRow(stringIterator.next());

            if (cellList.size() <= 0) {
                continue;
            }
            String first = cellList.get(0);
            if (first.contains("Перелік")) {
                paymentList.setNumber(getListNumb(first));
                paymentList.setDate(getListDate(first));
            }

            String paymentCodePattern = "Код платника:(\\d*)";
            if (cellList.size() >= 2 && cellList.get(1).matches(paymentCodePattern)) {
                paymentList.setPaymentCode((int) getLongFromPattern(cellList.get(1), paymentCodePattern));
            }

            String openBalancePattern = "Сальдо на початок.+:.+?(-?\\d+[,.]\\d+)";
            if (cellList.size() > 1 && cellList.get(1).matches(openBalancePattern)) {
                paymentList.setOpeningBalance(-getLongFromPattern(cellList.get(1), openBalancePattern));

            }


            if (cellList.size() >= 4 && cellList.get(2).matches("Сальдо на кінець.+")) {
                paymentList.setClosingBalance(-getLongFromPattern(cellList.get(3), NUMBER_PATTERN));
            }
            if (cellList.size() >= 2) {
                if (first.matches("Разом")) {
                    paymentList.setPaymentVsTaxes(getLongFromPattern(cellList.get(1), NUMBER_PATTERN));
                } else if (first.matches("Всього проведено платежів")) {
                    paymentList.setPayments(getLongFromPattern(cellList.get(1), NUMBER_PATTERN));
                } else if (first.matches("ПДВ")) {
                    paymentList.setPaymentTaxes(getLongFromPattern(cellList.get(1), NUMBER_PATTERN));
                }

            }
            List<PaymentDetails> pdList = getPaymentDetailsByType(first, stringIterator);
            paymentList.addAll(pdList);
        }

        if (!checkSumTest(paymentList)) {
            throw new RuntimeException("Ошибка контрольной суммы для перечня " + paymentList.getNumber());
        }
        return paymentList;
    }

    private List<String> parseChartRow(Element tableString) {

        List<String> cellList = new ArrayList<String>();
        Iterator<Element> cellIterator = tableString.select("th").iterator();
        while (cellIterator.hasNext()) {
            cellList.add(cellIterator.next().text());
        }

        cellIterator = tableString.select("tcol").iterator();
        while (cellIterator.hasNext()) {
            cellList.add(cellIterator.next().text());
        }
        return cellList;
    }


    private int getListNumb(String string) {
        Pattern pattern = Pattern.compile(LIST_NUMBER_PATTERN);
        Matcher m = pattern.matcher(string);
        if (m.find()) {
            return Integer.parseInt(m.group());
        }
        return -1;
    }

    private Date getListDate(String string) {
        Pattern pattern = Pattern.compile(LIST_DATE_PATTERN);
        Matcher m = pattern.matcher(string);

        if (m.find()) {
            try {
                return DATE_FORMAT.parse(m.group());
            } catch (ParseException e) {
                throw new RuntimeException("Ошибка получения даты перечня");
            }

        }
        return new Date();
    }

    private long getLongFromPattern(String matchedString, String stringPattern) {
        Pattern pattern = Pattern.compile(stringPattern);
        Matcher matcher = pattern.matcher(matchedString);
        if (matcher.matches()) {
            String numbString = matcher.group(1).replaceAll("[,.]", "");
            return Long.parseLong(numbString);
        }

        return -1L;
    }

    private List<PaymentDetails> getPaymentDetailsByType(String type, Iterator<Element> iterator) {
        switch (type) {
            case "Вiдправлення":
            case "Вiдправлення - мiжнародне сполучення":
            case "Прибуття":
            case "Прибуття - імпорт":
                return getTransportPayments(type, iterator);
            case "Вiдомостi плати за користування вагонами":
            case "Накопичувальні карточки":
            case "Коригування сум нарахованих платежів минулі періоди":
            case "Коригування сум нарахованих платежів":
                return getStationPayments(type, iterator);
            case "Платіжні доручення":
                return getPayments(type, iterator);
            default:
                return new ArrayList<>();
        }
    }

    private List<PaymentDetails> getTransportPayments(String type, Iterator<Element> iterator) {
        List<String> row = parseChartRow(iterator.next());
        List<PaymentDetails> paymentDetailsList = new ArrayList<>();
        if (row.size() < 1) return paymentDetailsList;
        while (iterator.hasNext()) {
            try {
                if (row.get(0).matches("Дата")) {
                    row = parseChartRow(iterator.next());
                    continue;
                }
                if (row.get(4).equals("Всього")) return paymentDetailsList;
                PaymentDetails pd = new PaymentDetails();
                pd.setType(type);
                pd.setDate(DATE_FORMAT.parse(row.get(0)));
                pd.setStationCode(Integer.parseInt(row.get(1)));
                pd.setStationName(row.get(2));
                pd.setDocumentNumber(row.get(3));
                pd.setPayment(getLongFromPattern(row.get(4), NUMBER_PATTERN));
                pd.setAdditionalPayment(getLongFromPattern(row.get(5), NUMBER_PATTERN));
                pd.setTaxPayment(getLongFromPattern(row.get(6), NUMBER_PATTERN));
                pd.setTotalPayment(getLongFromPattern(row.get(7), NUMBER_PATTERN));

                paymentDetailsList.add(pd);
                row = parseChartRow(iterator.next());

            } catch (ParseException e) {
                throw new RuntimeException("Ошибка парсинга строки " + row.toString());
            }
        }
        return paymentDetailsList;
    }

    private List<PaymentDetails> getStationPayments(String type, Iterator<Element> iterator) {
        List<String> row = parseChartRow(iterator.next());
        List<PaymentDetails> paymentDetailsList = new ArrayList<>();
        if (row.size() < 1) return paymentDetailsList;
        while (iterator.hasNext()) {
            try {
                if (row.get(0).matches("Дата")) {
                    row = parseChartRow(iterator.next());
                    continue;
                }
                if (row.get(3).equals("Всього")) return paymentDetailsList;
                PaymentDetails pd = new PaymentDetails();
                pd.setType(type);
                pd.setDate(DATE_FORMAT.parse(row.get(0)));
                pd.setStationCode(Integer.parseInt(row.get(1)));
                pd.setStationName(row.get(2));
                pd.setDocumentNumber(row.get(3));
                pd.setPaymentCode(row.get(4));
                pd.setPaymentDescription(row.get(5));
                pd.setPayment(getLongFromPattern(row.get(6), NUMBER_PATTERN));
                pd.setTaxPayment(getLongFromPattern(row.get(7), NUMBER_PATTERN));
                pd.setTotalPayment(getLongFromPattern(row.get(8), NUMBER_PATTERN));

                paymentDetailsList.add(pd);
                row = parseChartRow(iterator.next());

            } catch (ParseException e) {
                throw new RuntimeException("Ошибка парсинга строки " + row.toString());
            }
        }
        return paymentDetailsList;
    }

    private List<PaymentDetails> getPayments(String type, Iterator<Element> iterator) {
        List<String> row = parseChartRow(iterator.next());
        List<PaymentDetails> paymentDetailsList = new ArrayList<>();
        if (row.size() < 1) return paymentDetailsList;
        while (iterator.hasNext()) {
            try {
                if (row.get(0).matches("Дата")) {
                    row = parseChartRow(iterator.next());
                    continue;
                }
                if (row.get(2).equals("Всього")) return paymentDetailsList;
                PaymentDetails pd = new PaymentDetails();
                pd.setType(type);
                pd.setDate(DATE_FORMAT.parse(row.get(0)));
                pd.setDocumentNumber(row.get(1));
                pd.setPaymentCode(row.get(2));
                pd.setTotalPayment(getLongFromPattern(row.get(3), NUMBER_PATTERN));
                paymentDetailsList.add(pd);
                row = parseChartRow(iterator.next());

            } catch (ParseException e) {
                throw new RuntimeException("Ошибка парсинга строки " + row.toString());
            }
        }
        return paymentDetailsList;
    }

    private boolean checkSumTest(PaymentList paymentList) {
        long openingBalance = paymentList.getOpeningBalance();
        long closingBalance = paymentList.getClosingBalance();
        long payments = paymentList.getPaymentDetailsList()
                .stream()
                .filter(paymentDetails -> paymentDetails.getType().equals("Платіжні доручення"))
                .mapToLong(PaymentDetails::getTotalPayment).sum();
        long totalPaymentsVsTaxes = paymentList.getPaymentVsTaxes();
        long totalPaymentsFromList = paymentList.getPaymentDetailsList()
                .stream()
                .filter(paymentDetails -> !paymentDetails.getType().equals("Платіжні доручення"))
                .mapToLong(PaymentDetails::getTotalPayment).sum();

        long checkSum = openingBalance + payments - totalPaymentsFromList;

        return (checkSum == closingBalance) && (totalPaymentsVsTaxes == totalPaymentsFromList);
    }

}


