# QLKhachSan - MVC DatPhong

## Đã thêm
- DTO: `DatPhongRequestDTO`, `DatPhongListDTO`, `PhongOptionDTO`
- Mapper: `DatPhongMapper`
- Controller: `DatPhongServlet` mapping `/dat-phong`
- JSP:
  - `/dat-phong/dat-phong.jsp`
  - `/dat-phong/danh-sach.jsp`
- `index.jsp`, `WEB-INF/web.xml`
- DAO/service hỗ trợ fetch `LoaiPhong` và `KhachHang` để tránh LazyInitializationException ở JSP/Servlet.

## Luồng kiểm tra
1. Mở `/dat-phong?action=new`.
2. Chọn ngày nhận/trả.
3. Bấm `Kiểm tra phòng trống`.
4. Chọn khách hàng + một hoặc nhiều phòng.
5. Bấm `Tạo đặt phòng`.
6. Hệ thống gọi `DatPhongService.datPhong()`.
7. Quay về danh sách với trạng thái `CHO_XAC_NHAN`.
8. Có thể thử `Xác nhận -> Check-in -> Check-out`.

## Lưu ý
- Cần chạy MySQL và import CSDL/dữ liệu mẫu trước.
- Cần deploy WAR trên Tomcat 10+ (Jakarta Servlet 6).
- Database connection vẫn lấy từ `DataSourceConfig` hiện có.
