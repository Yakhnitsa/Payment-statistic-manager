package com.yurets_y.payment_statistic.model.parser;

import com.yurets_y.payment_statistic.model.entity.PaymentList;

import java.io.File;
import java.io.IOException;

public interface DocParser {

    PaymentList parseFromFile(File file) throws IOException;

}
