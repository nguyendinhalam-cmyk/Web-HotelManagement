package com.hotel.service;

import com.hotel.config.TransactionManager;
import com.hotel.dao.CtDatPhongDAO;
import com.hotel.dao.DatPhongDAO;
import com.hotel.dao.PhongDAO;
import com.hotel.dao.NhanVienDAO;
import com.hotel.dao.HoaDonDAO;
import com.hotel.dao.CtHoaDonDAO;
import com.hotel.entity.NhanVien;
import com.hotel.entity.HoaDon;
import com.hotel.entity.CtHoaDon;
import com.hotel.enums.TrangThaiHoaDon;
import com.hotel.entity.CtDatPhong;
import com.hotel.entity.DatPhong;
import com.hotel.entity.Phong;
import com.hotel.enums.TrangThaiDatPhong;
import com.hotel.enums.TrangThaiPhong;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.hotel.util.MaCodeGenerator;

public class DatPhongService {

    // Khởi tạo các DAO cần thiết để thao tác với bảng DatPhong, CtDatPhong và Phong
    private final DatPhongDAO datPhongDAO;
    private final CtDatPhongDAO ctDatPhongDAO;
    private final PhongDAO phongDAO;
    // [master] Nhân viên xử lý + hóa đơn nháp
    private final NhanVienDAO nhanVienDAO;
    private final HoaDonDAO hoaDonDAO;
    private final CtHoaDonDAO ctHoaDonDAO;

    public DatPhongService() {
        this.datPhongDAO = new DatPhongDAO();
        this.ctDatPhongDAO = new CtDatPhongDAO();
        this.phongDAO = new PhongDAO();
        this.nhanVienDAO = new NhanVienDAO();
        this.hoaDonDAO = new HoaDonDAO();
        this.ctHoaDonDAO = new CtHoaDonDAO();
    }

    /**
     * TẠO ĐẶT PHÒNG (Bản nạp chồng 3 tham số - giữ đúng chữ ký của nhóm trưởng trên master)
     * Không truyền số lượng khách -> không kiểm tra sức chứa.
     */
    public void datPhong(
            DatPhong datPhong,
            List<CtDatPhong> danhSachChiTiet,
            String maNVXuLy) {
        datPhong(datPhong, danhSachChiTiet, maNVXuLy, null);
    }

