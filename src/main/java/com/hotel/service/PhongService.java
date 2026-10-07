package com.hotel.service;

import com.hotel.config.TransactionManager;
import com.hotel.dao.LoaiPhongDAO;
import com.hotel.dao.PhongDAO;
import com.hotel.entity.LoaiPhong;
import com.hotel.entity.Phong;
import com.hotel.enums.TrangThaiPhong;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import com.hotel.util.MaCodeGenerator;

public class PhongService {
    // Khởi tạo các DAO để tương tác với cơ sở dữ liệu
    private final PhongDAO dao = new PhongDAO();
    private final LoaiPhongDAO loaiPhongDAO = new LoaiPhongDAO();

    /**
     * THÊM PHÒNG MỚI (Nghiệp vụ TV1 - Quản lý phòng)
     * Có quản lý Transaction (begin -> commit/rollback -> close)
     */
    public void them(Phong x) {
        validate(x); // 1. Kiểm tra tính hợp lệ cơ bản của dữ liệu đầu vào
        TransactionManager tm = new TransactionManager();
        try {
            tm.begin(); // Bắt đầu giao dịch cơ sở dữ liệu
            EntityManager em = tm.getEntityManager();

            // 2. Tự động sinh mã phòng dạng "P001", "P002"... nếu chưa truyền mã
            if (x.getMaPhong() == null || x.getMaPhong().isBlank()) {
                x.setMaPhong(MaCodeGenerator.nextId(dao.findAll(em), "maPhong", "P"));
            }

            // 3. Kiểm tra trùng khóa chính (mã phòng)
            if (dao.findById(em, x.getMaPhong()) != null) {
                throw new IllegalArgumentException("Mã phòng đã tồn tại: " + x.getMaPhong());
            }

            // 4. Kiểm tra trùng số phòng thực tế (ví dụ: không thể có 2 phòng cùng số 101)
            if (dao.findBySoPhong(em, x.getSoPhong()) != null) {
                throw new IllegalArgumentException("Số phòng đã tồn tại: " + x.getSoPhong());
            }

            // 5. Kiểm tra loại phòng có tồn tại trong hệ thống hay không
            LoaiPhong lp = loaiPhongDAO.findById(em, x.getLoaiPhong().getMaLoaiPhong());
            if (lp == null) {
                throw new IllegalArgumentException("Không tìm thấy loại phòng.");
            }
            x.setLoaiPhong(lp);

            // 6. Gán trạng thái mặc định ban đầu là TRONG nếu chưa thiết lập
            if (x.getTrangThai() == null) {
                x.setTrangThai(TrangThaiPhong.TRONG);
            }

            // 7. Lưu vào DB và commit transaction
            dao.save(em, x);
            tm.commit();
        } catch (Exception e) {
            tm.rollback(); // Hoàn tác nếu có lỗi
            throw e;
        } finally {
            tm.close(); // Giải phóng kết nối EntityManager
        }
    }

    /**
     * Tìm phòng theo mã phòng (không tải kèm thông tin Loại phòng)
     */
    public Phong findById(String id) {
        TransactionManager tm = new TransactionManager();
        try {
            return dao.findById(tm.getEntityManager(), id);
        } finally {
            tm.close();
        }
    }

    /**
     * Tìm phòng theo mã phòng VÀ tải kèm Loại phòng (Eager load)
     * Thường dùng khi hiển thị chi tiết hoặc tính tiền dựa theo giá Loại phòng
     */
    public Phong findByIdWithLoaiPhong(String id) {
        TransactionManager tm = new TransactionManager();
        try {
            return dao.findByIdWithLoaiPhong(tm.getEntityManager(), id);
        } finally {
            tm.close();
        }
    }

    /**
     * Tìm phòng theo số phòng (ví dụ: "P101", "202")
     */
    public Phong findBySoPhong(String soPhong) {
        TransactionManager tm = new TransactionManager();
        try {
            return dao.findBySoPhong(tm.getEntityManager(), soPhong);
        } finally {
            tm.close();
        }
    }

    /**
     * Lấy toàn bộ danh sách phòng hiện có trong khách sạn
     */
    public List<Phong> findAll() {
        TransactionManager tm = new TransactionManager();
        try {
            return dao.findAll(tm.getEntityManager());
        } finally {
            tm.close();
        }
    }

    /**
     * Lấy danh sách các phòng thuộc một loại phòng cụ thể (ví dụ: tất cả phòng Single)
     */
    public List<Phong> findByLoaiPhong(String maLoaiPhong) {
        TransactionManager tm = new TransactionManager();
        try {
            return dao.findByLoaiPhong(tm.getEntityManager(), maLoaiPhong);
        } finally {
            tm.close();
        }
    }

    /**
     * TÌM PHÒNG CÓ THỂ ĐẶT - BẢN CƠ BẢN (Nghiệp vụ TV2)
     * Kiểm tra tính hợp lệ của ngày trước khi truy vấn DAO
     */
    public List<Phong> findPhongCoTheDat(LocalDate ngayNhan, LocalDate ngayTra) {
        // Kiểm tra logic thời gian: không null và ngày trả phải sau ngày nhận
        if (ngayNhan == null || ngayTra == null || !ngayTra.isAfter(ngayNhan)) {
            throw new IllegalArgumentException("Khoảng ngày không hợp lệ.");
        }
        TransactionManager tm = new TransactionManager();
        try {
            return dao.findPhongCoTheDat(tm.getEntityManager(), ngayNhan, ngayTra);
        } finally {
            tm.close();
        }
    }

