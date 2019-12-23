package com.yurets_y.payment_statistic.model.saver;



import com.yurets_y.payment_statistic.model.elements.PaymentList;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface DocumentSaver {
    void saveDocument(PaymentList payList, File file) throws IOException;

    void saveDocumentGroupToFile(List<PaymentList> paymentLists, File file) throws IOException;
}
