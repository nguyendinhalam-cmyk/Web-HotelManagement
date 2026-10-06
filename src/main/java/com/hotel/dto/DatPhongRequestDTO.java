package com.hotel.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO đại diện cho dữ liệu gửi lên từ Form tạo đặt phòng.
 * Nguyên tắc thiết kế: Tuyệt đối KHÔNG chứa Entity JPA (như KhachHang hay Phong)
 * để tránh lỗi vòng lặp phụ thuộc, lỗi Lazy Loading, hoặc vô tình bị gán dữ liệu rác vào Database.
 */
public class DatPhongRequestDTO {

    // Mã khách hàng được chọn từ dropdown trên giao diện
    private String maKH;

    // Ghi chú của khách (tối đa 500 ký tự theo ràng buộc SQL)
    private String ghiChu;

    /**
     * [NGHIỆP VỤ TV2 - KIỂM SOÁT SỨC CHỨA]:
     * Thuộc tính này chỉ tồn tại trong bộ nhớ RAM lúc xử lý Request,
     * dùng để Service tính toán: soLuongKhach <= tổng sức chứa các phòng.
     * Schema CSDL bảng DatPhong KHÔNG có cột này, nên không mapping vào Entity DatPhong.
     */
    private Integer soLuongKhach;

    // Danh sách các phòng được chọn kèm khoảng ngày lưu trú tương ứng
    // Khởi tạo sẵn bằng new ArrayList<>() để tránh NullPointerException khi gọi getChiTiet()
    private final List<ChiTiet> chiTiet = new ArrayList<>();

    // Các hàm Getter và Setter tiêu chuẩn của Java Bean
    public String getMaKH() { return maKH; }
    public void setMaKH(String maKH) { this.maKH = maKH; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public Integer getSoLuongKhach() { return soLuongKhach; }
    public void setSoLuongKhach(Integer soLuongKhach) { this.soLuongKhach = soLuongKhach; }

    // Chỉ cung cấp hàm Getter để đọc danh sách phòng, không cung cấp Setter
    public List<ChiTiet> getChiTiet() { return chiTiet; }

    /**
     * Helper method: Hỗ trợ Servlet thêm từng phòng vào danh sách một cách tiện lợi
     * mà không cần phải tự khởi tạo 'new ChiTiet(...)' bên ngoài Controller.
     */
    public void addChiTiet(String maPhong, LocalDate ngayNhan, LocalDate ngayTra) {
        chiTiet.add(new ChiTiet(maPhong, ngayNhan, ngayTra));
    }

    /**
     * Static Inner Class: Đại diện cho một dòng chi tiết phòng được chọn.
     * Thiết kế theo dạng "Immutable" (bất biến):
     * - Các trường đều là 'private final'
     * - Chỉ nhận giá trị 1 lần duy nhất qua Constructor, không có Setter
     * Giúp đảm bảo dữ liệu phòng và ngày nhận/trả không bao giờ bị thay đổi ngoài ý muốn trong suốt quá trình xử lý.
     */
    public static class ChiTiet {
        private final String maPhong;       // Mã định danh của phòng (P001, P002...)
        private final LocalDate ngayNhan;   // Ngày nhận phòng này
        private final LocalDate ngayTra;    // Ngày trả phòng này

        public ChiTiet(String maPhong, LocalDate ngayNhan, LocalDate ngayTra) {
            this.maPhong = maPhong;
            this.ngayNhan = ngayNhan;
            this.ngayTra = ngayTra;
        }

        public String getMaPhong() { return maPhong; }
        public LocalDate getNgayNhan() { return ngayNhan; }
        public LocalDate getNgayTra() { return ngayTra; }
    }
}