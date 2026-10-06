package com.hotel.service;

import com.hotel.config.TransactionManager;
import com.hotel.dao.CtDatPhongDAO;
import com.hotel.dao.CtHoaDonDAO;
import com.hotel.dao.DatPhongDAO;
import com.hotel.dao.HoaDonDAO;
import com.hotel.dao.PhongDAO;
import com.hotel.dao.SuDungDichVuDAO;
import com.hotel.entity.CtDatPhong;
import com.hotel.entity.CtHoaDon;
import com.hotel.entity.DatPhong;
import com.hotel.entity.DichVu;
import com.hotel.entity.HoaDon;
import com.hotel.entity.Phong;
import com.hotel.entity.SuDungDichVu;
import com.hotel.enums.TrangThaiDatPhong;
import com.hotel.enums.TrangThaiHoaDon;
import com.hotel.enums.TrangThaiPhong;
import com.hotel.util.MaCodeGenerator;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Orchestrates checkout: tính tiền, lập hóa đơn, trả phòng trong một transaction. */
public class CheckoutService {
    private final DatPhongDAO datPhongDAO = new DatPhongDAO();
    private final CtDatPhongDAO ctDatPhongDAO = new CtDatPhongDAO();
    private final PhongDAO phongDAO = new PhongDAO();
    private final SuDungDichVuDAO suDungDichVuDAO = new SuDungDichVuDAO();
    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private final CtHoaDonDAO ctHoaDonDAO = new CtHoaDonDAO();

    public HoaDon checkout(String maDatPhong) {
        TransactionManager tm = new TransactionManager();
        try {
            tm.begin();
            EntityManager em = tm.getEntityManager();
            DatPhong datPhong = datPhongDAO.findById(em, maDatPhong);
            if (datPhong == null) throw new IllegalArgumentException("Không tìm thấy đặt phòng: " + maDatPhong);
            if (datPhong.getTrangThai() != TrangThaiDatPhong.DANG_O) throw new IllegalStateException("Chỉ được checkout khi đặt phòng ở trạng thái DANG_O.");

            List<CtDatPhong> chiTietPhong = ctDatPhongDAO.findByDatPhong(em, maDatPhong);
            if (chiTietPhong.isEmpty()) throw new IllegalStateException("Đặt phòng không có chi tiết phòng.");
            List<SuDungDichVu> dichVu = suDungDichVuDAO.findByDatPhong(em, maDatPhong);

            BigDecimal tongTienPhong = BigDecimal.ZERO;
            BigDecimal tongTienDichVu = BigDecimal.ZERO;
            List<CtHoaDon> chiTietHoaDon = new ArrayList<>();

            for (CtDatPhong ct : chiTietPhong) {
                BigDecimal tien = tinhTienPhong(ct);
                tongTienPhong = tongTienPhong.add(tien);
                CtHoaDon line = new CtHoaDon();
                line.setTenKhoanThu("Tiền phòng " + (ct.getPhong() == null ? "" : ct.getPhong().getSoPhong()));
                line.setSoLuong(1); line.setDonGia(ct.getGiaPhong()); line.setThanhTien(tien);
                chiTietHoaDon.add(line);
            }
            for (SuDungDichVu sd : dichVu) {
                BigDecimal tien = tinhTienDichVu(sd);
                tongTienDichVu = tongTienDichVu.add(tien);
                DichVu dv = sd.getDichVu();
                CtHoaDon line = new CtHoaDon();
                line.setTenKhoanThu(dv == null ? "Dịch vụ" : dv.getTenDichVu());
                line.setSoLuong(sd.getSoLuong()); line.setDonGia(sd.getDonGia()); line.setThanhTien(tien);
                chiTietHoaDon.add(line);
            }

            HoaDon hoaDon = new HoaDon();
            hoaDon.setMaHoaDon(MaCodeGenerator.nextId(hoaDonDAO.findAll(em), "maHoaDon", "HD"));
            hoaDon.setDatPhong(datPhong); hoaDon.setNgayLap(LocalDateTime.now());
            hoaDon.setTongTienPhong(tongTienPhong); hoaDon.setTongTienDichVu(tongTienDichVu);
            hoaDon.setTongTien(tongTienPhong.add(tongTienDichVu)); hoaDon.setTrangThai(TrangThaiHoaDon.NHAP);
            hoaDonDAO.save(em, hoaDon);

            Set<String> reserved = new HashSet<>();
            for (CtHoaDon line : chiTietHoaDon) {
                line.setMaCtHoaDon(MaCodeGenerator.nextId(ctHoaDonDAO.findAll(em), "maCtHoaDon", "CTHD", reserved));
                reserved.add(line.getMaCtHoaDon()); line.setHoaDon(hoaDon); ctHoaDonDAO.save(em, line);
            }

            for (CtDatPhong ct : chiTietPhong) {
                Phong phong = ct.getPhong();
                if (phong != null) { phong.setTrangThai(TrangThaiPhong.TRONG); phongDAO.update(em, phong); }
            }
            datPhong.setTrangThai(TrangThaiDatPhong.DA_TRA_PHONG);
            datPhongDAO.update(em, datPhong);
            tm.commit();
            return hoaDon;
        } catch (Exception e) { tm.rollback(); throw new RuntimeException("Checkout thất bại: " + e.getMessage(), e); }
        finally { tm.close(); }
    }

    private BigDecimal tinhTienPhong(CtDatPhong ct) {
        if (ct.getNgayNhan() == null || ct.getNgayTra() == null || ct.getGiaPhong() == null) throw new IllegalStateException("Chi tiết phòng thiếu ngày hoặc giá.");
        long soDem = ChronoUnit.DAYS.between(ct.getNgayNhan(), ct.getNgayTra());
        if (soDem <= 0) throw new IllegalStateException("Số đêm phải lớn hơn 0.");
        return ct.getGiaPhong().multiply(BigDecimal.valueOf(soDem));
    }

    private BigDecimal tinhTienDichVu(SuDungDichVu sd) {
        if (sd.getDonGia() == null || sd.getSoLuong() == null || sd.getSoLuong() <= 0) throw new IllegalStateException("Dữ liệu dịch vụ không hợp lệ.");
        return sd.getDonGia().multiply(BigDecimal.valueOf(sd.getSoLuong()));
    }
}
