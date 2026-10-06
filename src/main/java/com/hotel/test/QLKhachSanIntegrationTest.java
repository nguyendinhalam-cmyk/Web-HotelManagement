package com.hotel.test;

import com.hotel.config.JpaConfig;
import com.hotel.config.TransactionManager;
import com.hotel.dao.CtDatPhongDAO;
import com.hotel.dao.CtHoaDonDAO;
import com.hotel.dao.DatPhongDAO;
import com.hotel.dao.HoaDonDAO;
import com.hotel.dao.KhachHangDAO;
import com.hotel.dao.PhongDAO;
import com.hotel.dao.SuDungDichVuDAO;
import com.hotel.dao.ThanhToanDAO;
import com.hotel.entity.CtDatPhong;
import com.hotel.entity.DatPhong;
import com.hotel.entity.HoaDon;
import com.hotel.entity.KhachHang;
import com.hotel.entity.Phong;
import com.hotel.entity.SuDungDichVu;
import com.hotel.entity.ThanhToan;
import com.hotel.entity.DichVu;
import com.hotel.enums.PhuongThucThanhToan;
import com.hotel.enums.TrangThaiDatPhong;
import com.hotel.enums.TrangThaiPhong;
import com.hotel.enums.TrangThaiThanhToan;
import com.hotel.service.CheckoutService;
import com.hotel.service.DatPhongService;
import com.hotel.service.HoaDonService;
import com.hotel.service.SuDungDichVuService;
import com.hotel.service.ThanhToanService;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Integration test thủ công cho QLKhachSan.
 *
 * Chạy khi MySQL QLKhachSan đã được tạo bằng 2 file SQL của project.
 * Test tạo dữ liệu tạm và tự dọn dữ liệu sau khi chạy.
 */
public class QLKhachSanIntegrationTest {

    private static final String BOOKING_1 = "TSTDP001";
    private static final String CT_BOOKING_1 = "TSTCT001";
    private static final String SERVICE_USE_1 = "TSTSD001";

    private static final String BOOKING_2 = "TSTDP002";
    private static final String CT_BOOKING_2 = "TSTCT002";

    private int passed;
    private int failed;

    public static void main(String[] args) {
        QLKhachSanIntegrationTest test = new QLKhachSanIntegrationTest();
        try {
            test.run();
        } finally {
            JpaConfig.close();
        }
    }

    private void run() {
        System.out.println("\n========== QLKHACHSAN INTEGRATION TEST ==========");
        try {
            testDao();
            testServiceRead();
            testBookingFlow();
            testHoaDonAndPaymentFlow();
            testCheckoutFlow();
        } catch (Throwable e) {
            fail("Test runner", e);
        } finally {
            cleanup();
        }
        System.out.println("\n========== KẾT QUẢ ==========");
        System.out.println("PASS: " + passed);
        System.out.println("FAIL: " + failed);
        if (failed > 0) {
            throw new AssertionError("Có " + failed + " test thất bại.");
        }
    }

    private void testDao() {
        section("1. DAO");
        EntityManager em = JpaConfig.createEntityManager();
        try {
            assertNotNull("DAO KhachHang.findById", new KhachHangDAO().findById(em, "KH001"));
            assertNotNull("DAO Phong.findById String", new PhongDAO().findById(em, "P101"));
            assertNotNull("DAO DatPhong.findById String", new DatPhongDAO().findById(em, "DP003"));
            assertNotNull("DAO HoaDon.findById String", new HoaDonDAO().findById(em, "HD001"));
            assertTrue("DAO SuDungDichVu.findByDatPhong", !new SuDungDichVuDAO().findByDatPhong(em, "DP003").isEmpty());
            assertTrue("DAO ThanhToan.findByDatPhong", new ThanhToanDAO().findByDatPhong(em, "DP004").size() == 2);
            assertTrue("DAO CtDatPhong.existsPhongTrungLich", new CtDatPhongDAO().existsPhongTrungLich(em, "P103", LocalDate.of(2026, 9, 24), LocalDate.of(2026, 9, 25)));
        } finally {
            em.close();
        }
    }

