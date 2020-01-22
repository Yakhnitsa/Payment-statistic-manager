package com.yurets_y.payment_statistic.model.dao;


import com.yurets_y.payment_statistic.model.parser.DocParser;
import com.yurets_y.payment_statistic.model.parser.HtmlDocParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.AliasFor;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.File;
import java.io.FileNotFoundException;

@Configuration
@ComponentScan(basePackages = "com.yurets_y.payment_statistic.model")
@PropertySource("classpath:test.properties")
public class DAOTestConfiguration {
//
//    @Resource(name="paymentListDao")
//    PaymentListDAO paymentListDAO;

    @Value("${model.dao.backup-path}")
    private String backupDir;

    @Value("${test.resources.testDirectory-location}")
    private String testDirectoryLocation;

    @Value("${test.resources.testFile-location}")
    private String testFileLocation;


    @Bean("immutable-test-db")
    public EntityManagerFactory entityManagerFactoryImmutable() {
        return Persistence.createEntityManagerFactory("immutable-test-persistence");
    }

    @Bean(name = {"in-memory-test-db", "defaultEMF"})
    public EntityManagerFactory entityManagerFactoryInMemory() {
        return Persistence.createEntityManagerFactory("in-memory-test-persistence");
    }

    @Bean("dao-vs-immutable-db")
    public PaymentListDAO paymentListDAOImmutable() {
        return new PaymentListDAO(entityManagerFactoryImmutable());
    }

    @Bean("dao-vs-in-memory-db")
    public PaymentListDAO paymentListDAOInMemory() {
        return new PaymentListDAO(entityManagerFactoryInMemory());
    }


    @Bean(name = "testFile")
    public File testFile() {
        File file = new File(testFileLocation);
        if (!file.exists()) throw new RuntimeException("Тестовый файл не найден");
        return file;
    }

    @Bean(name = "testDir")
    public File testDir() {

        File dir = new File(testDirectoryLocation);
        if (!dir.exists() && !dir.isDirectory())
            throw new RuntimeException("Путь не является директорией, или не существует");

        return dir;
    }

    @Bean(name = "backupDir")
    public File backupDir() {
        return new File(backupDir);
    }

}
