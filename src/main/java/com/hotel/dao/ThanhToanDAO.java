package com.hotel.dao;

import com.hotel.entity.ThanhToan;
import jakarta.persistence.EntityManager;
import java.util.List;

public class ThanhToanDAO
        extends BaseDAO<ThanhToan, String> {

    public ThanhToanDAO() {
        super(ThanhToan.class);
    }

    public List<ThanhToan> findByDatPhong(EntityManager em, String maDatPhong) {
        String jpql = """
                SELECT t
                FROM ThanhToan t
                WHERE t.datPhong.maDatPhong = :maDatPhong
                ORDER BY t.thoiGianTao DESC
                """;
        return em.createQuery(jpql, ThanhToan.class)
                .setParameter("maDatPhong", maDatPhong)
                .getResultList();
    }
}