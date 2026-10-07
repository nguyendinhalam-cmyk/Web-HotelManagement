package com.hotel.service;

import com.hotel.config.TransactionManager;
import com.hotel.dao.CtHoaDonDAO;
import com.hotel.dao.HoaDonDAO;
import com.hotel.dao.SuDungDichVuDAO;
import com.hotel.entity.CtDatPhong;
import com.hotel.entity.CtHoaDon;
import com.hotel.entity.DatPhong;
import com.hotel.entity.DichVu;
import com.hotel.entity.HoaDon;
import com.hotel.entity.SuDungDichVu;
import com.hotel.enums.TrangThaiHoaDon;
import jakarta.persistence.EntityManager;
import org.hibernate.Hibernate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.hotel.util.MaCodeGenerator;

public class HoaDonService {

    private final HoaDonDAO hoaDonDAO;
    private final CtHoaDonDAO ctHoaDonDAO;
    private final SuDungDichVuDAO suDungDichVuDAO;

    public HoaDonService() {
        this.hoaDonDAO = new HoaDonDAO();
        this.ctHoaDonDAO = new CtHoaDonDAO();
        this.suDungDichVuDAO = new SuDungDichVuDAO();
    }

    /**
     * Lập/cập nhật hóa đơn duy nhất của một DatPhong.
     * Hóa đơn được tạo ngay khi đặt phòng; phương thức này chỉ
     * phục vụ tương thích và sẽ cập nhật lại hóa đơn hiện có.
     */
    public HoaDon taoHoaDon(String maDatPhong) {

        TransactionManager transactionManager =
                new TransactionManager();

        try {
            transactionManager.begin();

            EntityManager em =
                    transactionManager.getEntityManager();

            DatPhong datPhong = em.find(
                    DatPhong.class,
                    maDatPhong
            );

            if (datPhong == null) {
                throw new IllegalArgumentException(
                        "Không tìm thấy đặt phòng: "
                                + maDatPhong
                );
            }

            if (datPhong.getTrangThai() == com.hotel.enums.TrangThaiDatPhong.DA_HUY) {
                throw new IllegalStateException("Không thể lập hóa đơn cho đặt phòng đã hủy.");
            }

            if (datPhong.getChiTietDatPhong() == null
                    || datPhong.getChiTietDatPhong().isEmpty()) {

                throw new IllegalStateException(
                        "Đặt phòng không có chi tiết phòng."
                );
            }

            /*
             * ==========================================
             * 1. TÍNH TIỀN PHÒNG
             * ==========================================
             */

            BigDecimal tongTienPhong = BigDecimal.ZERO;

            List<CtHoaDon> chiTietHoaDon =
                    new ArrayList<>();

            for (CtDatPhong ctDatPhong
                    : datPhong.getChiTietDatPhong()) {

                BigDecimal tienPhong =
                        tinhTienPhong(ctDatPhong);

                tongTienPhong =
                        tongTienPhong.add(tienPhong);

                CtHoaDon ctHoaDon = taoChiTietTienPhong(
                        ctDatPhong,
                        tienPhong
                );

                chiTietHoaDon.add(ctHoaDon);
            }

            /*
             * ==========================================
             * 2. TÍNH TIỀN DỊCH VỤ
             * ==========================================
             */

            List<SuDungDichVu> danhSachSuDung =
                    suDungDichVuDAO.findByDatPhong(
                            em,
                            maDatPhong
                    );

            BigDecimal tongTienDichVu =
                    BigDecimal.ZERO;

            for (SuDungDichVu suDung
                    : danhSachSuDung) {

                BigDecimal tienDichVu =
                        tinhTienDichVu(suDung);

                tongTienDichVu =
                        tongTienDichVu.add(tienDichVu);

                CtHoaDon ctHoaDon =
                        taoChiTietDichVu(
                                suDung,
                                tienDichVu
                        );

                chiTietHoaDon.add(ctHoaDon);
            }

            /*
             * ==========================================
             * 3. TỔNG TIỀN
             * ==========================================
             */

            BigDecimal tongTien =
                    tongTienPhong.add(tongTienDichVu);

            /*
             * ==========================================
             * 4. TẠO HÓA ĐƠN
             * ==========================================
             */

            List<HoaDon> hoaDons = hoaDonDAO.findByDatPhong(em, maDatPhong);
            HoaDon hoaDon;
            if (hoaDons.isEmpty()) {
                hoaDon = new HoaDon();
                hoaDon.setMaHoaDon(MaCodeGenerator.nextId(hoaDonDAO.findAll(em), "maHoaDon", "HD"));
                hoaDon.setDatPhong(datPhong);
                hoaDon.setNgayLap(LocalDateTime.now());
                hoaDonDAO.save(em, hoaDon);
            } else {
                hoaDon = hoaDons.get(0);
                if (hoaDon.getTrangThai() == TrangThaiHoaDon.DA_PHAT_HANH) {
                    throw new IllegalStateException("Hóa đơn đã phát hành, không thể tạo lại.");
                }
                for (CtHoaDon old : ctHoaDonDAO.findAll(em)) {
                    if (old.getHoaDon() != null && hoaDon.getMaHoaDon().equals(old.getHoaDon().getMaHoaDon())) {
                        ctHoaDonDAO.delete(em, old);
                    }
                }
            }

            hoaDon.setTongTienPhong(tongTienPhong);
            hoaDon.setTongTienDichVu(tongTienDichVu);
            hoaDon.setTongTien(tongTien);

            hoaDon.setTrangThai(
                    TrangThaiHoaDon.NHAP
            );

            hoaDonDAO.update(em, hoaDon);

            /*
             * ==========================================
             * 5. TẠO CHI TIẾT HÓA ĐƠN
             * ==========================================
             */

            Set<String> maCtDaSinh = new HashSet<>();
            for (CtHoaDon ctHoaDon : chiTietHoaDon) {
                ctHoaDon.setMaCtHoaDon(MaCodeGenerator.nextId(
                        ctHoaDonDAO.findAll(em), "maCtHoaDon", "CTHD", maCtDaSinh));
                maCtDaSinh.add(ctHoaDon.getMaCtHoaDon());
                ctHoaDon.setHoaDon(hoaDon);

                ctHoaDonDAO.save(
                        em,
                        ctHoaDon
                );
            }

            transactionManager.commit();

            return hoaDon;

        } catch (Exception e) {

            transactionManager.rollback();

            throw e;

        } finally {

            transactionManager.close();
        }
    }

