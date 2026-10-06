package com.hotel.dao;

import jakarta.persistence.EntityManager;

import java.util.List;

public abstract class BaseDAO<T, ID> {

    private final Class<T> entityClass;

    protected BaseDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public void save(EntityManager em, T entity) {
        em.persist(entity);
    }

    public T findById(EntityManager em, ID id) {
        return em.find(entityClass, id);
    }

    public List<T> findAll(EntityManager em) {

        String jpql = "SELECT e FROM "
                + entityClass.getSimpleName()
                + " e";

        return em.createQuery(jpql, entityClass)
                .getResultList();
    }

    public T update(EntityManager em, T entity) {
        return em.merge(entity);
    }

    public void delete(EntityManager em, T entity) {

        T managedEntity = em.merge(entity);

        em.remove(managedEntity);
    }
}