    private void testServiceRead() {
        section("2. SERVICE");
        assertNotNull("KhachHangService.findById", new com.hotel.service.KhachHangService().findById("KH001"));
        assertNotNull("NhanVienService.findById", new com.hotel.service.NhanVienService().findById("NV001"));
        assertNotNull("LoaiPhongService.findById", new com.hotel.service.LoaiPhongService().findById("LP01"));
        assertNotNull("PhongService.findById", new com.hotel.service.PhongService().findById("P101"));
        assertNotNull("DichVuService.findById", new com.hotel.service.DichVuService().findById("DV01"));
        assertNotNull("DatPhongService.findById", new DatPhongService().findById("DP003"));
        assertNotNull("HoaDonService.findById", new HoaDonService().findById("HD001"));
        assertNotNull("ThanhToanService.findById", new ThanhToanService().findById("TT001"));
    }

    private void testBookingFlow() {
        section("3. NGHIỆP VỤ ĐẶT PHÒNG");
        DatPhongService service = new DatPhongService();

        DatPhong dp = new DatPhong();
        dp.setMaDatPhong(BOOKING_1);
        KhachHang kh = new KhachHang();
        kh.setMaKH("KH001");
        dp.setKhachHang(kh);
        dp.setGhiChu("Integration test booking");

        CtDatPhong ct = new CtDatPhong();
        ct.setMaCTDatPhong(CT_BOOKING_1);
        Phong phong = new Phong();
        phong.setMaPhong("P201");
        ct.setPhong(phong);
        ct.setNgayNhan(LocalDate.of(2026, 10, 10));
        ct.setNgayTra(LocalDate.of(2026, 10, 12));
        ct.setGiaPhong(new BigDecimal("800000"));

        service.datPhong(dp, List.of(ct));
        DatPhong saved = service.findById(BOOKING_1);
        assertEquals("Đặt phòng -> CHO_XAC_NHAN", TrangThaiDatPhong.CHO_XAC_NHAN, saved.getTrangThai());
        assertEquals("Phòng -> DA_DAT", TrangThaiPhong.DA_DAT, new com.hotel.service.PhongService().findById("P201").getTrangThai());

        service.xacNhanDatPhong(BOOKING_1);
        assertEquals("Xác nhận -> DA_XAC_NHAN", TrangThaiDatPhong.DA_XAC_NHAN, service.findById(BOOKING_1).getTrangThai());

        service.checkIn(BOOKING_1);
        assertEquals("Check-in -> DANG_O", TrangThaiDatPhong.DANG_O, service.findById(BOOKING_1).getTrangThai());
        assertEquals("Check-in phòng -> DANG_SU_DUNG", TrangThaiPhong.DANG_SU_DUNG, new com.hotel.service.PhongService().findById("P201").getTrangThai());
    }

