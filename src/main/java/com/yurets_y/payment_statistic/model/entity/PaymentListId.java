package com.yurets_y.payment_statistic.model.entity;

import java.io.Serializable;
import java.util.Objects;

public class PaymentListId implements Serializable {
    private int payerCode;
    private int number;

    public PaymentListId() {
    }

    public PaymentListId(int payerCode, int number) {
        this.payerCode = payerCode;
        this.number = number;
    }

    public int getPayerCode() {
        return payerCode;
    }

    public void setPayerCode(int paymentCode) {
        this.payerCode = paymentCode;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentListId that = (PaymentListId) o;
        return payerCode == that.payerCode &&
                number == that.number;
    }

    @Override
    public int hashCode() {

        return Objects.hash(payerCode, number);
    }
}
