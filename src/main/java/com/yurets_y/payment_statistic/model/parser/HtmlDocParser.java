package com.yurets_y.payment_statistic.model.parser;

import com.yurets_y.payment_statistic.model.entity.PaymentList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HtmlDocParser implements DocParser {
    @Override
    public PaymentList parseFromFile(File file) throws IOException {
        Document document = Jsoup.parse(file, "UTF-8");

        if(!file.getName().toLowerCase().endsWith(".html")){
            throw new IOException("Неизвесный формат файла!!!");
        }
        PaymentList paymentList = getPaymentList(document);
        paymentList.setBackupFile(file);
        return paymentList;

    }

    private PaymentList getPaymentList(Document document) {
        return parseFromJsout(document);
    }

    private PaymentList parseFromJsout(Document document) {
        PaymentList paymentList = new PaymentList();



        return paymentList;
    }
}
