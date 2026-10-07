package com.hotel.test;

import com.hotel.config.JpaConfig;
import jakarta.persistence.EntityManager;

public class JpaConnectionTest {

    public static void main(String[] args) {

        EntityManager entityManager = null;

        try {
            entityManager = JpaConfig.createEntityManager();

            System.out.println("=================================");
            System.out.println("JPA KẾT NỐI THÀNH CÔNG");
            System.out.println("EntityManager đang hoạt động: "
                    + entityManager.isOpen());
            System.out.println("=================================");

        } catch (Exception e) {

            System.err.println("JPA KẾT NỐI THẤT BẠI!");
            e.printStackTrace();

        } finally {

            if (entityManager != null
                    && entityManager.isOpen()) {

                entityManager.close();
            }

            JpaConfig.close();
        }
    }
}