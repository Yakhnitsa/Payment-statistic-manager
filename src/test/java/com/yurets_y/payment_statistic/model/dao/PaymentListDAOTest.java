package com.yurets_y.payment_statistic.model.dao;


import com.yurets_y.payment_statistic.model.repository.RepositoryTestConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

@Import(DAOTestConfiguration.class)
@RunWith(SpringRunner.class)
public class PaymentListDAOTest {
    @Autowired
    private PaymentListDAO paymentListDAO;
    @Resource(name="testFile")
    private File testFile;

    @Resource(name="testDir")
    private File testDir;

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
    public void saveToDBTest(){

    }

    @Test
    public void loadFromDBTest(){

    }

    @Test
    public void filterByDateTest(){

    }
}
