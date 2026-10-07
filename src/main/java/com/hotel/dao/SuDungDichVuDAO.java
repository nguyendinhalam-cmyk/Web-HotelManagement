package com.hotel.dao;

import com.hotel.entity.SuDungDichVu;
import jakarta.persistence.EntityManager;
import java.util.List;

public class SuDungDichVuDAO
        extends BaseDAO<SuDungDichVu, String> {

    public SuDungDichVuDAO() {
        super(SuDungDichVu.class);
    }

    public List<SuDungDichVu> findByDatPhong(
            EntityManager em,
            String maDatPhong) {

        String jpql = """
            SELECT s
            FROM SuDungDichVu s
            WHERE s.datPhong.maDatPhong = :maDatPhong
            ORDER BY s.thoiGianSuDung
            """;

        return em.createQuery(jpql, SuDungDichVu.class)
                .setParameter("maDatPhong", maDatPhong)
                .getResultList();
    }
}