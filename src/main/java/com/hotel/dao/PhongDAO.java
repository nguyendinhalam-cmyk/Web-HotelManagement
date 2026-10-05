package com.hotel.dao;

import com.hotel.entity.Phong;
import com.hotel.enums.TrangThaiDatPhong;
import jakarta.persistence.EntityManager;


import java.time.LocalDate;
import java.util.List;

public class PhongDAO extends BaseDAO<Phong, String> {

    public PhongDAO() {
        super(Phong.class);
    }

    public Phong findByIdWithLoaiPhong(EntityManager em, String maPhong) {
        String jpql = """
                SELECT p
                FROM Phong p
                JOIN FETCH p.loaiPhong
                WHERE p.maPhong = :maPhong
                """;
        List<Phong> result = em.createQuery(jpql, Phong.class)
                .setParameter("maPhong", maPhong)
                .getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    public Phong findBySoPhong(
            EntityManager em,
            String soPhong) {

        String jpql = """
                SELECT p
                FROM Phong p
                WHERE p.soPhong = :soPhong
                """;

        List<Phong> result = em.createQuery(jpql, Phong.class)
                .setParameter("soPhong", soPhong)
                .getResultList();

        return result.isEmpty()
                ? null
                : result.get(0);
    }

    public List<Phong> findByLoaiPhong(
            EntityManager em,
            String maLoaiPhong) {

        String jpql = """
                SELECT p
                FROM Phong p
                WHERE p.loaiPhong.maLoaiPhong = :maLoaiPhong
                """;

        return em.createQuery(jpql, Phong.class)
                .setParameter("maLoaiPhong", maLoaiPhong)
                .getResultList();
    }

    public List<Phong> findPhongCoTheDat(
            EntityManager em,
            LocalDate ngayNhan,
            LocalDate ngayTra) {

        String jpql = """
            SELECT p
            FROM Phong p
            JOIN FETCH p.loaiPhong
            WHERE p.maPhong NOT IN (
                SELECT c.phong.maPhong
                FROM CtDatPhong c
                WHERE c.datPhong.trangThai <> :trangThaiHuy
                  AND c.ngayNhan < :ngayTra
                  AND c.ngayTra > :ngayNhan
            )
            ORDER BY p.soPhong
            """;

        return em.createQuery(jpql, Phong.class)
                .setParameter(
                        "trangThaiHuy",
                        TrangThaiDatPhong.DA_HUY
                )
                .setParameter("ngayNhan", ngayNhan)
                .setParameter("ngayTra", ngayTra)
                .getResultList();
    }
}