package com.hotel.dao;

// Import các thực thể Entity và Enum quản lý trạng thái
import com.hotel.entity.Phong;
import com.hotel.enums.TrangThaiDatPhong;
import com.hotel.enums.TrangThaiPhong;
// Import API quản lý kết nối và thực thi truy vấn của JPA
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * PhongDAO kế thừa từ BaseDAO:
 * - Kế thừa sẵn các hàm CRUD cơ bản: findById, findAll, insert, update, delete.
 * - Nhận 2 kiểu Generic: <Phong, String> tương ứng <Tên_Entity, Kiểu_Dữ_Liệu_Khóa_Chính (maPhong)>.
 */
public class PhongDAO extends BaseDAO<Phong, String> {

    public PhongDAO() {
        super(Phong.class); // Truyền Class metadata về cho BaseDAO xử lý EntityManager
    }

    /**
     * Tìm phòng theo mã phòng và TẢI KÈM luôn thông tin Loại Phòng (Eager Loading).
     * Dùng JOIN FETCH để tránh lỗi LazyInitializationException và tối ưu hiệu năng (chỉ chạy đúng 1 câu SQL).
     */
    public Phong findByIdWithLoaiPhong(EntityManager em, String maPhong) {
        String jpql = """
                SELECT p
                FROM Phong p
                JOIN FETCH p.loaiPhong
                WHERE p.maPhong = :maPhong
                """;
        List<Phong> result = em.createQuery(jpql, Phong.class)
                .setParameter("maPhong", maPhong)
                .getResultList();
        // Nếu không có kết quả trả về null, có thì lấy phần tử đầu tiên (tránh NonUniqueResultException)
        return result.isEmpty() ? null : result.get(0);
    }

    /**
     * Tra cứu phòng theo Số phòng hiển thị bên ngoài cửa (ví dụ: "P101", "P202")
     */
    public Phong findBySoPhong(
            EntityManager em,
            String soPhong) {

        String jpql = """
                SELECT p
                FROM Phong p
                WHERE p.soPhong = :soPhong
                """;

        List<Phong> result = em.createQuery(jpql, Phong.class)
                .setParameter("soPhong", soPhong)
                .getResultList();

        return result.isEmpty()
                ? null
                : result.get(0);
    }

    /**
     * Lấy toàn bộ danh sách phòng thuộc về một Loại phòng cụ thể (ví dụ: tất cả phòng VIP)
     */
    public List<Phong> findByLoaiPhong(
            EntityManager em,
            String maLoaiPhong) {

        String jpql = """
                SELECT p
                FROM Phong p
                WHERE p.loaiPhong.maLoaiPhong = :maLoaiPhong
                """;

        return em.createQuery(jpql, Phong.class)
                .setParameter("maLoaiPhong", maLoaiPhong)
                .getResultList();
    }

    /**
     * [BẢN CƠ BẢN]: Tìm danh sách phòng CÓ THỂ ĐẶT chỉ dựa vào khoảng ngày [ngayNhan, ngayTra)
     */
    public List<Phong> findPhongCoTheDat(
            EntityManager em,
            LocalDate ngayNhan,
            LocalDate ngayTra) {

        // Subquery: Lấy danh sách các phòng ĐÃ BỊ TRÙNG LỊCH ĐẶT
        // Công thức kiểm tra 2 khoảng thời gian giao nhau:
        // Lịch cũ bắt đầu trước khi đợt mới kết thúc VÀ lịch cũ kết thúc sau khi đợt mới bắt đầu
        String jpql = """
            SELECT p
            FROM Phong p
            JOIN FETCH p.loaiPhong
            WHERE p.maPhong NOT IN (
                SELECT c.phong.maPhong
                FROM CtDatPhong c
                WHERE c.datPhong.trangThai NOT IN :trangThaiKhongGiuPhong
                  AND c.ngayNhan < :ngayTra
                  AND c.ngayTra > :ngayNhan
            )
            ORDER BY p.soPhong
            """;

        // Ghi chú nghiệp vụ:
        // Đơn đã hủy (DA_HUY) hoặc đã hoàn tất trả phòng (DA_TRA_PHONG) thì coi như phòng đã giải phóng,
        // không được tính là giữ phòng nữa -> dùng chung hằng số TRANG_THAI_KHONG_GIU_PHONG
        return em.createQuery(jpql, Phong.class)
                .setParameter(
                        "trangThaiKhongGiuPhong",
                        CtDatPhongDAO.TRANG_THAI_KHONG_GIU_PHONG
                )
                .setParameter("ngayNhan", ngayNhan)
                .setParameter("ngayTra", ngayTra)
                .getResultList();
    }