    private void testHoaDonAndPaymentFlow() {
        section("4. HÓA ĐƠN + THANH TOÁN");

        SuDungDichVu use = new SuDungDichVu();
        use.setMaSuDungDichVu(SERVICE_USE_1);
        DatPhong dp = new DatPhong(); dp.setMaDatPhong(BOOKING_1); use.setDatPhong(dp);
        DichVu dv = new DichVu(); dv.setMaDichVu("DV01"); use.setDichVu(dv);
        use.setSoLuong(2);
        new SuDungDichVuService().them(use);

        HoaDonService hoaDonService = new HoaDonService();
        HoaDon hoaDon = hoaDonService.taoHoaDon(BOOKING_1);
        assertEquals("Tiền phòng = 2 đêm x 800.000", new BigDecimal("1600000.00"), hoaDon.getTongTienPhong());
        assertEquals("Tiền dịch vụ = 2 x 15.000", new BigDecimal("30000.00"), hoaDon.getTongTienDichVu());
        assertEquals("Tổng hóa đơn", new BigDecimal("1630000.00"), hoaDon.getTongTien());

        hoaDonService.phatHanhHoaDon(hoaDon.getMaHoaDon());
        assertEquals("Phát hành hóa đơn", com.hotel.enums.TrangThaiHoaDon.DA_PHAT_HANH, hoaDonService.findById(hoaDon.getMaHoaDon()).getTrangThai());

        ThanhToan payment = new ThanhToan();
        DatPhong paymentBooking = new DatPhong(); paymentBooking.setMaDatPhong(BOOKING_1); payment.setDatPhong(paymentBooking);
        payment.setSoTien(hoaDon.getTongTien());
        payment.setPhuongThuc(PhuongThucThanhToan.VNPAY);
        new ThanhToanService().taoThanhToan(payment);
        assertEquals("Tạo thanh toán -> CHO_THANH_TOAN", TrangThaiThanhToan.CHO_THANH_TOAN, new ThanhToanService().findById(payment.getMaThanhToan()).getTrangThai());

        ThanhToanService paymentService = new ThanhToanService();
        paymentService.thanhToanThanhCong(payment.getMaThanhToan(), "TEST-VNPAY-001");
        ThanhToan paid = paymentService.findById(payment.getMaThanhToan());
        assertEquals("Thanh toán thành công", TrangThaiThanhToan.DA_THANH_TOAN, paid.getTrangThai());
        assertEquals("Mã giao dịch", "TEST-VNPAY-001", paid.getMaGiaoDich());
        assertNotNull("Thời gian thanh toán", paid.getThoiGianThanhToan());

        paymentService.hoanTien(payment.getMaThanhToan());
        assertEquals("Hoàn tiền", TrangThaiThanhToan.DA_HOAN_TIEN, paymentService.findById(payment.getMaThanhToan()).getTrangThai());
    }

    private void testCheckoutFlow() {
        section("5. CHECKOUT");
        DatPhongService bookingService = new DatPhongService();

        DatPhong dp = new DatPhong();
        dp.setMaDatPhong(BOOKING_2);
        KhachHang kh = new KhachHang(); kh.setMaKH("KH002"); dp.setKhachHang(kh);
        CtDatPhong ct = new CtDatPhong();
        ct.setMaCTDatPhong(CT_BOOKING_2);
        Phong phong = new Phong(); phong.setMaPhong("P203"); ct.setPhong(phong);
        ct.setNgayNhan(LocalDate.of(2026, 10, 15));
        ct.setNgayTra(LocalDate.of(2026, 10, 17));
        ct.setGiaPhong(new BigDecimal("800000"));
        bookingService.datPhong(dp, List.of(ct));
        bookingService.xacNhanDatPhong(BOOKING_2);
        bookingService.checkIn(BOOKING_2);

        HoaDon hoaDon = new CheckoutService().checkout(BOOKING_2);
        assertEquals("Checkout tổng tiền phòng", new BigDecimal("1600000.00"), hoaDon.getTongTienPhong());
        assertEquals("Checkout tổng tiền dịch vụ", BigDecimal.ZERO.setScale(2), hoaDon.getTongTienDichVu());
        assertEquals("Checkout tổng tiền", new BigDecimal("1600000.00"), hoaDon.getTongTien());
        assertEquals("Checkout -> DA_TRA_PHONG", TrangThaiDatPhong.DA_TRA_PHONG, bookingService.findById(BOOKING_2).getTrangThai());
        assertEquals("Checkout phòng -> TRONG", TrangThaiPhong.TRONG, new com.hotel.service.PhongService().findById("P203").getTrangThai());
    }

