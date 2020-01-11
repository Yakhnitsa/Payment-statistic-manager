package com.yurets_y.payment_statistic.model.repository;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class SessionFactoryInitializer {
    /** The single instance of hibernate SessionFactory */
    private static org.hibernate.SessionFactory sessionFactory;

    private SessionFactoryInitializer() {
    }
    static {
        final Configuration cfg = new Configuration();
        cfg.configure("/hibernate/hibernate.cfg.xml");
        sessionFactory = cfg.buildSessionFactory();
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

}
