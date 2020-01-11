package com.yurets_y.payment_statistic.model.repository;


import com.yurets_y.payment_statistic.model.entity.PaymentList;
import com.yurets_y.payment_statistic.model.parser.DocParser;
import com.yurets_y.payment_statistic.model.parser.TestConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@Import(RepositoryTestConfig.class)
@RunWith(SpringRunner.class)
public class PaymentListRepoTest {

    @Autowired
    private DocParser docParser;

    @Autowired
    private PaymentListRepo paymentListRepo;

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
    public void saveToRepoTest() throws IOException {
        File testFile = getTestFile();
        PaymentList paymentList = docParser.parseFromFile(testFile);
        paymentListRepo.add(paymentList);


    }





    private File getTestFile(){
        if(testFile != null) return testFile;
        testFile = new File("src/test/resources/testList/20122019_040318.html");

        return testFile;
    }
}
