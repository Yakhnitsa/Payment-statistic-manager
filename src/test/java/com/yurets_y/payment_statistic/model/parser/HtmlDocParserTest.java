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


    @Test
    public void resourceIntegrationTest(){
        assertThat(this.docParser).isNotNull();
    }
    @Test
    public void parseDocDateAndNumber() throws IOException {
        File file = getTestFile();
        PaymentList paymentList = docParser.parseFromFile(file);

        System.out.println(paymentList.getDate());
    }

    public File getTestFile(){
        File testFile = new File("src/test/resources/testList/20122019_040318.html");

        return testFile;
    }
    @Test
    public void testFileExists(){
        assertThat(getTestFile()).exists();
    }


}
