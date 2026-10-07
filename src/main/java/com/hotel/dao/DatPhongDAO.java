package com.hotel.dao;

import com.hotel.entity.DatPhong;
import com.hotel.enums.TrangThaiDatPhong;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.util.List;

/**
 * DatPhongDAO kế thừa BaseDAO:
 * - Kế thừa các hàm CRUD cơ bản: save, update, delete, findById, findAll.
 * - Nhận cặp Generic: <DatPhong, String> tương ứng <Entity, Kiểu_Dữ_Liệu_Khóa_Chính (maDatPhong)>.
 */
public class DatPhongDAO extends BaseDAO<DatPhong, String> {

    public DatPhongDAO() {
        super(DatPhong.class);
    }

    /**
     * Lấy toàn bộ lịch sử đơn đặt phòng của một khách hàng cụ thể.
     * Sắp xếp giảm dần theo ngày đặt (mới nhất lên đầu) để tiện hiển thị timeline.
     */
    public List<DatPhong> findByKhachHang(
            EntityManager em,
            String maKH) {

        String jpql = """
                SELECT d
                FROM DatPhong d
                WHERE d.khachHang.maKH = :maKH
                ORDER BY d.ngayDat DESC
                """;

        return em.createQuery(jpql, DatPhong.class)
                .setParameter("maKH", maKH)
                .getResultList();
    }

    /**
     * Lấy tất cả đơn đặt phòng đồng thời tải kèm thông tin Khách hàng (JOIN FETCH).
     * Dùng cho trang danh sách mặc định (/dat-phong?action=list).
     * JOIN FETCH triệt tiêu vấn đề N+1 query khi hiển thị tên khách ngoài bảng JSP.
     */
    public List<DatPhong> findAllWithKhachHang(EntityManager em) {
        String jpql = """
                SELECT d
                FROM DatPhong d
                JOIN FETCH d.khachHang
                JOIN FETCH d.nhanVienXuLy
                LEFT JOIN FETCH d.nhanVienCheckIn
                LEFT JOIN FETCH d.nhanVienCheckOut
                ORDER BY d.ngayDat DESC
                """;
        return em.createQuery(jpql, DatPhong.class).getResultList();
    }

    /**
     * Lọc danh sách đặt phòng theo một trạng thái cụ thể
     * (ví dụ: chỉ lấy các đơn CHO_XAC_NHAN để lễ tân duyệt).
     */
    public List<DatPhong> findByTrangThai(
            EntityManager em,
            TrangThaiDatPhong trangThai) {

        String jpql = """
                SELECT d
                FROM DatPhong d
                WHERE d.trangThai = :trangThai
                ORDER BY d.ngayDat DESC
                """;

        return em.createQuery(jpql, DatPhong.class)
                .setParameter("trangThai", trangThai)
                .getResultList();
    }

    /**
     * [NGHIỆP VỤ 2 CỦA TV2]: TRA CỨU ĐẶT PHÒNG ĐA TIÊU CHÍ (DYNAMIC JPQL)
     * Kỹ thuật: Ghép chuỗi truy vấn động dựa vào các tham số khác null.
     *
     * @param maDatPhong Tìm gần đúng theo mã đơn (không phân biệt hoa thường)
     * @param khachHang  Tìm gần đúng theo họ tên khách hoặc số điện thoại
     * @param trangThai  Lọc chính xác theo trạng thái đơn
     * @param tuNgay     Tìm các đơn có ngày nhận phòng >= tuNgay
     * @param denNgay    Tìm các đơn có ngày nhận phòng <= denNgay
     */
    public List<DatPhong> traCuu(
            EntityManager em,
            String maDatPhong,
            String khachHang,
            TrangThaiDatPhong trangThai,
            LocalDate tuNgay,
            LocalDate denNgay) {

        // Mẹo "WHERE 1 = 1": Cho phép mọi điều kiện lọc phía sau đều dùng cú pháp đồng nhất "AND ..."
        StringBuilder jpql = new StringBuilder("""
                SELECT d
                FROM DatPhong d
                JOIN FETCH d.khachHang kh
                WHERE 1 = 1
                """);

        // 1. Lọc theo mã đặt phòng (dùng LOWER để tìm kiếm case-insensitive)
        if (maDatPhong != null) {
            jpql.append(" AND LOWER(d.maDatPhong) LIKE :maDatPhong");
        }

        // 2. Lọc theo thông tin khách: quét đồng thời cả tên khách lẫn số điện thoại
        if (khachHang != null) {
            jpql.append(" AND (LOWER(kh.hoTen) LIKE :khachHang OR kh.soDienThoai LIKE :khachHang)");
        }

        // 3. Lọc theo trạng thái đơn
        if (trangThai != null) {
            jpql.append(" AND d.trangThai = :trangThai");
        }

        // 4. Lọc theo khoảng ngày nhận:
        // Do ngày nhận nằm ở bảng con CT_DAT_PHONG chứ không nằm ở DAT_PHONG,
        // sử dụng mệnh đề EXISTS để lọc mà không làm nhân đôi dòng kết quả (tránh trùng lặp đơn).
        if (tuNgay != null || denNgay != null) {
            jpql.append(" AND EXISTS (SELECT c.maCTDatPhong FROM CtDatPhong c WHERE c.datPhong = d");
            if (tuNgay != null) {
                jpql.append(" AND c.ngayNhan >= :tuNgay");
            }
            if (denNgay != null) {
                jpql.append(" AND c.ngayNhan <= :denNgay");
            }
            jpql.append(")");
        }

        jpql.append(" ORDER BY d.ngayDat DESC");

        TypedQuery<DatPhong> query = em.createQuery(jpql.toString(), DatPhong.class);

        // 5. Gán giá trị thực tế cho các tham số đã ghép (áp dụng ký tự đại diện % cho phép tìm gần đúng)
        if (maDatPhong != null) query.setParameter("maDatPhong", "%" + maDatPhong.toLowerCase() + "%");
        if (khachHang != null) query.setParameter("khachHang", "%" + khachHang.toLowerCase() + "%");
        if (trangThai != null) query.setParameter("trangThai", trangThai);
        if (tuNgay != null) query.setParameter("tuNgay", tuNgay);
        if (denNgay != null) query.setParameter("denNgay", denNgay);

        return query.getResultList();
    }

    /**
     * [NGHIỆP VỤ 2 CỦA TV2]: XEM CHI TIẾT ĐẶT PHÒNG
     * Kỹ thuật "Deep Fetch Join": Tải một mạch cây quan hệ 4 cấp để hiển thị trọn vẹn ở trang chi tiết
     * d (DatPhong) -> khachHang
     *              -> chiTietDatPhong -> phong -> loaiPhong
     */
    public DatPhong findByIdWithChiTiet(
            EntityManager em,
            String maDatPhong) {

        String jpql = """
                SELECT DISTINCT d
                FROM DatPhong d
                JOIN FETCH d.khachHang
                LEFT JOIN FETCH d.chiTietDatPhong c
                LEFT JOIN FETCH c.phong p
                LEFT JOIN FETCH p.loaiPhong
                WHERE d.maDatPhong = :maDatPhong
                """;

        List<DatPhong> result = em.createQuery(jpql, DatPhong.class)
                .setParameter("maDatPhong", maDatPhong)
                .getResultList();

        return result.isEmpty() ? null : result.get(0);
    }
}