    /**
     * Tính tiền phòng.
     *
     * Số tiền = số đêm × giá phòng.
     */
    private BigDecimal tinhTienPhong(
            CtDatPhong ctDatPhong) {

        if (ctDatPhong.getNgayNhan() == null
                || ctDatPhong.getNgayTra() == null) {

            throw new IllegalArgumentException(
                    "Chi tiết đặt phòng thiếu ngày nhận/trả."
            );
        }

        if (ctDatPhong.getGiaPhong() == null) {

            throw new IllegalArgumentException(
                    "Chi tiết đặt phòng thiếu giá phòng."
            );
        }

        long soDem =
                java.time.temporal.ChronoUnit.DAYS.between(
                        ctDatPhong.getNgayNhan(),
                        ctDatPhong.getNgayTra()
                );

        if (soDem <= 0) {
            throw new IllegalArgumentException(
                    "Số đêm lưu trú phải lớn hơn 0."
            );
        }

        return ctDatPhong.getGiaPhong()
                .multiply(
                        BigDecimal.valueOf(soDem)
                );
    }

    /**
     * Tính tiền sử dụng dịch vụ.
     */
    private BigDecimal tinhTienDichVu(
            SuDungDichVu suDungDichVu) {

        if (suDungDichVu.getSoLuong() == null) {
            throw new IllegalArgumentException(
                    "Số lượng dịch vụ không được null."
            );
        }

        if (suDungDichVu.getDonGia() == null) {
            throw new IllegalArgumentException(
                    "Đơn giá dịch vụ không được null."
            );
        }

        return suDungDichVu.getDonGia()
                .multiply(
                        BigDecimal.valueOf(
                                suDungDichVu.getSoLuong()
                        )
                );
    }

    /**
     * Tạo chi tiết hóa đơn cho tiền phòng.
     */
    private CtHoaDon taoChiTietTienPhong(
            CtDatPhong ctDatPhong,
            BigDecimal thanhTien) {

        CtHoaDon ctHoaDon = new CtHoaDon();

        ctHoaDon.setTenKhoanThu(
                "Tiền phòng "
                        + ctDatPhong
                        .getPhong()
                        .getSoPhong()
        );

        ctHoaDon.setSoLuong(1);

        ctHoaDon.setDonGia(
                ctDatPhong.getGiaPhong()
        );

        ctHoaDon.setThanhTien(
                thanhTien
        );

        return ctHoaDon;
    }

    /**
     * Tạo chi tiết hóa đơn cho dịch vụ.
     */
    private CtHoaDon taoChiTietDichVu(
            SuDungDichVu suDungDichVu,
            BigDecimal thanhTien) {

        CtHoaDon ctHoaDon = new CtHoaDon();

        DichVu dichVu =
                suDungDichVu.getDichVu();

        ctHoaDon.setTenKhoanThu(
                dichVu != null
                        ? dichVu.getTenDichVu()
                        : "Dịch vụ"
        );

        ctHoaDon.setSoLuong(suDungDichVu.getSoLuong());

        ctHoaDon.setDonGia(
                suDungDichVu.getDonGia()
        );

        ctHoaDon.setThanhTien(
                thanhTien
        );

        return ctHoaDon;
    }

