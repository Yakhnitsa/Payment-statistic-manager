package com.yurets_y.payment_statistic.model.parser;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Import(TestConfig.class)
@RunWith(SpringRunner.class)
@SpringBootTest
public class HtmlDocParserTest {

    @Autowired
    private DocParser docParser;

    @Autowired
    private DocumentParser documentParser;

    @Test
    public void getNumberAndDateTest(){
        System.out.println("Parser test");
    }
    @Test
    public void resourceIntegrationTest(){
        assertNotNull(docParser);
    }

}