    /**
     * NGHIỆP VỤ 2 CỐT LÕI CỦA TV2: TẠO ĐẶT PHÒNG
     * Có quản lý Transaction (ACID), ghi nhận nhân viên xử lý, kiểm tra sức chứa
     * phòng theo số lượng khách và tạo hóa đơn nháp.
     */
    public void datPhong(
            DatPhong datPhong,
            List<CtDatPhong> danhSachChiTiet,
            String maNVXuLy,
            Integer soLuongKhach) {

        // 1. Kiểm tra tính hợp lệ cơ bản của dữ liệu (không null, có nhân viên, ngày hợp lệ, ghi chú <= 500 ký tự)
        validateDatPhong(datPhong, danhSachChiTiet, maNVXuLy);

        if (soLuongKhach != null && soLuongKhach <= 0) {
            throw new IllegalArgumentException("Số lượng khách phải lớn hơn 0.");
        }

        TransactionManager transactionManager = new TransactionManager();

        try {
            transactionManager.begin(); // Bắt đầu Transaction
            EntityManager em = transactionManager.getEntityManager();

            // [master] Ghi nhận nhân viên đang đăng nhập là người xử lý đặt phòng
            NhanVien nhanVienXuLy = nhanVienDAO.findById(em, maNVXuLy);
            if (nhanVienXuLy == null) {
                throw new IllegalArgumentException("Không tìm thấy nhân viên xử lý: " + maNVXuLy);
            }
            datPhong.setNhanVienXuLy(nhanVienXuLy);

            // 2. Kiểm tra xem khách hàng có tồn tại trong hệ thống hay không
            String maKH = datPhong.getKhachHang().getMaKH();
            com.hotel.dao.KhachHangDAO khachHangDAO = new com.hotel.dao.KhachHangDAO();
            com.hotel.entity.KhachHang khachHang = khachHangDAO.findById(em, maKH);
            if (khachHang == null) {
                throw new IllegalArgumentException("Không tìm thấy khách hàng: " + maKH);
            }
            datPhong.setKhachHang(khachHang);

            // 3. Kiểm tra danh sách phòng được chọn trước khi lưu
            Set<String> maPhongTrongDatPhong = new HashSet<>();
            int tongSucChua = 0; // Cộng dồn sức chứa tối đa của các phòng đã chọn

            for (CtDatPhong chiTiet : danhSachChiTiet) {
                if (chiTiet.getPhong() == null || chiTiet.getPhong().getMaPhong() == null) {
                    throw new IllegalArgumentException("Chi tiết đặt phòng chưa có phòng.");
                }

                String maPhong = chiTiet.getPhong().getMaPhong();
                // Dùng Set để ngăn người dùng chọn trùng 1 phòng 2 lần trong cùng một đơn
                if (!maPhongTrongDatPhong.add(maPhong)) {
                    throw new IllegalArgumentException("Một đặt phòng không được chứa cùng một phòng nhiều lần: " + maPhong);
                }

                Phong phong = phongDAO.findById(em, maPhong);
                if (phong == null) {
                    throw new IllegalArgumentException("Không tìm thấy phòng có mã: " + maPhong);
                }

                // Kiểm tra trùng lịch: Gọi hàm DAO quét xem phòng này đã có đơn khác giữ chỗ trong khoảng ngày này chưa
                boolean biTrungLich = ctDatPhongDAO.existsPhongTrungLich(
                        em, maPhong, chiTiet.getNgayNhan(), chiTiet.getNgayTra()
                );

                if (biTrungLich) {
                    throw new IllegalArgumentException(
                            "Phòng " + phong.getSoPhong()
                                    + " đã có lịch trong khoảng " + chiTiet.getNgayNhan()
                                    + " đến " + chiTiet.getNgayTra()
                    );
                }

                // Gắn thực thể phòng Managed vào chi tiết
                chiTiet.setPhong(phong);

                // Lấy số người tối đa từ bảng Loại Phòng cộng vào tổng sức chứa
                tongSucChua += phong.getLoaiPhong().getSoNguoiToiDa();
            }

            // 4. Kiểm tra sức chứa: Số khách đi cùng không được vượt quá tổng sức chứa các phòng
            if (soLuongKhach != null && soLuongKhach > tongSucChua) {
                throw new IllegalArgumentException(
                        "Số lượng khách (" + soLuongKhach
                                + ") vượt quá sức chứa tối đa của các phòng đã chọn ("
                                + tongSucChua + " người)."
                );
            }

            // 5. Thiết lập thông tin đơn đặt phòng và lưu vào bảng DatPhong
            datPhong.setTrangThai(TrangThaiDatPhong.CHO_XAC_NHAN);
            if (datPhong.getNgayDat() == null) {
                datPhong.setNgayDat(java.time.LocalDateTime.now());
            }
            if (datPhong.getMaDatPhong() == null || datPhong.getMaDatPhong().isBlank()) {
                datPhong.setMaDatPhong(MaCodeGenerator.nextId(datPhongDAO.findAll(em), "maDatPhong", "DP"));
            }
            datPhongDAO.save(em, datPhong);

            // 6. Lưu từng dòng Chi Tiết Đặt Phòng (bảng CtDatPhong)
            Set<String> maCtDaSinh = new HashSet<>();
            for (CtDatPhong chiTiet : danhSachChiTiet) {
                chiTiet.setDatPhong(datPhong); // Thiết lập khóa ngoại liên kết tới DatPhong vừa tạo
                if (chiTiet.getMaCTDatPhong() == null || chiTiet.getMaCTDatPhong().isBlank()) {
                    chiTiet.setMaCTDatPhong(MaCodeGenerator.nextId(
                            ctDatPhongDAO.findAll(em), "maCTDatPhong", "CT", maCtDaSinh));
                }
                maCtDaSinh.add(chiTiet.getMaCTDatPhong());
                ctDatPhongDAO.save(em, chiTiet);
            }

            // [master] Theo nghiệp vụ: 1 đặt phòng có đúng 1 hóa đơn, tạo ngay ở trạng thái NHAP.
            taoHoaDonNhap(em, datPhong, danhSachChiTiet);

            // 7. Cập nhật trạng thái vật lý của phòng:
            // Chỉ đổi TRONG -> DA_DAT. Nếu khách đang ở (DANG_SU_DUNG), tuyệt đối không ghi đè trạng thái
            for (CtDatPhong chiTiet : danhSachChiTiet) {
                Phong phong = chiTiet.getPhong();
                if (phong.getTrangThai() == TrangThaiPhong.TRONG) {
                    phong.setTrangThai(TrangThaiPhong.DA_DAT);
                    phongDAO.update(em, phong);
                }
            }

            transactionManager.commit(); // Hoàn tất toàn bộ thao tác an toàn
        } catch (Exception e) {
            transactionManager.rollback(); // Hoàn tác dữ liệu nếu có lỗi bất kỳ
            throw e;
        } finally {
            transactionManager.close();
        }
    }