    /**
     * TÌM PHÒNG CÓ THỂ ĐẶT - BẢN NÂNG CẤP ĐẦY ĐỦ (Nghiệp vụ 1 của TV2)
     * Kiểm tra phòng trống theo khoảng ngày [ngayNhan, ngayTra)
     * kết hợp lọc theo loại phòng, khoảng giá và tình trạng phòng
     */
    public List<Phong> findPhongCoTheDat(
            LocalDate ngayNhan,
            LocalDate ngayTra,
            String maLoaiPhong,
            BigDecimal giaTu,
            BigDecimal giaDen,
            TrangThaiPhong trangThai) {

        // Validate khoảng ngày bắt buộc
        if (ngayNhan == null || ngayTra == null || !ngayTra.isAfter(ngayNhan)) {
            throw new IllegalArgumentException("Khoảng ngày không hợp lệ.");
        }

        TransactionManager tm = new TransactionManager();
        try {
            // Gọi hàm truy vấn động bên PhongDAO
            return dao.findPhongCoTheDat(
                    tm.getEntityManager(),
                    ngayNhan,
                    ngayTra,
                    maLoaiPhong,
                    giaTu,
                    giaDen,
                    trangThai
            );
        } finally {
            tm.close();
        }
    }

    /**
     * CẬP NHẬT THÔNG TIN PHÒNG (Nghiệp vụ TV1)
     */
    public void capNhat(Phong x) {
        validate(x);
        TransactionManager tm = new TransactionManager();
        try {
            tm.begin();
            EntityManager em = tm.getEntityManager();

            // 1. Kiểm tra phòng cần cập nhật có tồn tại không
            Phong current = dao.findById(em, x.getMaPhong());
            if (current == null) {
                throw new IllegalArgumentException("Không tìm thấy phòng: " + x.getMaPhong());
            }

            // 2. Kiểm tra tránh trùng số phòng với phòng khác trong hệ thống
            Phong same = dao.findBySoPhong(em, x.getSoPhong());
            if (same != null && !same.getMaPhong().equals(x.getMaPhong())) {
                throw new IllegalArgumentException("Số phòng đã tồn tại: " + x.getSoPhong());
            }

            // 3. Kiểm tra loại phòng có hợp lệ không
            LoaiPhong lp = loaiPhongDAO.findById(em, x.getLoaiPhong().getMaLoaiPhong());
            if (lp == null) {
                throw new IllegalArgumentException("Không tìm thấy loại phòng.");
            }
            x.setLoaiPhong(lp);

            // 4. Cập nhật và commit
            dao.update(em, x);
            tm.commit();
        } catch (Exception e) {
            tm.rollback();
            throw e;
        } finally {
            tm.close();
        }
    }

    /**
     * CẬP NHẬT TRẠNG THÁI PHÒNG (Dùng khi Check-in, Check-out, hoặc Bảo trì)
     */
    public void capNhatTrangThai(String maPhong, TrangThaiPhong trangThai) {
        if (trangThai == null) {
            throw new IllegalArgumentException("Trạng thái phòng không được null.");
        }
        TransactionManager tm = new TransactionManager();
        try {
            tm.begin();
            EntityManager em = tm.getEntityManager();

            Phong x = dao.findById(em, maPhong);
            if (x == null) {
                throw new IllegalArgumentException("Không tìm thấy phòng: " + maPhong);
            }

            // Vì 'x' là thực thể Managed trong Hibernate, chỉ cần set là sẽ tự động đồng bộ khi commit
            x.setTrangThai(trangThai);
            tm.commit();
        } catch (Exception e) {
            tm.rollback();
            throw e;
        } finally {
            tm.close();
        }
    }

    /**
     * XÓA PHÒNG (Nghiệp vụ TV1)
     */
    public void xoa(String id) {
        TransactionManager tm = new TransactionManager();
        try {
            tm.begin();
            EntityManager em = tm.getEntityManager();

            Phong x = dao.findById(em, id);
            if (x == null) {
                throw new IllegalArgumentException("Không tìm thấy phòng: " + id);
            }

            dao.delete(em, x);
            tm.commit();
        } catch (Exception e) {
            tm.rollback();
            throw e;
        } finally {
            tm.close();
        }
    }

    /**
     * KIỂM TRA HỢP LỆ DỮ LIỆU ĐẦU VÀO (Private Helper)
     */
    private void validate(Phong x) {
        if (x == null
                || blank(x.getSoPhong())
                || x.getLoaiPhong() == null
                || blank(x.getLoaiPhong().getMaLoaiPhong())) {
            throw new IllegalArgumentException("Thông tin phòng không hợp lệ.");
        }
    }

    /**
     * Kiểm tra chuỗi rỗng hoặc khoảng trắng (Private Helper)
     */
    private boolean blank(String s) {
        return s == null || s.isBlank();
    }
}
