package com.hotel.dao;

import com.hotel.entity.CtDatPhong;
import com.hotel.enums.TrangThaiDatPhong;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.util.List;

/**
 * CtDatPhongDAO kế thừa BaseDAO:
 * - Kế thừa CRUD cơ bản: save, update, delete, findById, findAll.
 * - Nhận cặp Generic: <CtDatPhong, String> tương ứng <Entity, Khóa_Chính (maCTDatPhong)>.
 */
public class CtDatPhongDAO
        extends BaseDAO<CtDatPhong, String> {

    /**
     * Hằng số public static dùng chung cho toàn hệ thống:
     * Định nghĩa các trạng thái đặt phòng KHÔNG CÒN HIỆU LỰC GIỮ CHỖ.
     * - DA_HUY: Đơn đã hủy bỏ.
     * - DA_TRA_PHONG: Khách đã thanh toán và trả phòng xong.
     * Được tái sử dụng bên PhongDAO để đồng nhất 1 quy tắc duy nhất khi lọc phòng trống.
     */
    public static final List<TrangThaiDatPhong> TRANG_THAI_KHONG_GIU_PHONG =
            List.of(TrangThaiDatPhong.DA_HUY, TrangThaiDatPhong.DA_TRA_PHONG);

    public CtDatPhongDAO() {
        super(CtDatPhong.class);
    }

    /**
     * Lấy danh sách tất cả các phòng thuộc về một đơn đặt phòng cụ thể.
     * Sắp xếp theo ngày nhận tăng dần.
     */
    public List<CtDatPhong> findByDatPhong(
            EntityManager em,
            String maDatPhong) {

        String jpql = """
                SELECT c
                FROM CtDatPhong c
                WHERE c.datPhong.maDatPhong = :maDatPhong
                ORDER BY c.ngayNhan
                """;

        return em.createQuery(jpql, CtDatPhong.class)
                .setParameter("maDatPhong", maDatPhong)
                .getResultList();
    }

    /**
     * Lấy toàn bộ lịch sử các đợt lưu trú gắn liền với một phòng cụ thể.
     */
    public List<CtDatPhong> findByPhong(
            EntityManager em,
            String maPhong) {

        String jpql = """
                SELECT c
                FROM CtDatPhong c
                WHERE c.phong.maPhong = :maPhong
                ORDER BY c.ngayNhan
                """;

        return em.createQuery(jpql, CtDatPhong.class)
                .setParameter("maPhong", maPhong)
                .getResultList();
    }

    /**
     * [NGHIỆP VỤ 1 & 2 CỦA TV2]: KIỂM TRA TRÙNG LỊCH ĐẶT PHÒNG
     * Đếm xem có bao nhiêu đơn đặt phòng hợp lệ đang chiếm phòng này trong khoảng thời gian [ngayNhan, ngayTra).
     *
     * @return true nếu BỊ TRÙNG (count > 0), false nếu phòng còn trống lịch.
     */
    public boolean existsPhongTrungLich(
            EntityManager em,
            String maPhong,
            LocalDate ngayNhan,
            LocalDate ngayTra) {

        // Chỉ xét các đơn đặt phòng ĐANG CÓ HIỆU LỰC (bỏ qua đơn Đã hủy hoặc Đã trả phòng)
        // Công thức 2 khoảng thời gian [A1, A2) và [B1, B2) giao nhau:
        // Lịch cũ bắt đầu trước khi lịch mới kết thúc VÀ lịch cũ kết thúc sau khi lịch mới bắt đầu
        String jpql = """
            SELECT COUNT(c)
            FROM CtDatPhong c
            WHERE c.phong.maPhong = :maPhong
              AND c.datPhong.trangThai NOT IN :trangThaiKhongGiuPhong
              AND c.ngayNhan < :ngayTra
              AND c.ngayTra > :ngayNhan
            """;

        Long count = em.createQuery(jpql, Long.class)
                .setParameter("maPhong", maPhong)
                .setParameter(
                        "trangThaiKhongGiuPhong",
                        TRANG_THAI_KHONG_GIU_PHONG
                )
                .setParameter("ngayNhan", ngayNhan)
                .setParameter("ngayTra", ngayTra)
                .getSingleResult();

        // Nếu count > 0 nghĩa là đã có ít nhất 1 đơn đặt trước chiếm chỗ
        return count > 0;
    }

    /**
     * [NGHIỆP VỤ HỦY ĐẶT PHÒNG CỦA TV2]: KIỂM TRA CÒN ĐƠN KHÁC GIỮ PHÒNG HAY KHÔNG
     * Dùng khi hủy một đơn đặt phòng: Kiểm tra xem ngoài đơn đang hủy (maDatPhongBoQua),
     * phòng này có còn nằm trong đơn đặt phòng hiệu lực nào khác nữa không.
     *
     * @param maDatPhongBoQua Mã đơn đang được xử lý hủy (bỏ qua không tính đơn này)
     * @return true nếu vẫn còn đơn khác giữ phòng (không được trả phòng về TRONG),
     *         false nếu hoàn toàn không còn đơn nào (được phép trả phòng về TRONG).
     */
    public boolean existsDatPhongKhacConGiuPhong(
            EntityManager em,
            String maPhong,
            String maDatPhongBoQua) {

        //Loại trừ chính đơn đặt phòng đang làm thao tác hủy
        //Chỉ xét các đơn còn hiệu lực giữ phòng (CHO_XAC_NHAN, DA_XAC_NHAN, DANG_O)
        String jpql = """
            SELECT COUNT(c)
            FROM CtDatPhong c
            WHERE c.phong.maPhong = :maPhong
              AND c.datPhong.maDatPhong <> :maDatPhongBoQua
              AND c.datPhong.trangThai NOT IN :trangThaiKhongGiuPhong
            """;

        Long count = em.createQuery(jpql, Long.class)
                .setParameter("maPhong", maPhong)
                .setParameter("maDatPhongBoQua", maDatPhongBoQua)
                .setParameter("trangThaiKhongGiuPhong", TRANG_THAI_KHONG_GIU_PHONG)
                .getSingleResult();

        return count > 0;
    }
}