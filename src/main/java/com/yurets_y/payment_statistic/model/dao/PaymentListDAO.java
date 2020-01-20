package com.yurets_y.payment_statistic.model.dao;

import com.yurets_y.payment_statistic.model.entity.PaymentList;
import com.yurets_y.payment_statistic.model.entity.PaymentListId;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.Date;
import java.util.List;


@Service("paymentListDao")
public class PaymentListDAO {
    private EntityManagerFactory emf;

    private EntityManager em;

    public PaymentListDAO(EntityManagerFactory entityManager) {
        this.emf = entityManager;
    }

    public void add(PaymentList paymentList) {
        openEntityManager();
        beginTransaction();
        em.persist(paymentList);
        commitTransaction();
        closeEntityManager();
    }

    public void update(PaymentList paymentList) {
        openEntityManager();
        beginTransaction();
        em.merge(paymentList);
        commitTransaction();
        closeEntityManager();
    }

    public void remove(PaymentList paymentList) {
        openEntityManager();
        em.remove(paymentList);
        commitTransaction();
        closeEntityManager();
    }


    public PaymentList getById(PaymentListId id) {
        openEntityManager();
        beginTransaction();
        PaymentList listFromRepo = em.find(PaymentList.class, id);
        commitTransaction();
        closeEntityManager();
        return listFromRepo;

    }

    public List<PaymentList> getAll() {
        openEntityManager();
        beginTransaction();
        List<PaymentList> paymentLists = em.createQuery("from PaymentList", PaymentList.class).getResultList();
        commitTransaction();
        closeEntityManager();
        return paymentLists;
    }

    public List<PaymentList> getByPeriod(Date from, Date until){
        openEntityManager();
        beginTransaction();
        List<PaymentList> sortedList = em
                .createQuery("select list from PaymentList list where list.date between :dateFrom and :dateUntil",PaymentList.class)
                .setParameter("dateFrom",from)
                .setParameter("dateUntil", until)
                .getResultList();
        commitTransaction();
        closeEntityManager();
        return sortedList;
    }

    private void openEntityManager(){
        if(!this.em.isOpen()){
            this.em = emf.createEntityManager();
        }
    }

    private void closeEntityManager(){
        if(em.isOpen()) em.close();
    }


    private void beginTransaction() {
        em.getTransaction().begin();
    }

    private void commitTransaction() {
        em.getTransaction().commit();
    }
}
