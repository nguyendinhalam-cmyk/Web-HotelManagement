package com.hotel.service;

import com.hotel.config.TransactionManager;
import com.hotel.dao.KhachHangDAO;
import com.hotel.entity.KhachHang;
import com.hotel.util.MaCodeGenerator;
import jakarta.persistence.EntityManager;

import java.util.List;

public class KhachHangService {

    private final KhachHangDAO dao = new KhachHangDAO();

    public void them(KhachHang khachHang) {
        validate(khachHang);

        TransactionManager tm = new TransactionManager();

        try {
            tm.begin();

            EntityManager em = tm.getEntityManager();

            if (khachHang.getMaKH() == null
                    || khachHang.getMaKH().isBlank()) {
                khachHang.setMaKH(
                        MaCodeGenerator.nextId(
                                dao.findAll(em),
                                "maKH",
                                "KH"
                        )
                );
            }

            if (dao.findById(em, khachHang.getMaKH()) != null) {
                throw new IllegalArgumentException(
                        "Mã khách hàng đã tồn tại: "
                                + khachHang.getMaKH()
                );
            }

            ensureUnique(em, khachHang, null);

            dao.save(em, khachHang);

            tm.commit();

        } catch (RuntimeException e) {
            tm.rollback();
            throw e;
        } finally {
            tm.close();
        }
    }

    public KhachHang findById(String maKH) {
        TransactionManager tm = new TransactionManager();

        try {
            return dao.findById(tm.getEntityManager(), maKH);
        } finally {
            tm.close();
        }
    }

    public List<KhachHang> findAll() {
        TransactionManager tm = new TransactionManager();

        try {
            return dao.findAll(tm.getEntityManager());
        } finally {
            tm.close();
        }
    }

    public void capNhat(KhachHang khachHang) {
        validate(khachHang);

        if (blank(khachHang.getMaKH())) {
            throw new IllegalArgumentException(
                    "Mã khách hàng không được để trống."
            );
        }

        TransactionManager tm = new TransactionManager();

        try {
            tm.begin();

            EntityManager em = tm.getEntityManager();

            if (dao.findById(em, khachHang.getMaKH()) == null) {
                throw new IllegalArgumentException(
                        "Không tìm thấy khách hàng: "
                                + khachHang.getMaKH()
                );
            }

            ensureUnique(em, khachHang, khachHang.getMaKH());

            dao.update(em, khachHang);

            tm.commit();

        } catch (RuntimeException e) {
            tm.rollback();
            throw e;
        } finally {
            tm.close();
        }
    }

    public void xoa(String maKH) {
        TransactionManager tm = new TransactionManager();

        try {
            tm.begin();

            EntityManager em = tm.getEntityManager();

            KhachHang khachHang = dao.findById(em, maKH);

            if (khachHang == null) {
                throw new IllegalArgumentException(
                        "Không tìm thấy khách hàng: " + maKH
                );
            }

            dao.delete(em, khachHang);

            tm.commit();

        } catch (RuntimeException e) {
            tm.rollback();
            throw e;
        } finally {
            tm.close();
        }
    }

    private void validate(KhachHang x) {
        if (x == null) {
            throw new IllegalArgumentException(
                    "Thông tin khách hàng không hợp lệ."
            );
        }

        if (blank(x.getHoTen())) {
            throw new IllegalArgumentException(
                    "Vui lòng nhập họ tên khách hàng."
            );
        }

        if (blank(x.getSoDienThoai())) {
            throw new IllegalArgumentException(
                    "Vui lòng nhập số điện thoại."
            );
        }

        if (!blank(x.getCccd())) {
            String cccd = x.getCccd().trim();

            if (!cccd.matches("[0-9]{12}")) {
                throw new IllegalArgumentException(
                        "CCCD phải gồm đúng 12 chữ số."
                );
            }

            x.setCccd(cccd);
        }
    }

    private void ensureUnique(
            EntityManager em,
            KhachHang x,
            String currentId) {

        for (KhachHang item : dao.findAll(em)) {

            if (currentId != null
                    && currentId.equals(item.getMaKH())) {
                continue;
            }

            if (x.getCccd() != null
                    && !x.getCccd().isBlank()
                    && x.getCccd().equals(item.getCccd())) {
                throw new IllegalArgumentException(
                        "CCCD đã được sử dụng."
                );
            }

            if (x.getSoDienThoai().equals(item.getSoDienThoai())) {
                throw new IllegalArgumentException(
                        "Số điện thoại đã được sử dụng."
                );
            }

            if (!blank(x.getEmail())
                    && x.getEmail().equals(item.getEmail())) {
                throw new IllegalArgumentException(
                        "Email đã được sử dụng."
                );
            }
        }
    }

    private boolean blank(String s) {
        return s == null || s.isBlank();
    }
}

