package com.yurets_y.payment_statistic.model.entity;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;

import javax.persistence.*;

@Entity
@IdClass(PaymentListId.class)
@Table(name = "PAYMENT_LIST")
public class PaymentList {
    private Long id;
    @Id
    private int number;
    @Id
    private int payerCode;

    @Temporal(TemporalType.DATE)
    private Date date;
    private int taxCode;
    private String payerName;
    private String contractNumber;

    private long openingBalance;
    private long closingBalance;

    private long payments;
    private long paymentTaxes;
    private long paymentVsTaxes;

    @OneToMany(mappedBy="paymentList", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<PaymentDetails> paymentDetailsList;

    private File backupFile;
    {
        paymentDetailsList = new ArrayList<>();
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getPayerCode() {
        return payerCode;
    }

    public void setPayerCode(int paymentCode) {
        this.payerCode = paymentCode;
    }

    public int getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(int taxCode) {
        this.taxCode = taxCode;
    }

    public long getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(long openingBalance) {
        this.openingBalance = openingBalance;
    }

    public long getClosingBalance() {
        return closingBalance;
    }

    public void setClosingBalance(long closingBalance) {
        this.closingBalance = closingBalance;
    }

    public long getPayments() {
        return payments;
    }

    public void setPayments(long payments) {
        this.payments = payments;
    }

    public long getPaymentTaxes() {
        return paymentTaxes;
    }

    public void setPaymentTaxes(long paymentTaxes) {
        this.paymentTaxes = paymentTaxes;
    }

    public long getPaymentVsTaxes() {
        return paymentVsTaxes;
    }

    public void setPaymentVsTaxes(long paymentVsTaxes) {
        this.paymentVsTaxes = paymentVsTaxes;
    }

    public List<PaymentDetails> getPaymentDetailsList() {
        return paymentDetailsList;
    }

    public void setPaymentDetailsList(List<PaymentDetails> paymentDetailsList) {
        this.paymentDetailsList = paymentDetailsList;
    }

    public File getBackupFile() {
        return backupFile;
    }

    public void setBackupFile(File backupFile) {
        this.backupFile = backupFile;
    }

    public boolean containsDetail(Object o) {
        return paymentDetailsList.contains(o);
    }

    public Iterator<PaymentDetails> detailsIterator() {
        return paymentDetailsList.iterator();
    }

    public boolean addDetail(PaymentDetails paymentDetails) {
        paymentDetails.setPaymentList(this);
        return paymentDetailsList.add(paymentDetails);
    }

    public boolean removeDetail(Object o) {
        return paymentDetailsList.remove(o);
    }

    public boolean addAll(Collection<? extends PaymentDetails> c) {
        c.forEach(pl -> pl.setPaymentList(this));
        return paymentDetailsList.addAll(c);
    }

    public void forEach(Consumer<? super PaymentDetails> action) {
        paymentDetailsList.forEach(action);
    }
}
