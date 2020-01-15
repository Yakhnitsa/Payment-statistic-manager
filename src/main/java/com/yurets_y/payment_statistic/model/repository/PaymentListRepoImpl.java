package com.yurets_y.payment_statistic.model.repository;

import com.yurets_y.payment_statistic.model.entity.PaymentList;
import com.yurets_y.payment_statistic.model.entity.PaymentListId;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.List;

@Service("paymentListRepoImpl")
public class PaymentListRepoImpl implements PaymentListRepo {

    private EntityManager entityManager;

    public PaymentListRepoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void add(PaymentList paymentList) {
        beginTransaction();
        entityManager.persist(paymentList);
        commitTransaction();
    }

    @Override
    public void update(PaymentList paymentList) {
        beginTransaction();
        entityManager.merge(paymentList);
        commitTransaction();
    }

    @Override
    public void remove(PaymentList paymentList) {
        beginTransaction();
        entityManager.remove(paymentList);
        commitTransaction();
    }

    @Override
    public PaymentList getById(PaymentListId id) {
        beginTransaction();
        PaymentList listFromRepo = entityManager.find(PaymentList.class, id);
        commitTransaction();
        return listFromRepo;

    }

    @Override
    public List<PaymentList> getAll() {
        return null;
    }

    private void beginTransaction() {
        entityManager.getTransaction().begin();
    }

    private void commitTransaction() {
        entityManager.getTransaction().commit();
    }
}
