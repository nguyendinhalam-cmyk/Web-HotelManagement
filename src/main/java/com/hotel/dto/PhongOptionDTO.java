package com.hotel.dto;

import java.math.BigDecimal;

/**
 * DTO đại diện cho một lựa chọn phòng (Room Option) hiển thị trên giao diện đặt phòng.
 * Dùng trực tiếp cho Nghiệp vụ 1 (Kiểm tra phòng trống) & Nghiệp vụ 2 (Đặt phòng) của TV2.
 */
public class PhongOptionDTO {
    // Mã phòng (Khóa chính) dùng làm value cho checkbox/radio trên HTML (<input type="checkbox" name="maPhong" value="P001">)
    private final String maPhong;

    // Số phòng hiển thị bên ngoài thực tế để lễ tân nhận biết (ví dụ: "101", "202")
    private final String soPhong;

    // Mã loại phòng (ví dụ: "LP01", "VIP")
    private final String maLoaiPhong;

    // Tên loại phòng hiển thị trên bảng (ví dụ: "Phòng Đơn Tiêu Chuẩn", "Phòng Đôi VIP")
    private final String tenLoaiPhong;

    // Giá cơ bản mỗi đêm của phòng (lấy từ bảng LOAI_PHONG), dùng để hiển thị giá tham khảo và tính tạm tính ngoài giao diện
    private final BigDecimal giaCoBan;

    /**
     * Constructor chính: Khởi tạo các thông tin cơ bản của một phòng hiển thị.
     * Các trường này được gán 'final' để đảm bảo tính bất biến (Immutable), tránh bị ghi đè nhầm sau khi nạp từ DB.
     */
    public PhongOptionDTO(String maPhong, String soPhong, String maLoaiPhong,
                          String tenLoaiPhong, BigDecimal giaCoBan) {
        this.maPhong = maPhong;
        this.soPhong = soPhong;
        this.maLoaiPhong = maLoaiPhong;
        this.tenLoaiPhong = tenLoaiPhong;
        this.giaCoBan = giaCoBan;
    }

    // Các hàm Getter để EL (Expression Language) trên JSP đọc dữ liệu (ví dụ: ${p.soPhong}, ${p.tenLoaiPhong})
    public String getMaPhong() { return maPhong; }
    public String getSoPhong() { return soPhong; }
    public String getMaLoaiPhong() { return maLoaiPhong; }
    public String getTenLoaiPhong() { return tenLoaiPhong; }
    public BigDecimal getGiaCoBan() { return giaCoBan; }

    /**
     * [BỔ SUNG MỚI - NGHIỆP VỤ TV2: KIỂM SOÁT SỨC CHỨA]:
     * Thuộc tính này lấy từ LOAI_PHONG.soNguoiToiDa.
     * Mục đích:
     * 1. Hiển thị ngoài bảng để nhân viên biết phòng chứa được tối đa bao nhiêu người (ví dụ: "Phòng 101 - 2 người").
     * 2. Phục vụ JavaScript phía Frontend kiểm tra nhanh tổng sức chứa các phòng đã chọn có đủ cho số khách đi cùng không.
     */
    private Integer soNguoiToiDa;

    public Integer getSoNguoiToiDa() { return soNguoiToiDa; }

    // Dùng setter riêng để gán giá trị này sau khi khởi tạo đối tượng (như cách viết trong DatPhongServlet)
    public void setSoNguoiToiDa(Integer soNguoiToiDa) { this.soNguoiToiDa = soNguoiToiDa; }
}