package com.yurets_y.payment_statistic.model.parser;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {

        @Bean
        public DocParser getDocParser() {

            return new HtmlDocParser();
        }

        @Bean
        public DocumentParser documentParser(){
            return new DocumentParserBaseHTML();
        }
}