    /**
     * Tìm đơn đặt phòng theo mã
     */
    public DatPhong findById(String maDatPhong) {
        TransactionManager transactionManager = new TransactionManager();
        try {
            return datPhongDAO.findById(transactionManager.getEntityManager(), maDatPhong);
        } finally {
            transactionManager.close();
        }
    }

    /**
     * Lấy lịch sử đặt phòng của một khách hàng (Hỗ trợ TV3 - Quản lý khách hàng)
     */
    public List<DatPhong> findByKhachHang(String maKH) {
        TransactionManager transactionManager = new TransactionManager();
        try {
            return datPhongDAO.findByKhachHang(transactionManager.getEntityManager(), maKH);
        } finally {
            transactionManager.close();
        }
    }

    /**
     * Lấy toàn bộ đơn đặt phòng kèm thông tin Khách hàng (Hiển thị trang danh sách mặc định)
     */
    public List<DatPhong> findAllWithKhachHang() {
        TransactionManager transactionManager = new TransactionManager();
        try {
            return datPhongDAO.findAllWithKhachHang(transactionManager.getEntityManager());
        } finally {
            transactionManager.close();
        }
    }

    /**
     * Lọc danh sách đơn đặt phòng theo trạng thái
     */
    public List<DatPhong> findByTrangThai(TrangThaiDatPhong trangThai) {
        TransactionManager transactionManager = new TransactionManager();
        try {
            return datPhongDAO.findByTrangThai(transactionManager.getEntityManager(), trangThai);
        } finally {
            transactionManager.close();
        }
    }

    /**
     * NGHIỆP VỤ 2 CỦA TV2: HỦY ĐẶT PHÒNG
     * Có kiểm tra logic trạng thái và hoàn trả trạng thái phòng thông minh
     */
    // TODO(TV2 - DISCUSS): Từ khi đặt phòng tự tạo hóa đơn nháp (master),
    // cần thống nhất với nhóm trưởng/TV5: khi hủy đặt phòng thì hóa đơn nháp xử lý thế nào.
    // Hiện tại hủy đặt phòng KHÔNG đụng tới hóa đơn.
    public void huyDatPhong(String maDatPhong) {
        TransactionManager transactionManager = new TransactionManager();
        try {
            transactionManager.begin();
            EntityManager em = transactionManager.getEntityManager();

            DatPhong datPhong = datPhongDAO.findById(em, maDatPhong);
            if (datPhong == null) {
                throw new IllegalArgumentException("Không tìm thấy đặt phòng: " + maDatPhong);
            }

            // Chặn các trường hợp vô lý khi hủy đơn:
            if (datPhong.getTrangThai() == TrangThaiDatPhong.DA_TRA_PHONG) {
                throw new IllegalStateException("Đặt phòng đã trả phòng, không thể hủy.");
            }
            if (datPhong.getTrangThai() == TrangThaiDatPhong.DA_HUY) {
                throw new IllegalStateException("Đặt phòng đã được hủy trước đó.");
            }
            if (datPhong.getTrangThai() == TrangThaiDatPhong.DANG_O) {
                throw new IllegalStateException("Khách đang ở, không thể hủy đặt phòng.");
            }

            // Đổi trạng thái đơn thành DA_HUY
            datPhong.setTrangThai(TrangThaiDatPhong.DA_HUY);
            datPhongDAO.update(em, datPhong);

            // Hoàn trả trạng thái phòng:
            // Chỉ trả về TRONG nếu không còn đơn đặt phòng nào KHÁC đang giữ phòng này
            for (CtDatPhong chiTiet : datPhong.getChiTietDatPhong()) {
                Phong phong = chiTiet.getPhong();
                if (phong != null
                        && phong.getTrangThai() == TrangThaiPhong.DA_DAT
                        && !ctDatPhongDAO.existsDatPhongKhacConGiuPhong(em, phong.getMaPhong(), maDatPhong)) {
                    phong.setTrangThai(TrangThaiPhong.TRONG);
                    phongDAO.update(em, phong);
                }
            }

            transactionManager.commit();
        } catch (Exception e) {
            transactionManager.rollback();
            throw e;
        } finally {
            transactionManager.close();
        }
    }

