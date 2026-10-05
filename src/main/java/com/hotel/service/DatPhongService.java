package com.hotel.service;

import com.hotel.config.TransactionManager;
import com.hotel.dao.CtDatPhongDAO;
import com.hotel.dao.DatPhongDAO;
import com.hotel.dao.PhongDAO;
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

    private final DatPhongDAO datPhongDAO;
    private final CtDatPhongDAO ctDatPhongDAO;
    private final PhongDAO phongDAO;

    public DatPhongService() {
        this.datPhongDAO = new DatPhongDAO();
        this.ctDatPhongDAO = new CtDatPhongDAO();
        this.phongDAO = new PhongDAO();
    }

    /**
     * Tạo một đặt phòng mới.
     *
     * Quy trình:
     * 1. Kiểm tra ngày nhận/trả.
     * 2. Kiểm tra phòng tồn tại.
     * 3. Kiểm tra phòng có bị trùng lịch.
     * 4. Tạo DatPhong.
     * 5. Tạo CtDatPhong.
     * 6. Cập nhật trạng thái phòng.
     * 7. Commit toàn bộ transaction.
     */
    public void datPhong(
            DatPhong datPhong,
            List<CtDatPhong> danhSachChiTiet) {

        validateDatPhong(datPhong, danhSachChiTiet);

        TransactionManager transactionManager =
                new TransactionManager();

        try {
            transactionManager.begin();

            EntityManager em =
                    transactionManager.getEntityManager();

            String maKH = datPhong.getKhachHang().getMaKH();
            com.hotel.dao.KhachHangDAO khachHangDAO = new com.hotel.dao.KhachHangDAO();
            com.hotel.entity.KhachHang khachHang = khachHangDAO.findById(em, maKH);
            if (khachHang == null) {
                throw new IllegalArgumentException("Không tìm thấy khách hàng: " + maKH);
            }
            datPhong.setKhachHang(khachHang);

            // Kiểm tra toàn bộ phòng trước khi lưu bất kỳ dữ liệu nào.
            Set<String> maPhongTrongDatPhong = new HashSet<>();
            for (CtDatPhong chiTiet : danhSachChiTiet) {

                if (chiTiet.getPhong() == null
                        || chiTiet.getPhong().getMaPhong() == null) {

                    throw new IllegalArgumentException(
                            "Chi tiết đặt phòng chưa có phòng."
                    );
                }

                String maPhong = chiTiet.getPhong().getMaPhong();
                if (!maPhongTrongDatPhong.add(maPhong)) {
                    throw new IllegalArgumentException("Một đặt phòng không được chứa cùng một phòng nhiều lần: " + maPhong);
                }

                Phong phong =
                        phongDAO.findById(em, maPhong);

                if (phong == null) {
                    throw new IllegalArgumentException(
                            "Không tìm thấy phòng có mã: "
                                    + maPhong
                    );
                }

                boolean biTrungLich =
                        ctDatPhongDAO.existsPhongTrungLich(
                                em,
                                maPhong,
                                chiTiet.getNgayNhan(),
                                chiTiet.getNgayTra()
                        );

                if (biTrungLich) {
                    throw new IllegalArgumentException(
                            "Phòng "
                                    + phong.getSoPhong()
                                    + " đã có lịch trong khoảng "
                                    + chiTiet.getNgayNhan()
                                    + " đến "
                                    + chiTiet.getNgayTra()
                    );
                }

                // Gắn entity Phong đang được quản lý bởi EntityManager.
                chiTiet.setPhong(phong);
            }

            /*
             * Trạng thái ban đầu của một đặt phòng mới.
             *
             * Trường hợp khách đặt online:
             * CHO_XAC_NHAN
             */
            datPhong.setTrangThai(
                    TrangThaiDatPhong.CHO_XAC_NHAN
            );
            if (datPhong.getNgayDat() == null) {
                datPhong.setNgayDat(java.time.LocalDateTime.now());
            }
            if (datPhong.getMaDatPhong() == null || datPhong.getMaDatPhong().isBlank()) {
                datPhong.setMaDatPhong(MaCodeGenerator.nextId(
                        datPhongDAO.findAll(em), "maDatPhong", "DP"));
            }

            datPhongDAO.save(em, datPhong);

            /*
             * DatPhong đã được persist.
             * Các CtDatPhong có thể tham chiếu đến nó.
             */
            Set<String> maCtDaSinh = new HashSet<>();
            for (CtDatPhong chiTiet : danhSachChiTiet) {
                chiTiet.setDatPhong(datPhong);
                if (chiTiet.getMaCTDatPhong() == null || chiTiet.getMaCTDatPhong().isBlank()) {
                    chiTiet.setMaCTDatPhong(MaCodeGenerator.nextId(
                            ctDatPhongDAO.findAll(em), "maCTDatPhong", "CT", maCtDaSinh));
                }
                maCtDaSinh.add(chiTiet.getMaCTDatPhong());
                ctDatPhongDAO.save(em, chiTiet);
            }

            /*
             * Không đổi phòng sang DANG_SU_DUNG ở thời điểm đặt.
             *
             * Phòng chỉ thực sự đang sử dụng khi khách check-in.
             *
             * Nếu hệ thống muốn thể hiện phòng đã được đặt,
             * có thể sử dụng DA_DAT.
             */
            for (CtDatPhong chiTiet : danhSachChiTiet) {

                Phong phong = chiTiet.getPhong();

                phong.setTrangThai(
                        TrangThaiPhong.DA_DAT
                );

                phongDAO.update(em, phong);
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
     * Lấy một đặt phòng theo mã.
     */
    public DatPhong findById(String maDatPhong) {

        TransactionManager transactionManager =
                new TransactionManager();

        try {
            EntityManager em =
                    transactionManager.getEntityManager();

            return datPhongDAO.findById(
                    em,
                    maDatPhong
            );

        } finally {

            transactionManager.close();
        }
    }

    /**
     * Lấy danh sách đặt phòng của một khách hàng.
     */
    public List<DatPhong> findByKhachHang(String maKH) {

        TransactionManager transactionManager =
                new TransactionManager();

        try {
            EntityManager em =
                    transactionManager.getEntityManager();

            return datPhongDAO.findByKhachHang(
                    em,
                    maKH
            );

        } finally {

            transactionManager.close();
        }
    }

    /** Lấy danh sách đặt phòng kèm khách hàng để phục vụ MVC. */
    public List<DatPhong> findAllWithKhachHang() {
        TransactionManager transactionManager = new TransactionManager();
        try {
            return datPhongDAO.findAllWithKhachHang(
                    transactionManager.getEntityManager());
        } finally {
            transactionManager.close();
        }
    }

    /**
     * Lấy danh sách đặt phòng theo trạng thái.
     */
    public List<DatPhong> findByTrangThai(
            TrangThaiDatPhong trangThai) {

        TransactionManager transactionManager =
                new TransactionManager();

        try {
            EntityManager em =
                    transactionManager.getEntityManager();

            return datPhongDAO.findByTrangThai(
                    em,
                    trangThai
            );

        } finally {

            transactionManager.close();
        }
    }

    /**
     * Hủy đặt phòng.
     */
    public void huyDatPhong(String maDatPhong) {

        TransactionManager transactionManager =
                new TransactionManager();

        try {
            transactionManager.begin();

            EntityManager em =
                    transactionManager.getEntityManager();

            DatPhong datPhong =
                    datPhongDAO.findById(
                            em,
                            maDatPhong
                    );

            if (datPhong == null) {
                throw new IllegalArgumentException(
                        "Không tìm thấy đặt phòng: "
                                + maDatPhong
                );
            }

            if (datPhong.getTrangThai()
                    == TrangThaiDatPhong.DA_TRA_PHONG) {

                throw new IllegalStateException(
                        "Đặt phòng đã trả phòng, không thể hủy."
                );
            }

            if (datPhong.getTrangThai()
                    == TrangThaiDatPhong.DA_HUY) {

                throw new IllegalStateException(
                        "Đặt phòng đã được hủy trước đó."
                );
            }

            datPhong.setTrangThai(
                    TrangThaiDatPhong.DA_HUY
            );

            datPhongDAO.update(em, datPhong);

            /*
             * Sau khi hủy, các phòng thuộc đặt phòng này
             * có thể trở lại trạng thái TRONG.
             */
            for (CtDatPhong chiTiet
                    : datPhong.getChiTietDatPhong()) {

                Phong phong = chiTiet.getPhong();

                if (phong != null
                        && phong.getTrangThai()
                        == TrangThaiPhong.DA_DAT) {

                    phong.setTrangThai(
                            TrangThaiPhong.TRONG
                    );

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
     * Kiểm tra dữ liệu đầu vào khi đặt phòng.
     */
    private void validateDatPhong(
            DatPhong datPhong,
            List<CtDatPhong> danhSachChiTiet) {

        if (datPhong == null) {
            throw new IllegalArgumentException(
                    "Thông tin đặt phòng không được null."
            );
        }

        if (datPhong.getKhachHang() == null) {
            throw new IllegalArgumentException(
                    "Đặt phòng phải có khách hàng."
            );
        }

        if (danhSachChiTiet == null
                || danhSachChiTiet.isEmpty()) {

            throw new IllegalArgumentException(
                    "Đặt phòng phải có ít nhất một phòng."
            );
        }

        for (CtDatPhong chiTiet : danhSachChiTiet) {

            if (chiTiet == null) {
                throw new IllegalArgumentException(
                        "Chi tiết đặt phòng không hợp lệ."
                );
            }

            LocalDate ngayNhan =
                    chiTiet.getNgayNhan();

            LocalDate ngayTra =
                    chiTiet.getNgayTra();

            if (ngayNhan == null
                    || ngayTra == null) {

                throw new IllegalArgumentException(
                        "Ngày nhận và ngày trả không được null."
                );
            }

            if (!ngayTra.isAfter(ngayNhan)) {

                throw new IllegalArgumentException(
                        "Ngày trả phải sau ngày nhận."
                );
            }

            if (chiTiet.getGiaPhong() == null) {

                throw new IllegalArgumentException(
                        "Giá phòng không được null."
                );
            }
        }
    }

    public void xacNhanDatPhong(String maDatPhong) {

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

            datPhong.setTrangThai(
                    TrangThaiDatPhong.DA_XAC_NHAN
            );

            datPhongDAO.update(em, datPhong);

            transactionManager.commit();

        } catch (Exception e) {

            transactionManager.rollback();
            throw e;

        } finally {

            transactionManager.close();
        }
    }

    public void checkIn(String maDatPhong) {

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

            datPhong.setTrangThai(
                    TrangThaiDatPhong.DANG_O
            );

            datPhongDAO.update(em, datPhong);

            transactionManager.commit();

        } catch (Exception e) {

            transactionManager.rollback();
            throw e;

        } finally {

            transactionManager.close();
        }
    }

    public void checkOut(String maDatPhong) {

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

            datPhong.setTrangThai(
                    TrangThaiDatPhong.DA_TRA_PHONG
            );

            datPhongDAO.update(em, datPhong);

            transactionManager.commit();

        } catch (Exception e) {

            transactionManager.rollback();
            throw e;

        } finally {

            transactionManager.close();
        }
    }
}