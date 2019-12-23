package com.yurets_y.payment_statistic.model.parser;

import com.yurets_y.payment_statistic.model.elements.PaymentList;

import java.io.File;
import java.io.IOException;

public interface DocumentParser {
    PaymentList parseFromURI(String uri) throws IOException;

    PaymentList parseFromFile(File file) throws IOException;
}