    /**
     * HÀM HỖ TRỢ VALIDATE DỮ LIỆU ĐẦU VÀO
     */
    private void validateDatPhong(DatPhong datPhong, List<CtDatPhong> danhSachChiTiet, String maNVXuLy) {
        if (datPhong == null) {
            throw new IllegalArgumentException("Thông tin đặt phòng không được null.");
        }
        // [master] Bắt buộc có nhân viên đăng nhập tiếp nhận đặt phòng
        if (maNVXuLy == null || maNVXuLy.isBlank()) {
            throw new IllegalArgumentException("Phải xác định nhân viên tiếp nhận đặt phòng.");
        }
        if (datPhong.getKhachHang() == null) {
            throw new IllegalArgumentException("Đặt phòng phải có khách hàng.");
        }
        if (danhSachChiTiet == null || danhSachChiTiet.isEmpty()) {
            throw new IllegalArgumentException("Đặt phòng phải có ít nhất một phòng.");
        }

        for (CtDatPhong chiTiet : danhSachChiTiet) {
            if (chiTiet == null) {
                throw new IllegalArgumentException("Chi tiết đặt phòng không hợp lệ.");
            }
            LocalDate ngayNhan = chiTiet.getNgayNhan();
            LocalDate ngayTra = chiTiet.getNgayTra();

            if (ngayNhan == null || ngayTra == null) {
                throw new IllegalArgumentException("Ngày nhận và ngày trả không được null.");
            }
            if (!ngayTra.isAfter(ngayNhan)) {
                throw new IllegalArgumentException("Ngày trả phải sau ngày nhận.");
            }
            if (chiTiet.getGiaPhong() == null) {
                throw new IllegalArgumentException("Giá phòng không được null.");
            }
        }

        // Kiểm tra độ dài ghi chú để không bị lỗi tràn trường SQL (VARCHAR(500))
        if (datPhong.getGhiChu() != null && datPhong.getGhiChu().length() > 500) {
            throw new IllegalArgumentException("Ghi chú tối đa 500 ký tự.");
        }
    }

    /**
     * NGHIỆP VỤ 2 CỦA TV2: TRA CỨU ĐẶT PHÒNG ĐA TIÊU CHÍ
     */
    public List<DatPhong> traCuu(
            String maDatPhong,
            String khachHang,
            TrangThaiDatPhong trangThai,
            LocalDate tuNgay,
            LocalDate denNgay) {

        TransactionManager transactionManager = new TransactionManager();
        try {
            return datPhongDAO.traCuu(
                    transactionManager.getEntityManager(),
                    maDatPhong, khachHang, trangThai, tuNgay, denNgay
            );
        } finally {
            transactionManager.close();
        }
    }

    /**
     * NGHIỆP VỤ 2 CỦA TV2: LẤY CHI TIẾT ĐẶT PHÒNG KÈM DANH SÁCH PHÒNG ĐÃ CHỌN
     */
    public DatPhong findByIdWithChiTiet(String maDatPhong) {
        TransactionManager transactionManager = new TransactionManager();
        try {
            return datPhongDAO.findByIdWithChiTiet(
                    transactionManager.getEntityManager(),
                    maDatPhong
            );
        } finally {
            transactionManager.close();
        }
    }