    /**
     * Phát hành hóa đơn.
     */
    public void phatHanhHoaDon(
            String maHoaDon) {

        TransactionManager transactionManager =
                new TransactionManager();

        try {
            transactionManager.begin();

            EntityManager em =
                    transactionManager.getEntityManager();

            HoaDon hoaDon =
                    hoaDonDAO.findById(
                            em,
                            maHoaDon
                    );

            if (hoaDon == null) {
                throw new IllegalArgumentException(
                        "Không tìm thấy hóa đơn: "
                                + maHoaDon
                );
            }

            if (hoaDon.getTrangThai()
                    != TrangThaiHoaDon.NHAP) {

                throw new IllegalStateException(
                        "Chỉ hóa đơn ở trạng thái NHAP "
                                + "mới được phát hành."
                );
            }

            hoaDon.setTrangThai(
                    TrangThaiHoaDon.DA_PHAT_HANH
            );

            hoaDonDAO.update(
                    em,
                    hoaDon
            );

            transactionManager.commit();

        } catch (Exception e) {

            transactionManager.rollback();

            throw e;

        } finally {

            transactionManager.close();
        }
    }

    /**
     * Hủy hóa đơn.
     */
    public void huyHoaDon(
            String maHoaDon) {

        TransactionManager transactionManager =
                new TransactionManager();

        try {
            transactionManager.begin();

            EntityManager em =
                    transactionManager.getEntityManager();

            HoaDon hoaDon =
                    hoaDonDAO.findById(
                            em,
                            maHoaDon
                    );

            if (hoaDon == null) {
                throw new IllegalArgumentException(
                        "Không tìm thấy hóa đơn: "
                                + maHoaDon
                );
            }

            if (hoaDon.getTrangThai()
                    == TrangThaiHoaDon.DA_HUY) {

                throw new IllegalStateException(
                        "Hóa đơn đã được hủy."
                );
            }

            hoaDon.setTrangThai(
                    TrangThaiHoaDon.DA_HUY
            );

            hoaDonDAO.update(
                    em,
                    hoaDon
            );

            transactionManager.commit();

        } catch (Exception e) {

            transactionManager.rollback();

            throw e;

        } finally {

            transactionManager.close();
        }
    }

    /**
     * Tìm hóa đơn theo mã.
     */
    public HoaDon findById(String maHoaDon) {

        TransactionManager transactionManager =
                new TransactionManager();

        try {
            EntityManager em =
                    transactionManager.getEntityManager();

            HoaDon hoaDon = hoaDonDAO.findById(em, maHoaDon);
            initialize(hoaDon);
            return hoaDon;

        } finally {

            transactionManager.close();
        }
    }

    /**
     * Lấy tất cả hóa đơn của một đặt phòng.
     */
    public List<HoaDon> findByDatPhong(
            String maDatPhong) {

        TransactionManager transactionManager =
                new TransactionManager();

        try {
            EntityManager em =
                    transactionManager.getEntityManager();

            List<HoaDon> items = hoaDonDAO.findByDatPhong(em, maDatPhong);
            items.forEach(this::initialize);
            return items;

        } finally {

            transactionManager.close();
        }
    }
    /** Lấy toàn bộ hóa đơn. */
    public List<HoaDon> findAll() {
        TransactionManager transactionManager = new TransactionManager();
        try {
            List<HoaDon> items = hoaDonDAO.findAll(transactionManager.getEntityManager());
            items.forEach(this::initialize);
            return items;
        } finally {
            transactionManager.close();
        }
    }

    /** Xóa hóa đơn chỉ khi còn ở trạng thái NHAP. */
    public void xoa(String maHoaDon) {
        TransactionManager transactionManager = new TransactionManager();
        try {
            transactionManager.begin();
            EntityManager em = transactionManager.getEntityManager();
            HoaDon hoaDon = hoaDonDAO.findById(em, maHoaDon);
            if (hoaDon == null) throw new IllegalArgumentException("Không tìm thấy hóa đơn: " + maHoaDon);
            if (hoaDon.getTrangThai() != TrangThaiHoaDon.NHAP) {
                throw new IllegalStateException("Chỉ hóa đơn NHAP mới được xóa.");
            }
            daoDelete(em, hoaDon);
            transactionManager.commit();
        } catch (Exception e) {
            transactionManager.rollback();
            throw e;
        } finally {
            transactionManager.close();
        }
    }

    private void daoDelete(EntityManager em, HoaDon hoaDon) {
        hoaDonDAO.delete(em, hoaDon);
    }

    private void initialize(HoaDon hoaDon) {
        if (hoaDon == null) return;
        Hibernate.initialize(hoaDon.getDatPhong());
        if (hoaDon.getDatPhong() != null) {
            Hibernate.initialize(hoaDon.getDatPhong().getKhachHang());
        }
        Hibernate.initialize(hoaDon.getChiTietHoaDon());
    }

}