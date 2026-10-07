package com.hotel.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class TransactionManager {

    private final EntityManager entityManager;
    private final EntityTransaction transaction;

    public TransactionManager() {
        this.entityManager = JpaConfig.createEntityManager();
        this.transaction = entityManager.getTransaction();
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void begin() {
        if (!transaction.isActive()) {
            transaction.begin();
        }
    }

    public void commit() {
        if (transaction.isActive()) {
            transaction.commit();
        }
    }

    public void rollback() {
        if (transaction.isActive()) {
            transaction.rollback();
        }
    }

    public void close() {
        if (entityManager.isOpen()) {
            entityManager.close();
        }
    }
}