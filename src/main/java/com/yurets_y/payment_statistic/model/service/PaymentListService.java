package com.yurets_y.payment_statistic.model.service;
import  com.yurets_y.payment_statistic.model.entity.PaymentList;

public interface PaymentListService {

    boolean save(PaymentList paymentList);

    boolean update(PaymentList paymentList);

    boolean delete(PaymentList paymentList);

    PaymentList getById(Long id);


}
