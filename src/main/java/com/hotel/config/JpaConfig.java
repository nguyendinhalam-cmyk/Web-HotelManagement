package com.hotel.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

public class JpaConfig {

    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY;

    static {
        try {
            Map<String, Object> properties = new HashMap<>();

            // Sử dụng HikariCP làm DataSource
            properties.put(
                    "hibernate.connection.datasource",
                    DataSourceConfig.getDataSource()
            );

            // Hibernate
            properties.put(
                    "hibernate.hbm2ddl.auto",
                    "validate"
            );

            properties.put(
                    "hibernate.dialect",
                    "org.hibernate.dialect.MySQLDialect"
            );

            properties.put(
                    "hibernate.show_sql",
                    true
            );

            properties.put(
                    "hibernate.format_sql",
                    true
            );

            properties.put(
                    "hibernate.highlight_sql",
                    true
            );

            ENTITY_MANAGER_FACTORY =
                    Persistence.createEntityManagerFactory(
                            "QLKhachSanPU",
                            properties
                    );

        } catch (Throwable e) {

            System.err.println(
                    "Không thể khởi tạo EntityManagerFactory."
            );

            e.printStackTrace();

            // Nếu JPA khởi tạo thất bại thì đóng pool
            DataSourceConfig.close();

            throw new ExceptionInInitializerError(e);
        }
    }

    private JpaConfig() {
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return ENTITY_MANAGER_FACTORY;
    }

    public static EntityManager createEntityManager() {
        return ENTITY_MANAGER_FACTORY.createEntityManager();
    }

    public static void close() {

        if (ENTITY_MANAGER_FACTORY != null
                && ENTITY_MANAGER_FACTORY.isOpen()) {

            ENTITY_MANAGER_FACTORY.close();
        }

        DataSourceConfig.close();
    }
}