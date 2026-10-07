package com.hotel.dao;

import com.hotel.entity.TaiKhoan;
import jakarta.persistence.EntityManager;
import java.util.Optional;

public class TaiKhoanDAO extends BaseDAO<TaiKhoan, String> {
    public TaiKhoanDAO() { super(TaiKhoan.class); }

    public Optional<TaiKhoan> findByMaNV(EntityManager em, String maNV) {
        return em.createQuery("select t from TaiKhoan t where t.nhanVien.maNV = :maNV", TaiKhoan.class)
                .setParameter("maNV", maNV)
                .getResultStream().findFirst();
    }
}
