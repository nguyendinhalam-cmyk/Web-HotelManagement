package com.hotel.dao;

import com.hotel.entity.HoaDon;
import jakarta.persistence.EntityManager;
import java.util.List;

public class HoaDonDAO
        extends BaseDAO<HoaDon, String> {

    public HoaDonDAO() {
        super(HoaDon.class);
    }
    public List<HoaDon> findByDatPhong(
            EntityManager em,
            String maDatPhong) {

        String jpql = """
            SELECT h
            FROM HoaDon h
            WHERE h.datPhong.maDatPhong = :maDatPhong
            ORDER BY h.ngayLap DESC
            """;

        return em.createQuery(jpql, HoaDon.class)
                .setParameter("maDatPhong", maDatPhong)
                .getResultList();
    }
}