package com.yurets_y.payment_statistic.model.entity;

import java.util.Date;

public class PaymentDetails {
    private Long id;
    private PaymentList paymentList;

    private String type;
    private Date date;
    private int stationCode;
    private String stationName;
    private String documentNumber;
    private String paymentCode;
    private String paymentDescription;

    private long payment;
    private long additionalPayment;
    private long taxPayment;
    private long totalPayment;

}
