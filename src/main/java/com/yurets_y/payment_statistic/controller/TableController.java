package com.yurets_y.payment_statistic.controller;

import com.yurets_y.payment_statistic.model.elements.PaymentList;

import java.util.List;

public interface TableController {

    PaymentList getSelectedItem();

    List<PaymentList> getDocuments();

    List<PaymentList> getSelectedDocuments();

    boolean deleteSelectedDocuments();

    void addDocuments(List<PaymentList> paymentLists);

}