    private void cleanup() {
        System.out.println("\n--- CLEANUP TEST DATA ---");
        EntityManager em = null;
        try {
            TransactionManager tm = new TransactionManager();
            em = tm.getEntityManager();
            tm.begin();

            ThanhToanDAO thanhToanDAO = new ThanhToanDAO();
            HoaDonDAO hoaDonDAO = new HoaDonDAO();
            CtHoaDonDAO ctHoaDonDAO = new CtHoaDonDAO();
            SuDungDichVuDAO suDungDichVuDAO = new SuDungDichVuDAO();
            CtDatPhongDAO ctDatPhongDAO = new CtDatPhongDAO();
            DatPhongDAO datPhongDAO = new DatPhongDAO();
            PhongDAO phongDAO = new PhongDAO();

            for (ThanhToan t : thanhToanDAO.findByDatPhong(em, BOOKING_1)) thanhToanDAO.delete(em, t);
            for (ThanhToan t : thanhToanDAO.findByDatPhong(em, BOOKING_2)) thanhToanDAO.delete(em, t);
            for (HoaDon h : hoaDonDAO.findByDatPhong(em, BOOKING_1)) {
                for (var ct : ctHoaDonDAO.findAll(em)) if (ct.getHoaDon() != null && h.getMaHoaDon().equals(ct.getHoaDon().getMaHoaDon())) ctHoaDonDAO.delete(em, ct);
                hoaDonDAO.delete(em, h);
            }
            for (HoaDon h : hoaDonDAO.findByDatPhong(em, BOOKING_2)) {
                for (var ct : ctHoaDonDAO.findAll(em)) if (ct.getHoaDon() != null && h.getMaHoaDon().equals(ct.getHoaDon().getMaHoaDon())) ctHoaDonDAO.delete(em, ct);
                hoaDonDAO.delete(em, h);
            }
            for (SuDungDichVu s : suDungDichVuDAO.findByDatPhong(em, BOOKING_1)) suDungDichVuDAO.delete(em, s);
            for (CtDatPhong c : ctDatPhongDAO.findByDatPhong(em, BOOKING_1)) ctDatPhongDAO.delete(em, c);
            for (CtDatPhong c : ctDatPhongDAO.findByDatPhong(em, BOOKING_2)) ctDatPhongDAO.delete(em, c);
            DatPhong d1 = datPhongDAO.findById(em, BOOKING_1); if (d1 != null) datPhongDAO.delete(em, d1);
            DatPhong d2 = datPhongDAO.findById(em, BOOKING_2); if (d2 != null) datPhongDAO.delete(em, d2);
            Phong p201 = phongDAO.findById(em, "P201"); if (p201 != null) { p201.setTrangThai(TrangThaiPhong.TRONG); phongDAO.update(em, p201); }
            Phong p203 = phongDAO.findById(em, "P203"); if (p203 != null) { p203.setTrangThai(TrangThaiPhong.TRONG); phongDAO.update(em, p203); }
            tm.commit();
            tm.close();
            System.out.println("Cleanup OK");
        } catch (Throwable e) {
            if (em != null && em.getTransaction().isActive()) em.getTransaction().rollback();
            if (em != null && em.isOpen()) em.close();
            System.err.println("Cleanup FAILED: " + e.getMessage());
        }
    }

    private void section(String name) { System.out.println("\n--- " + name + " ---"); }
    private void assertNotNull(String name, Object value) { if (value == null) fail(name, "expected non-null"); else pass(name); }
    private void assertTrue(String name, boolean value) { if (!value) fail(name, "expected true"); else pass(name); }
    private void assertEquals(String name, Object expected, Object actual) { if (!java.util.Objects.equals(expected, actual)) fail(name, "expected=" + expected + ", actual=" + actual); else pass(name); }
    private void pass(String name) { passed++; System.out.println("[PASS] " + name); }
    private void fail(String name, Throwable e) { failed++; System.err.println("[FAIL] " + name + ": " + e.getMessage()); }
    private void fail(String name, String message) { failed++; System.err.println("[FAIL] " + name + ": " + message); }
}
