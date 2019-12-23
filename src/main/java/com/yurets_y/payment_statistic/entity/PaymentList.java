package com.yurets_y.payment_statistic.entity;

import java.util.Date;
import java.util.List;

public class PaymentList {
    private Long id;
    private int number;
    private Date date;
    private int paymentCode;
    private int taxCode;
    private String payerName;
    private String contractNumber;

    private long openingBalance;
    private long closingBalance;

    private long payments;
    private long paymentTaxes;
    private long paymentVsTaxes;

    private List<PaymentDetails> paymentDetailsList;


}
