package com.yurets_y.payment_statistic.model.elements;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Admin on 20.02.2018.
 */
public class PaymentList {

    private String listName;
    private int listNumber;
    private int paymentCode;
    private Map<String, List<List<String>>> tableMap;
    private double openingBalance;
    private double closingBalance;
    private double totalCosts;
    private double payment;
    private File file;

    public PaymentList() {
        tableMap = new LinkedHashMap<>();
    }

    public String getListName() {
        return listName;
    }

    public int getListNumber() {
        return listNumber;
    }

    public int getPaymentCode() {
        return paymentCode;
    }

    public void setPaymentCode(int paymentCode) {
        if (paymentCode > 1) {
            this.paymentCode = paymentCode;
        }
    }

    public void setListName(String listName) {
        this.listName = listName;
        this.listNumber = getListNumbFromName(listName);
    }

    public void addDataToTableMap(String tableName, List<List<String>> tableData) {
        tableMap.put(tableName, tableData);
    }

    public double getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(double openingBalance) {
        if (openingBalance == -1) return;
        this.openingBalance = openingBalance;
    }

    public double getClosingBalance() {
        return closingBalance;
    }

    public void setClosingBalance(double closingBalance) {
        if (closingBalance == -1) return;
        this.closingBalance = closingBalance;
    }

    public double getTotalCosts() {
        return totalCosts;
    }

    public double getPayment() {
        return payment;
    }

    public void setPayment(double payment) {
        this.payment = payment;
    }

    public void setTotalCosts(double totalCosts) {
        if (totalCosts == -1) return;
        this.totalCosts = totalCosts;
    }

    public Map<String, List<List<String>>> getTableMap() {
        return Collections.unmodifiableMap(tableMap);
    }

    private int getListNumbFromName(String name) {
        Pattern pattern = Pattern.compile("\\d{8}");
        Matcher m = pattern.matcher(name);
        if (m.find()) {
            return Integer.parseInt(m.group());
        }
        return -1;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentList that = (PaymentList) o;
        return listNumber == that.listNumber &&
                paymentCode == that.paymentCode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(listNumber, paymentCode);
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