    // =====================================================================
    // CÁC HÀM DƯỚI ĐÂY LẤY NGUYÊN VĂN TỪ MASTER (nhóm trưởng / TV3 / TV5)
    // =====================================================================

    /**
     * XÁC NHẬN ĐẶT PHÒNG: Đổi trạng thái từ CHO_XAC_NHAN sang DA_XAC_NHAN, ghi nhận nhân viên
     */
    public void xacNhanDatPhong(String maDatPhong, String maNVXuLy) {

        TransactionManager transactionManager =
                new TransactionManager();

        try {
            transactionManager.begin();

            EntityManager em =
                    transactionManager.getEntityManager();

            DatPhong datPhong =
                    datPhongDAO.findById(em, maDatPhong);

            if (datPhong == null) {
                throw new IllegalArgumentException(
                        "Không tìm thấy đặt phòng: "
                                + maDatPhong
                );
            }

            if (datPhong.getTrangThai()
                    != TrangThaiDatPhong.CHO_XAC_NHAN) {

                throw new IllegalStateException(
                        "Chỉ có đặt phòng đang chờ xác nhận "
                                + "mới được xác nhận."
                );
            }

            NhanVien nhanVien = nhanVienDAO.findById(em, maNVXuLy);
            if (nhanVien == null) throw new IllegalArgumentException("Không tìm thấy nhân viên xử lý: " + maNVXuLy);
            datPhong.setNhanVienXuLy(nhanVien);
            datPhong.setTrangThai(TrangThaiDatPhong.DA_XAC_NHAN);

            datPhongDAO.update(em, datPhong);

            transactionManager.commit();

        } catch (Exception e) {

            transactionManager.rollback();
            throw e;

        } finally {

            transactionManager.close();
        }
    }

    /**
     * CHECK-IN (TV3) – ghi nhận nhân viên check-in
     */
    public void checkIn(String maDatPhong, String maNVCheckIn) {

        TransactionManager transactionManager =
                new TransactionManager();

        try {
            transactionManager.begin();

            EntityManager em =
                    transactionManager.getEntityManager();

            DatPhong datPhong =
                    datPhongDAO.findById(em, maDatPhong);

            if (datPhong == null) {
                throw new IllegalArgumentException(
                        "Không tìm thấy đặt phòng: "
                                + maDatPhong
                );
            }

            if (datPhong.getTrangThai()
                    != TrangThaiDatPhong.DA_XAC_NHAN) {

                throw new IllegalStateException(
                        "Đặt phòng chưa được xác nhận "
                                + "hoặc không thể check-in."
                );
            }

            for (CtDatPhong chiTiet
                    : datPhong.getChiTietDatPhong()) {

                Phong phong = chiTiet.getPhong();

                if (phong == null) {
                    throw new IllegalStateException(
                            "Chi tiết đặt phòng không có phòng."
                    );
                }

                phong.setTrangThai(
                        TrangThaiPhong.DANG_SU_DUNG
                );

                phongDAO.update(em, phong);
            }

            NhanVien nhanVien = nhanVienDAO.findById(em, maNVCheckIn);
            if (nhanVien == null) throw new IllegalArgumentException("Không tìm thấy nhân viên check-in: " + maNVCheckIn);
            datPhong.setNhanVienCheckIn(nhanVien);
            datPhong.setTrangThai(TrangThaiDatPhong.DANG_O);

            datPhongDAO.update(em, datPhong);

            transactionManager.commit();

        } catch (Exception e) {

            transactionManager.rollback();
            throw e;

        } finally {

            transactionManager.close();
        }
    }

