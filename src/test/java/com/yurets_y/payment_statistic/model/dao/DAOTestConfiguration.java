package com.yurets_y.payment_statistic.model.dao;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.File;
import java.io.FileNotFoundException;

@Configuration
@ComponentScan(basePackages = "com.yurets_y.payment_statistic.model")
public class DAOTestConfiguration {
//
//    @Resource(name="paymentListDao")
//    PaymentListDAO paymentListDAO;
    private File testFile;


    @Bean
    public EntityManagerFactory entityManagerFactory(){
        return Persistence.createEntityManagerFactory("in-memory-test-persistence");
    }

    @Bean
    public PaymentListDAO paymentListDAO(){
        return new PaymentListDAO(entityManagerFactory());
    }

    private File getTestFile() {
        if (testFile != null) return testFile;
        testFile = new File("src/test/resources/testList/20122019_040318.html");

        return testFile;
    }

    @Bean(name="testFile")
    public File testFile() {
        File file = new File("src/test/resources/testList/20122019_040318.html");
        if(!file.exists()) throw new RuntimeException("Тестовый файл не найден");
        return file;

    }

    @Bean(name="testDir")
    public File testDir() {

        File dir = new File("src/test/resources/testList");
        if(!dir.exists() && !dir.isDirectory()) throw new RuntimeException("Путь не является директорией, или не существует");

        return dir;
    }

}
