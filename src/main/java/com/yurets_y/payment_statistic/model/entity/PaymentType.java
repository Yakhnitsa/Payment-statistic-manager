package com.yurets_y.payment_statistic.model.entity;

public enum PaymentType {
    DEPARTURE("Вiдправлення"),
    INTERNATIONAL_DEPARTURE("Вiдправлення - мiжнародне сполучення"),
    ARRIVAL("Прибуття"),
    STATEMENTS("Вiдомостi плати за користування вагонами"),
    FUNDED("Накопичувальні карточки"),
    PAYMENTS("Платіжні доручення"),
    REFUND(""),
    OTHER(""),
    ;
    String name;
    String pattern;
    PaymentType(String name){
        this.name = name;
    }
    public static PaymentType getByName(String name){
        for(PaymentType pt : PaymentType.values()){
            if(pt.name.equals(name)) return pt;
        }
        return OTHER;
    }

    public String getName() {
        return name;
    }
}
