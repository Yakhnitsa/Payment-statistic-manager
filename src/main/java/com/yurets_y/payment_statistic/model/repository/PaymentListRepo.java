package com.yurets_y.payment_statistic.model.repository;

import com.yurets_y.payment_statistic.model.entity.PaymentList;

import java.util.List;

public interface PaymentListRepo {
    void add(PaymentList paymentList);
    void update(PaymentList paymentList);
    void remove(PaymentList paymentList);
    void getById(Long id);
    List<PaymentList> getAll();

//    List<PaymentList> getAll(Specification<PaymentList> specification);


}
