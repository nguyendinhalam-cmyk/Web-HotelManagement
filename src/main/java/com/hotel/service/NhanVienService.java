package com.hotel.service;

import com.hotel.config.TransactionManager;
import com.hotel.dao.NhanVienDAO;
import com.hotel.entity.NhanVien;
import com.hotel.entity.TaiKhoan;
import com.hotel.enums.TrangThaiTaiKhoan;
import com.hotel.enums.VaiTro;
import com.hotel.util.MaCodeGenerator;
import com.hotel.util.PasswordUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class NhanVienService {
    private final NhanVienDAO dao = new NhanVienDAO();

    public void taoNhanVienVaTaiKhoan(NhanVien x, String matKhauBanDau, VaiTro vaiTro) {
        validate(x);
        if (blank(matKhauBanDau) || vaiTro == null) throw new IllegalArgumentException("Mật khẩu và vai trò là bắt buộc.");
        TransactionManager tm = new TransactionManager();
        try {
            tm.begin();
            EntityManager em = tm.getEntityManager();
            if (x.getMaNV() == null || x.getMaNV().isBlank()) x.setMaNV(MaCodeGenerator.nextId(dao.findAll(em), "maNV", "NV"));
            if (dao.findById(em, x.getMaNV()) != null) throw new IllegalArgumentException("Mã nhân viên đã tồn tại: " + x.getMaNV());
            ensureUnique(em, x, null);
            TaiKhoan tk = new TaiKhoan();
            tk.setMaTK(MaCodeGenerator.nextId(em.createQuery("select t from TaiKhoan t", TaiKhoan.class).getResultList(), "maTK", "TK"));
            tk.setNhanVien(x);
            tk.setMatKhauHash(PasswordUtil.hash(matKhauBanDau));
            tk.setVaiTro(vaiTro);
            tk.setTrangThai(TrangThaiTaiKhoan.HOAT_DONG);
            x.setTaiKhoan(tk);
            dao.save(em, x);
            tm.commit();
        } catch (Exception e) { tm.rollback(); throw e; }
        finally { tm.close(); }
    }

    public NhanVien findById(String id) { TransactionManager tm = new TransactionManager(); try { return dao.findById(tm.getEntityManager(), id); } finally { tm.close(); } }
    public List<NhanVien> findAll() { TransactionManager tm = new TransactionManager(); try { return dao.findAll(tm.getEntityManager()); } finally { tm.close(); } }

    public void capNhatThongTin(NhanVien x) {
        validate(x);
        TransactionManager tm = new TransactionManager();
        try {
            tm.begin(); EntityManager em = tm.getEntityManager();
            NhanVien old = dao.findById(em, x.getMaNV());
            if (old == null) throw new IllegalArgumentException("Không tìm thấy nhân viên: " + x.getMaNV());
            ensureUnique(em, x, x.getMaNV());
            old.setHoTen(x.getHoTen()); old.setNgaySinh(x.getNgaySinh()); old.setGioiTinh(x.getGioiTinh());
            old.setSoDienThoai(x.getSoDienThoai()); old.setEmail(x.getEmail()); old.setDiaChi(x.getDiaChi());
            dao.update(em, old); tm.commit();
        } catch (Exception e) { tm.rollback(); throw e; } finally { tm.close(); }
    }

    public void xoa(String id) {
        throw new IllegalStateException("Không xóa nhân viên khỏi hệ thống. Hãy khóa tài khoản để bảo toàn lịch sử nghiệp vụ.");
    }

    private void validate(NhanVien x) { if (x == null || blank(x.getHoTen()) || blank(x.getSoDienThoai()) || blank(x.getEmail())) throw new IllegalArgumentException("Thông tin nhân viên không hợp lệ."); }
    private void ensureUnique(EntityManager em, NhanVien x, String current) { for (NhanVien i : dao.findAll(em)) { if (current != null && current.equals(i.getMaNV())) continue; if (x.getSoDienThoai().equals(i.getSoDienThoai())) throw new IllegalArgumentException("Số điện thoại đã được sử dụng."); if (x.getEmail().equals(i.getEmail())) throw new IllegalArgumentException("Email đã được sử dụng."); } }
    private boolean blank(String s) { return s == null || s.isBlank(); }
}
