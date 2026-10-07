package com.hotel.dao;

import com.hotel.entity.CtHoaDon;
import jakarta.persistence.EntityManager;
import java.util.List;

public class CtHoaDonDAO extends BaseDAO<CtHoaDon, String> {
    public CtHoaDonDAO() { super(CtHoaDon.class); }

    public List<CtHoaDon> findByHoaDon(EntityManager em, String maHoaDon) {
        return em.createQuery("SELECT c FROM CtHoaDon c WHERE c.hoaDon.maHoaDon = :maHoaDon", CtHoaDon.class)
                .setParameter("maHoaDon", maHoaDon).getResultList();
    }
}
