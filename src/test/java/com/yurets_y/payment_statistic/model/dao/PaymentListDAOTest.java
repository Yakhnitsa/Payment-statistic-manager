package com.yurets_y.payment_statistic.model.dao;


import com.yurets_y.payment_statistic.model.entity.PaymentDetails;
import com.yurets_y.payment_statistic.model.entity.PaymentList;
import com.yurets_y.payment_statistic.model.parser.DocParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@Import(DAOTestConfiguration.class)
@RunWith(SpringRunner.class)
public class PaymentListDAOTest {

    @Resource(name="dao-vs-in-memory-db")
    private PaymentListDAO paymentListDAOInMemory;

    @Resource(name="dao-vs-immutable-db")
    private PaymentListDAO paymentListDAOImmutable;

    @Resource(name="htmlDocParser")
    private DocParser docParser;

    @Resource(name="testFile")
    private File testFile;

    @Resource(name="testDir")
    private File testDir;

    @Resource(name="backupDir")
    private File backupDir;


    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Before
    public void loadDatabase(){

//        for(File file: testDir.listFiles()){
//            try {
//                paymentListDAO.add(docParser.parseFromFile(file));
//                System.out.println(file);
//            } catch (IOException e) {
//                throw new RuntimeException("Ошибка парсинга файла " + file);
//            }
//        }

    }

    @After
    public void clearDatabase(){

    }

    @Test
    public void autowireComponentTest(){
        assertThat(this.paymentListDAOInMemory).isNotNull();
    }

    @Test
    public void testFilesExist(){
        assertThat(testFile.exists()).isTrue();
        assertThat(testDir.exists()).isTrue();
    }

    @Test
    public void backupDirTest(){
        assertThat(backupDir.exists()).isTrue();
    }

    @Test
    public void saveToDBTest() throws IOException {
        PaymentList paymentList = docParser.parseFromFile(testFile);
        paymentListDAOInMemory.add(paymentList);
    }

    @Test
    public void loadFromDBTest(){
        List<PaymentList> paymentLists = paymentListDAOImmutable.getAll();
        assertThat(paymentLists.size()).isEqualTo(12);
        paymentLists.forEach(pl -> {
            assertThat(pl.getBackupFile().exists()).isTrue();
            System.out.println(pl.getBackupFile().getAbsolutePath());
        });
    }

    @Test
    public void filterByDateTest() throws ParseException {

        Date dateFrom = dateFormat.parse("2020-01-04");
        Date dateUntil = dateFormat.parse("2020-01-10");
        List<PaymentList> filteredList = paymentListDAOImmutable.getByPeriod(dateFrom,dateUntil);
        assertThat(filteredList.size()).isEqualTo(7);

    }

    @Test
    public void getDetailsByStationCodeTest(){
        int stationCode = 325104;
        List<PaymentDetails> testList = paymentListDAOImmutable.getPaymentDetailsByStationCode(stationCode);
        Long sum = testList.stream()
                .mapToLong(PaymentDetails::getTotalPayment)
                .sum();

        System.out.println(sum);
        Map<String,Long> resultMap = testList.stream()
                .collect(Collectors.groupingBy(PaymentDetails::getType,Collectors.summingLong(PaymentDetails::getTotalPayment)));
        resultMap.forEach((c,v) -> System.out.println(c + " - " + v));
    }

    @Test
    public void getDetailsByDateTest() throws ParseException{
        Date dateFrom = dateFormat.parse("2020-01-05");
        Date dateUntil = dateFormat.parse("2020-01-09");
        List<PaymentDetails> testList = paymentListDAOImmutable.getPaymentDetailsByDate(dateFrom,dateUntil);

        testList.forEach(detail ->{
            if(detail.getStationName() == null) detail.setStationName("other");
        });

        Map<Date,Map<String,List<PaymentDetails>>> resultMap = testList.stream()
                .collect(Collectors.groupingBy(PaymentDetails::getDate,
                        Collectors.groupingBy(PaymentDetails::getStationName)));

        resultMap.forEach((date,map) ->{
            System.out.println(date);
            map.forEach((station,list)->{
                System.out.println("   " + station);
                list.forEach(detail ->{
                    System.out.println("      " + detail.getType() + " - " + detail.getTotalPayment());
                        }
                );
            });
        });

    }

    @Test
    public void simpleTest() throws IOException{
    }


}
