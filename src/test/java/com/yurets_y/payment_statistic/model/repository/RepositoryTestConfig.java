package com.yurets_y.payment_statistic.model.repository;

import com.yurets_y.payment_statistic.model.parser.DocParser;
import com.yurets_y.payment_statistic.model.parser.HtmlDocParser;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

@TestConfiguration
public class RepositoryTestConfig {

        @Bean
        public DocParser getDocParser() {

            return new HtmlDocParser();
        }
        @Bean
        public PaymentListRepo getPaymentListRepo(){
            return new PaymentListRepoImpl(entityManager());
        }

        @Bean
        public EntityManager entityManager(){
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("test-persistence");
            return emf.createEntityManager();
        }

}
