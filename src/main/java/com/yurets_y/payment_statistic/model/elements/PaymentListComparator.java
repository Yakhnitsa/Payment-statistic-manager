package com.yurets_y.payment_statistic.model.elements;


import java.util.Comparator;

/**
 * Created by Admin on 11.04.2018.
 */
public class PaymentListComparator implements Comparator<PaymentList> {

    @Override
    public int compare(PaymentList o1, PaymentList o2) {
        return Integer.compare(o1.getListNumber(),o2.getListNumber());
    }

    @Override
    public Comparator<PaymentList> reversed() {
        return new Comparator<PaymentList>() {
            @Override
            public int compare(PaymentList o1, PaymentList o2) {
                return Integer.compare(o2.getListNumber(),o1.getListNumber());
            }
        };
    }
}
