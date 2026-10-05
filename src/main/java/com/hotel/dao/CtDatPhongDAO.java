package com.hotel.dao;

import com.hotel.entity.CtDatPhong;
import com.hotel.enums.TrangThaiDatPhong;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.util.List;

public class CtDatPhongDAO
        extends BaseDAO<CtDatPhong, String> {

    public CtDatPhongDAO() {
        super(CtDatPhong.class);
    }

    public List<CtDatPhong> findByDatPhong(
            EntityManager em,
            String maDatPhong) {

        String jpql = """
                SELECT c
                FROM CtDatPhong c
                WHERE c.datPhong.maDatPhong = :maDatPhong
                ORDER BY c.ngayNhan
                """;

        return em.createQuery(jpql, CtDatPhong.class)
                .setParameter("maDatPhong", maDatPhong)
                .getResultList();
    }

    public List<CtDatPhong> findByPhong(
            EntityManager em,
            String maPhong) {

        String jpql = """
                SELECT c
                FROM CtDatPhong c
                WHERE c.phong.maPhong = :maPhong
                ORDER BY c.ngayNhan
                """;

        return em.createQuery(jpql, CtDatPhong.class)
                .setParameter("maPhong", maPhong)
                .getResultList();
    }

    public boolean existsPhongTrungLich(
            EntityManager em,
            String maPhong,
            LocalDate ngayNhan,
            LocalDate ngayTra) {

        String jpql = """
            SELECT COUNT(c)
            FROM CtDatPhong c
            WHERE c.phong.maPhong = :maPhong
              AND c.datPhong.trangThai <> :trangThaiHuy
              AND c.ngayNhan < :ngayTra
              AND c.ngayTra > :ngayNhan
            """;

        Long count = em.createQuery(jpql, Long.class)
                .setParameter("maPhong", maPhong)
                .setParameter(
                        "trangThaiHuy",
                        TrangThaiDatPhong.DA_HUY
                )
                .setParameter("ngayNhan", ngayNhan)
                .setParameter("ngayTra", ngayTra)
                .getSingleResult();

        return count > 0;
    }
}