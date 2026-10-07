package com.hotel.service;

import com.hotel.config.TransactionManager;
import com.hotel.dao.TaiKhoanDAO;
import com.hotel.entity.TaiKhoan;
import com.hotel.entity.NhanVien;
import com.hotel.enums.TrangThaiTaiKhoan;
import com.hotel.enums.VaiTro;
import com.hotel.util.PasswordUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class TaiKhoanService {
    private final TaiKhoanDAO dao = new TaiKhoanDAO();

    public List<TaiKhoan> findAll() {
        TransactionManager tm = new TransactionManager();
        try { return dao.findAll(tm.getEntityManager()); }
        finally { tm.close(); }
    }

    public TaiKhoan findByMaNV(String maNV) {
        TransactionManager tm = new TransactionManager();
        try { return dao.findByMaNV(tm.getEntityManager(), maNV).orElse(null); }
        finally { tm.close(); }
    }

    public void tao(TaiKhoan taiKhoan, String matKhauBanDau) {
        if (taiKhoan == null || taiKhoan.getNhanVien() == null || taiKhoan.getVaiTro() == null) {
            throw new IllegalArgumentException("Thông tin tài khoản không hợp lệ.");
        }
        taiKhoan.setMatKhauHash(PasswordUtil.hash(matKhauBanDau));
        if (taiKhoan.getTrangThai() == null) taiKhoan.setTrangThai(TrangThaiTaiKhoan.HOAT_DONG);
        TransactionManager tm = new TransactionManager();
        try {
            tm.begin();
            EntityManager em = tm.getEntityManager();
            if (dao.findByMaNV(em, taiKhoan.getNhanVien().getMaNV()).isPresent()) {
                throw new IllegalArgumentException("Nhân viên đã có tài khoản.");
            }
            dao.save(em, taiKhoan);
            tm.commit();
        } catch (Exception e) { tm.rollback(); throw e; }
        finally { tm.close(); }
    }

    public void doiVaiTro(String maNV, VaiTro vaiTro) {
        capNhat(maNV, t -> t.setVaiTro(vaiTro));
    }

    public void doiTrangThai(String maNV, TrangThaiTaiKhoan trangThai) {
        capNhat(maNV, t -> t.setTrangThai(trangThai));
    }

    public void doiMatKhau(String maNV, String matKhauMoi) {
        String hash = PasswordUtil.hash(matKhauMoi);
        capNhat(maNV, t -> t.setMatKhauHash(hash));
    }

    public TaiKhoan dangNhap(String maNV, String matKhau) {
        TransactionManager tm = new TransactionManager();
        try {
            TaiKhoan t = dao.findByMaNV(tm.getEntityManager(), maNV).orElse(null);
            if (t == null) throw new IllegalArgumentException("Mã nhân viên hoặc mật khẩu không đúng.");
            if (t.getTrangThai() != TrangThaiTaiKhoan.HOAT_DONG) throw new IllegalStateException("Tài khoản đang bị khóa.");
            if (!PasswordUtil.matches(matKhau, t.getMatKhauHash())) throw new IllegalArgumentException("Mã nhân viên hoặc mật khẩu không đúng.");
            t.getNhanVien().getHoTen();
            return t;
        } finally { tm.close(); }
    }

    private void capNhat(String maNV, java.util.function.Consumer<TaiKhoan> action) {
        TransactionManager tm = new TransactionManager();
        try {
            tm.begin();
            EntityManager em = tm.getEntityManager();
            TaiKhoan t = dao.findByMaNV(em, maNV).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản."));
            action.accept(t);
            dao.update(em, t);
            tm.commit();
        } catch (Exception e) { tm.rollback(); throw e; }
        finally { tm.close(); }
    }
}
