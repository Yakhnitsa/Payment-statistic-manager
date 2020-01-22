package com.yurets_y.payment_statistic.model.parser;

import com.yurets_y.payment_statistic.model.entity.PaymentDetails;
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
        assertThat(paymentList.getPayerCode()).isEqualTo(8210260);
    }

    @Test
    public void parseOpeningBalanceTest() throws IOException {
        File file = getTestFile();
        PaymentList paymentList = docParser.parseFromFile(file);

        assertThat(paymentList.getOpeningBalance()).isEqualTo(428764838);
    }
    @Test
    public void parseClosingBalanceTest() throws IOException {
        File file = getTestFile();
        PaymentList paymentList = docParser.parseFromFile(file);
        assertThat(paymentList.getClosingBalance()).isEqualTo(376652922);
    }
    @Test
    public void totalPaymentAndTaxesTest() throws IOException {
        File file = getTestFile();
        PaymentList paymentList = docParser.parseFromFile(file);
        assertThat(paymentList.getPayments()).isEqualTo(302370890);
        assertThat(paymentList.getPaymentTaxes()).isEqualTo(49741026);
        assertThat(paymentList.getPaymentVsTaxes()).isEqualTo(352111916);
    }

    @Test
    public void parseDeparturePaymentTest() throws IOException{
        File file = getTestFile();
        PaymentList paymentList = docParser.parseFromFile(file);
        long departurePayment = paymentList.getPaymentDetailsList()
                .stream()
                .filter(pd -> pd.getType().equals("Вiдправлення"))
                .mapToLong(PaymentDetails::getPayment).sum();
        long internDeparturePayment = paymentList.getPaymentDetailsList()
                .stream()
                .filter(pd -> pd.getType().equals("Вiдправлення - мiжнародне сполучення"))
                .mapToLong(PaymentDetails::getPayment).sum();
        long deliverTotalPayment = paymentList.getPaymentDetailsList()
                .stream()
                .filter(pd -> pd.getType().equals("Прибуття"))
                .mapToLong(PaymentDetails::getTotalPayment).sum();

        assertThat(departurePayment).isEqualTo(15432010);
        assertThat(internDeparturePayment).isEqualTo(262541800);
        assertThat(deliverTotalPayment).isEqualTo(1533168);
    }

    @Test
    public void parseStationPaymentTest() throws IOException{
        File file = getTestFile();
        PaymentList paymentList = docParser.parseFromFile(file);
        long vagUsagePayment = paymentList.getPaymentDetailsList()
                .stream()
                .filter(pd -> pd.getType().equals("Вiдомостi плати за користування вагонами"))
                .mapToLong(PaymentDetails::getPayment).sum();

        long cumulativeCardsPayment = paymentList.getPaymentDetailsList()
                .stream()
                .filter(pd -> pd.getType().equals("Накопичувальні карточки"))
                .mapToLong(PaymentDetails::getPayment).sum();
        assertThat(vagUsagePayment).isEqualTo(21791830);
        assertThat(cumulativeCardsPayment).isEqualTo(225850);
    }

    @Test
    public void parsePaymentsTest() throws IOException{
        File file = getTestFile();
        PaymentList paymentList = docParser.parseFromFile(file);
        long totalPayment = paymentList.getPaymentDetailsList()
                .stream()
                .filter(pd -> pd.getType().equals("Платіжні доручення"))
                .mapToLong(PaymentDetails::getTotalPayment).sum();
        assertThat(totalPayment).isEqualTo(300000000);
    }

    private File getTestFile(){
        if(testFile != null) return testFile;
        testFile = new File("src/test/resources/testList/20122019_040318.html");

        return testFile;
    }

}