    /**
     * [BẢN NÂNG CẤP ĐẦY ĐỦ CỦA TV2]: Tìm phòng trống kết hợp bộ lọc đa tiêu chí
     * Áp dụng kỹ thuật nối chuỗi JPQL động (Dynamic Query bằng StringBuilder).
     *
     * @param maLoaiPhong Lọc theo loại phòng (null = bỏ qua)
     * @param giaTu       Lọc giá cơ bản từ mức này trở lên (null = bỏ qua)
     * @param giaDen      Lọc giá cơ bản dưới mức này (null = bỏ qua)
     * @param trangThai   Lọc theo trạng thái vật lý thực tế của phòng hiện tại (Trống, Đang ở, Bảo trì...)
     */
    public List<Phong> findPhongCoTheDat(
            EntityManager em,
            LocalDate ngayNhan,
            LocalDate ngayTra,
            String maLoaiPhong,
            BigDecimal giaTu,
            BigDecimal giaDen,
            TrangThaiPhong trangThai) {

        // 1. Mệnh đề cơ sở: Loại bỏ các phòng đã có người đặt trùng khoảng ngày
        StringBuilder jpql = new StringBuilder("""
            SELECT p
            FROM Phong p
            JOIN FETCH p.loaiPhong lp
            WHERE p.maPhong NOT IN (
                SELECT c.phong.maPhong
                FROM CtDatPhong c
                WHERE c.datPhong.trangThai NOT IN :trangThaiKhongGiuPhong
                  AND c.ngayNhan < :ngayTra
                  AND c.ngayTra > :ngayNhan
            )
            """);

        // 2. Nối điều kiện lọc động: Người dùng nhập trường nào thì nối điều kiện trường đó
        if (maLoaiPhong != null) jpql.append(" AND lp.maLoaiPhong = :maLoaiPhong");
        if (giaTu != null) jpql.append(" AND lp.giaCoBan >= :giaTu");
        if (giaDen != null) jpql.append(" AND lp.giaCoBan <= :giaDen");
        if (trangThai != null) jpql.append(" AND p.trangThai = :trangThai");

        jpql.append(" ORDER BY p.soPhong");

        // 3. Khởi tạo TypedQuery từ chuỗi JPQL hoàn chỉnh
        TypedQuery<Phong> query = em.createQuery(jpql.toString(), Phong.class)
                .setParameter("trangThaiKhongGiuPhong", CtDatPhongDAO.TRANG_THAI_KHONG_GIU_PHONG)
                .setParameter("ngayNhan", ngayNhan)
                .setParameter("ngayTra", ngayTra);

        // 4. Gán giá trị thực tế vào các tham số đã nối
        if (maLoaiPhong != null) query.setParameter("maLoaiPhong", maLoaiPhong);
        if (giaTu != null) query.setParameter("giaTu", giaTu);
        if (giaDen != null) query.setParameter("giaDen", giaDen);
        if (trangThai != null) query.setParameter("trangThai", trangThai);

        // 5. Thực thi câu lệnh và trả về danh sách kết quả cho tầng Service
        return query.getResultList();
    }
}