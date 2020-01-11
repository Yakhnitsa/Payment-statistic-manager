package com.yurets_y.payment_statistic.model.repository;

import com.yurets_y.payment_statistic.model.entity.PaymentList;
import com.yurets_y.payment_statistic.model.entity.PaymentListId;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("paymentListRepoImpl")
public class PaymentListRepoImpl implements PaymentListRepo {
    @Override
    public void add(PaymentList paymentList) {
        SessionFactory sessionFactory = SessionFactoryInitializer.getSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.save(paymentList);
        transaction.commit();
        session.close();

    }

    @Override
    public void update(PaymentList paymentList) {

    }

    @Override
    public void remove(PaymentList paymentList) {

    }

    @Override
    public void getById(PaymentListId id) {

    }

    @Override
    public List<PaymentList> getAll() {
        return null;
    }
}
