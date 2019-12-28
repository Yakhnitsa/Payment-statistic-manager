package com.yurets_y.payment_statistic.model.parser;

import com.yurets_y.payment_statistic.model.entity.PaymentList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestConfig.class)
@RunWith(SpringRunner.class)
public class HtmlDocParserTest {

    @Autowired
    private DocParser docParser;

    private File testFile;


    @Test
    public void resourceIntegrationTest(){
        assertThat(this.docParser).isNotNull();
    }

    @Test
    public void testFileExists(){
        assertThat(getTestFile()).exists();
    }

    @Test
    public void parseDocDateAndNumberTest() throws IOException {
        File file = getTestFile();
        PaymentList paymentList = docParser.parseFromFile(file);
        assertThat(paymentList.getNumber()).isEqualTo(20191219);
        assertThat(paymentList.getPaymentCode()).isEqualTo(8210260);
    }

    @Test
    public void parsePaymentCodeTest() throws IOException {
        File file = getTestFile();
        PaymentList paymentList = docParser.parseFromFile(file);
        assertThat(paymentList.getPaymentCode()).isEqualTo(8210260);
    }
    @Test
    public void parseOpeningBalanseTest() throws IOException {
        File file = getTestFile();
        PaymentList paymentList = docParser.parseFromFile(file);
        assertThat(paymentList.getOpeningBalance()).isEqualTo(428764838);
    }

    private File getTestFile(){
        if(testFile != null) return testFile;
        testFile = new File("src/test/resources/testList/20122019_040318.html");

        return testFile;
    }



}
