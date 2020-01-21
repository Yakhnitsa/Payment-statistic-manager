package com.yurets_y.payment_statistic.model.dao;


import com.yurets_y.payment_statistic.model.entity.PaymentList;
import com.yurets_y.payment_statistic.model.parser.DocParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Import(DAOTestConfiguration.class)
@RunWith(SpringRunner.class)
public class PaymentListDAOTest {
    @Autowired
    private PaymentListDAO paymentListDAO;

    @Resource(name="htmlDocParser")
    private DocParser docParser;

    @Resource(name="testFile")
    private File testFile;

    @Resource(name="testDir")
    private File testDir;

    @Resource(name="backupDir")
    private File backupDir;

    @Before
    public void loadDatabase(){

        for(File file: testDir.listFiles()){
            try {
                paymentListDAO.add(docParser.parseFromFile(file));
                System.out.println(file);
            } catch (IOException e) {
                throw new RuntimeException("Ошибка парсинга файла " + file);
            }
        }

    }

    @After
    public void clearDatabase(){

    }

    @Test
    public void autowireComponentTest(){
        assertThat(this.paymentListDAO).isNotNull();
    }

    @Test
    public void testFilesExist(){
        assertThat(testFile.exists()).isTrue();
        assertThat(testDir.exists()).isTrue();
    }
    @Test
    public void backupDirTest(){
        System.out.println(backupDir.getAbsolutePath());
    }

    @Test
    public void saveToDBTest() throws IOException {
        PaymentList paymentList = docParser.parseFromFile(testFile);
        paymentListDAO.add(paymentList);
    }

    @Test
    public void loadFromDBTest(){
        List<PaymentList> paymentLists = paymentListDAO.getAll();
        assertThat(paymentLists.size()).isEqualTo(12);
        paymentLists.forEach(pl -> {
            assertThat(pl.getBackupFile().exists()).isTrue();
            System.out.println(pl.getBackupFile().getAbsolutePath());
        });
    }

    @Test
    public void filterByDateTest() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date dateFrom = dateFormat.parse("2020-01-04");
        Date dateUntil = dateFormat.parse("2020-01-10");
        List<PaymentList> filteredList = paymentListDAO.getByPeriod(dateFrom,dateUntil);
        assertThat(filteredList.size()).isEqualTo(7);
    }

    @Test
    public void simpleTest() throws IOException{
        System.out.println(File.separator);
    }


}