    /**
     * CHECK-OUT (TV3 + TV5) – ghi nhận nhân viên check-out
     */
    public void checkOut(String maDatPhong, String maNVCheckOut) {

        TransactionManager transactionManager =
                new TransactionManager();

        try {
            transactionManager.begin();

            EntityManager em =
                    transactionManager.getEntityManager();

            DatPhong datPhong =
                    datPhongDAO.findById(em, maDatPhong);

            if (datPhong == null) {
                throw new IllegalArgumentException(
                        "Không tìm thấy đặt phòng: "
                                + maDatPhong
                );
            }

            if (datPhong.getTrangThai()
                    != TrangThaiDatPhong.DANG_O) {

                throw new IllegalStateException(
                        "Đặt phòng chưa ở trạng thái đang ở."
                );
            }

            for (CtDatPhong chiTiet
                    : datPhong.getChiTietDatPhong()) {

                Phong phong = chiTiet.getPhong();

                if (phong != null) {
                    phong.setTrangThai(
                            TrangThaiPhong.TRONG
                    );

                    phongDAO.update(em, phong);
                }
            }

            NhanVien nhanVien = nhanVienDAO.findById(em, maNVCheckOut);
            if (nhanVien == null) throw new IllegalArgumentException("Không tìm thấy nhân viên check-out: " + maNVCheckOut);
            datPhong.setNhanVienCheckOut(nhanVien);
            datPhong.setTrangThai(TrangThaiDatPhong.DA_TRA_PHONG);

            datPhongDAO.update(em, datPhong);

            transactionManager.commit();

        } catch (Exception e) {

            transactionManager.rollback();
            throw e;

        } finally {

            transactionManager.close();
        }
    }

    /** Tương thích các test/code cũ; code web phải truyền mã nhân viên. */
    public void datPhong(DatPhong datPhong, List<CtDatPhong> danhSachChiTiet) {
        throw new IllegalArgumentException("Đặt phòng phải được thực hiện bởi nhân viên đăng nhập.");
    }

    public void xacNhanDatPhong(String maDatPhong) {
        throw new IllegalArgumentException("Xác nhận đặt phòng phải có nhân viên đăng nhập.");
    }

    public void checkIn(String maDatPhong) {
        throw new IllegalArgumentException("Check-in phải có nhân viên đăng nhập.");
    }

    public void checkOut(String maDatPhong) {
        throw new IllegalArgumentException("Check-out phải có nhân viên đăng nhập.");
    }

    private void taoHoaDonNhap(EntityManager em, DatPhong datPhong, List<CtDatPhong> chiTiet) {
        HoaDon hoaDon = new HoaDon();
        hoaDon.setMaHoaDon(MaCodeGenerator.nextId(hoaDonDAO.findAll(em), "maHoaDon", "HD"));
        hoaDon.setDatPhong(datPhong);
        hoaDon.setNgayLap(java.time.LocalDateTime.now());
        java.math.BigDecimal tongPhong = java.math.BigDecimal.ZERO;
        Set<String> reservedCt = new HashSet<>();
        List<CtHoaDon> existingCt = ctHoaDonDAO.findAll(em);
        for (CtDatPhong ct : chiTiet) {
            long soDem = java.time.temporal.ChronoUnit.DAYS.between(ct.getNgayNhan(), ct.getNgayTra());
            java.math.BigDecimal tien = ct.getGiaPhong().multiply(java.math.BigDecimal.valueOf(soDem));
            tongPhong = tongPhong.add(tien);
            CtHoaDon line = new CtHoaDon();
            line.setMaCtHoaDon(MaCodeGenerator.nextId(existingCt, "maCtHoaDon", "CTHD", reservedCt));
            reservedCt.add(line.getMaCtHoaDon());
            line.setHoaDon(hoaDon);
            line.setTenKhoanThu("Tiền phòng " + (ct.getPhong() == null ? "" : ct.getPhong().getSoPhong()));
            line.setSoLuong(1); line.setDonGia(ct.getGiaPhong()); line.setThanhTien(tien);
            hoaDon.getChiTietHoaDon().add(line);
        }
        hoaDon.setTongTienPhong(tongPhong);
        hoaDon.setTongTienDichVu(java.math.BigDecimal.ZERO);
        hoaDon.setTongTien(tongPhong);
        hoaDon.setTrangThai(TrangThaiHoaDon.NHAP);
        hoaDonDAO.save(em, hoaDon);
        for (CtHoaDon line : hoaDon.getChiTietHoaDon()) ctHoaDonDAO.save(em, line);
    }

}