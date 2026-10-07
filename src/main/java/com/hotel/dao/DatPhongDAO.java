package com.hotel.dao;

import com.hotel.entity.DatPhong;
import com.hotel.enums.TrangThaiDatPhong;
import jakarta.persistence.EntityManager;

import java.util.List;

public class DatPhongDAO
        extends BaseDAO<DatPhong, String> {

    public DatPhongDAO() {
        super(DatPhong.class);
    }

    public List<DatPhong> findByKhachHang(
            EntityManager em,
            String maKH) {

        String jpql = """
                SELECT d
                FROM DatPhong d
                WHERE d.khachHang.maKH = :maKH
                ORDER BY d.ngayDat DESC
                """;

        return em.createQuery(jpql, DatPhong.class)
                .setParameter("maKH", maKH)
                .getResultList();
    }

    public List<DatPhong> findAllWithKhachHang(EntityManager em) {
        String jpql = """
                SELECT d
                FROM DatPhong d
                JOIN FETCH d.khachHang
                JOIN FETCH d.nhanVienXuLy
                LEFT JOIN FETCH d.nhanVienCheckIn
                LEFT JOIN FETCH d.nhanVienCheckOut
                ORDER BY d.ngayDat DESC
                """;
        return em.createQuery(jpql, DatPhong.class).getResultList();
    }

    public List<DatPhong> findByTrangThai(
            EntityManager em,
            TrangThaiDatPhong trangThai) {

        String jpql = """
                SELECT d
                FROM DatPhong d
                WHERE d.trangThai = :trangThai
                ORDER BY d.ngayDat DESC
                """;

        return em.createQuery(jpql, DatPhong.class)
                .setParameter("trangThai", trangThai)
                .getResultList();
    }
}