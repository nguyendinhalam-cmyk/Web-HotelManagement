package com.hotel.controller;

// 1. Nhóm DTO (Data Transfer Object): Các đối tượng dùng để đóng gói và vận chuyển dữ liệu giữa View (JSP) và Controller
import com.hotel.dto.DatPhongListDTO;
import com.hotel.dto.DatPhongMapper;
import com.hotel.dto.DatPhongRequestDTO;
import com.hotel.dto.PhongOptionDTO;

// 2. Nhóm Entity: Các lớp thực thể JPA ánh xạ trực tiếp xuống các bảng cơ sở dữ liệu
import com.hotel.entity.DatPhong;
import com.hotel.entity.KhachHang;
import com.hotel.entity.Phong;

// 3. Nhóm Enum: Định nghĩa tập hợp các giá trị trạng thái cố định
import com.hotel.enums.TrangThaiDatPhong;
import com.hotel.enums.TrangThaiPhong;

// 4. Nhóm Service: Tầng nghiệp vụ xử lý logic và quản lý transaction
import com.hotel.service.DatPhongService;
import com.hotel.service.CheckoutService;
import com.hotel.service.KhachHangService;
import com.hotel.service.LoaiPhongService;
import com.hotel.service.PhongService;

// 5. Thư viện Jakarta Servlet API
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

// Đăng ký đường dẫn URL cho Servlet: Mọi HTTP request gửi tới "/dat-phong" sẽ do class này tiếp nhận
@WebServlet(urlPatterns = "/dat-phong")
public class DatPhongServlet extends HttpServlet {
    // Khai báo các service phụ trách từng mảng nghiệp vụ
    private DatPhongService datPhongService;
    private KhachHangService khachHangService;
    private PhongService phongService;
    private CheckoutService checkoutService;
    private LoaiPhongService loaiPhongService; // Bổ sung mới: Nạp dữ liệu loại phòng cho combobox lọc

    // init() chạy 1 lần duy nhất khi Servlet được nạp vào bộ nhớ web container (Tomcat)
    @Override
    public void init() {
        datPhongService = new DatPhongService();
        khachHangService = new KhachHangService();
        phongService = new PhongService();
        checkoutService = new CheckoutService();
        loaiPhongService = new LoaiPhongService();
    }

    // =========================================================================
    // XỬ LÝ GET REQUEST: Xem danh sách, tra cứu, mở form lọc phòng, xem chi tiết
    // =========================================================================
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        // Đọc tham số ?action=... trên URL, nếu để trống thì mặc định là "list"
        String action = value(request.getParameter("action"), "list");

        try {
            switch (action) {
                case "new" -> showCreateForm(request, response);         // Mở form đặt phòng mới
                case "available" -> showCreateForm(request, response);   // Tìm kiếm phòng trống theo bộ lọc
                case "detail" -> detail(request, response);              // Xem chi tiết một đơn đặt phòng
                case "confirm" -> confirm(request, response);             // Xác nhận đơn đặt phòng
                case "cancel" -> cancel(request, response);               // Hủy đơn đặt phòng
                case "checkin" -> checkIn(request, response);             // Chuyển sang nhận phòng (TV3)
                case "checkout" -> checkOut(request, response);           // Chuyển sang trả phòng & tính tiền (TV3/TV5)
                default -> list(request, response);                       // Tra cứu / xem danh sách đặt phòng
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Khi có lỗi nghiệp vụ (nhập thiếu tham số, ID không tồn tại...), gán error để JSP hiển thị banner đỏ
            request.setAttribute("error", e.getMessage());
            list(request, response);
        }
    }

    // =========================================================================
    // XỬ LÝ POST REQUEST: Tiếp nhận form submit để tạo đơn đặt phòng mới
    // =========================================================================
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        try {
            // 1. Thu thập dữ liệu từ request body và validate kiểu dữ liệu
            DatPhongRequestDTO dto = parseRequest(request);

            // 2. Tạo đối tượng khách hàng vỏ với mã lấy từ DTO
            KhachHang khachHang = new KhachHang();
            khachHang.setMaKH(dto.getMaKH());

            // 3. Kiểm tra xem các phòng khách chọn có tồn tại trong hệ thống không
            List<Phong> rooms = new ArrayList<>();
            for (DatPhongRequestDTO.ChiTiet item : dto.getChiTiet()) {
                Phong room = phongService.findByIdWithLoaiPhong(item.getMaPhong());
                if (room == null) {
                    throw new IllegalArgumentException("Không tìm thấy phòng: " + item.getMaPhong());
                }
                rooms.add(room);
            }

            // 4. Ánh xạ DTO sang thực thể DatPhong cùng danh sách ChiTietDatPhong
            DatPhong datPhong = DatPhongMapper.toEntity(dto, khachHang, rooms);

            // 5. Gọi Service thực thi lưu DB; truyền thêm soLuongKhach để kiểm tra tổng sức chứa các phòng
            datPhongService.datPhong(datPhong, datPhong.getChiTietDatPhong(), dto.getSoLuongKhach());

            // 6. Theo chuẩn PRG (Post/Redirect/Get): Chuyển hướng về trang danh sách kèm cờ success và mã mới tạo
            response.sendRedirect(request.getContextPath()
                    + "/dat-phong?action=list&success=created&ma=" + datPhong.getMaDatPhong());
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Nếu có lỗi (trùng lịch, vượt quá sức chứa...), chuyển tiếp lại form cùng thông báo lỗi
            request.setAttribute("error", e.getMessage());
            showCreateForm(request, response);
        }
    }

    // =========================================================================
    // CÁC HÀM XỬ LÝ NGHIỆP VỤ CON (PRIVATE)
    // =========================================================================

    /**
     * Nghiệp vụ 2: Tra cứu danh sách đơn đặt phòng đa tiêu chí
     */
    private void list(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Lấy các tham số tra cứu từ query parameters
        String maDatPhong = trimToNull(request.getParameter("maDatPhong"));
        String khachHang = trimToNull(request.getParameter("khachHang"));
        String trangThaiParam = trimToNull(request.getParameter("trangThai"));
        String tuNgayParam = trimToNull(request.getParameter("tuNgay"));
        String denNgayParam = trimToNull(request.getParameter("denNgay"));

        List<DatPhong> datPhongs;
        // Nếu không có bất kỳ bộ lọc nào: Lấy toàn bộ đơn đặt phòng
        if (maDatPhong == null && khachHang == null && trangThaiParam == null
                && tuNgayParam == null && denNgayParam == null) {
            datPhongs = datPhongService.findAllWithKhachHang();
        } else {
            // Có tiêu chí tìm kiếm: Parse kiểu dữ liệu và gọi hàm tra cứu chuyên biệt ở Service
            try {
                TrangThaiDatPhong trangThai = trangThaiParam == null
                        ? null : TrangThaiDatPhong.valueOf(trangThaiParam);
                LocalDate tuNgay = tuNgayParam == null ? null : LocalDate.parse(tuNgayParam);
                LocalDate denNgay = denNgayParam == null ? null : LocalDate.parse(denNgayParam);
                datPhongs = datPhongService.traCuu(maDatPhong, khachHang, trangThai, tuNgay, denNgay);
            } catch (DateTimeParseException e) {
                request.setAttribute("error", "Ngày tra cứu không hợp lệ.");
                datPhongs = List.of();
            } catch (IllegalArgumentException e) {
                request.setAttribute("error", "Trạng thái tra cứu không hợp lệ.");
                datPhongs = List.of();
            }
        }

        // Chuyển sang DTO nhẹ để gửi ra giao diện JSP
        List<DatPhongListDTO> items = datPhongs.stream()
                .map(this::toListDTO)
                .toList();
        request.setAttribute("items", items);
        request.setAttribute("trangThais", TrangThaiDatPhong.values()); // Gửi enum để sinh danh sách lọc trạng thái
        request.setAttribute("success", request.getParameter("success"));
        request.getRequestDispatcher("/dat-phong/danh-sach.jsp").forward(request, response);
    }

    /**
     * Nghiệp vụ 1: Hiển thị form tạo đơn và tìm kiếm phòng trống theo bộ lọc
     */
    private void showCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Đổ dữ liệu tĩnh vào các dropdown trên giao diện
        request.setAttribute("khachHangs", khachHangService.findAll());
        request.setAttribute("loaiPhongs", loaiPhongService.findAll());
        request.setAttribute("trangThaiPhongs", List.of(TrangThaiPhong.TRONG, TrangThaiPhong.DA_DAT));

        String ngayNhanParam = request.getParameter("ngayNhan");
        String ngayTraParam = request.getParameter("ngayTra");

        // Chỉ quét tìm phòng khả dụng khi người dùng đã chỉ định cả ngày nhận và ngày trả
        if (notBlank(ngayNhanParam) && notBlank(ngayTraParam)) {
            // Giữ lại giá trị ngày trên ô nhập liệu của form sau khi tải lại trang
            request.setAttribute("ngayNhan", ngayNhanParam);
            request.setAttribute("ngayTra", ngayTraParam);
            try {
                LocalDate ngayNhan = LocalDate.parse(ngayNhanParam);
                LocalDate ngayTra = LocalDate.parse(ngayTraParam);

                // Lấy các tham số lọc bổ sung (loại phòng, khoảng giá, tình trạng hiện tại của phòng)
                String maLoaiPhong = trimToNull(request.getParameter("maLoaiPhong"));
                BigDecimal giaTu = parseGia(request.getParameter("giaTu"));
                BigDecimal giaDen = parseGia(request.getParameter("giaDen"));
                String trangThaiParam = trimToNull(request.getParameter("trangThaiPhong"));
                TrangThaiPhong trangThaiPhong;
                try {
                    trangThaiPhong = trangThaiParam == null ? null : TrangThaiPhong.valueOf(trangThaiParam);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Tình trạng phòng không hợp lệ.");
                }

                // Tìm phòng không bị trùng lịch VÀ thỏa mãn các tiêu chí lọc
                request.setAttribute("phongOptions", phongService.findPhongCoTheDat(
                                ngayNhan, ngayTra, maLoaiPhong, giaTu, giaDen, trangThaiPhong)
                        .stream()
                        .map(p -> {
                            PhongOptionDTO option = new PhongOptionDTO(
                                    p.getMaPhong(), p.getSoPhong(),
                                    p.getLoaiPhong().getMaLoaiPhong(),
                                    p.getLoaiPhong().getTenLoaiPhong(),
                                    p.getLoaiPhong().getGiaCoBan());
                            option.setSoNguoiToiDa(p.getLoaiPhong().getSoNguoiToiDa()); // Đính kèm sức chứa
                            return option;
                        })
                        .toList());
            } catch (DateTimeParseException e) {
                request.setAttribute("error", "Ngày nhận/trả không hợp lệ.");
            } catch (IllegalArgumentException e) {
                request.setAttribute("error", e.getMessage());
            }
        }
        request.getRequestDispatcher("/dat-phong/dat-phong.jsp").forward(request, response);
    }

    /**
     * Nghiệp vụ 2: Hiển thị chi tiết một đơn đặt phòng kèm danh sách phòng được chọn
     */
    private void detail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String ma = required(request, "ma");
        DatPhong datPhong = datPhongService.findByIdWithChiTiet(ma);
        if (datPhong == null) {
            throw new IllegalArgumentException("Không tìm thấy đặt phòng: " + ma);
        }
        request.setAttribute("item", datPhong);
        request.getRequestDispatcher("/dat-phong/chi-tiet.jsp").forward(request, response);
    }

    // Chuyển trạng thái đơn sang 'DA_XAC_NHAN'
    private void confirm(HttpServletRequest request, HttpServletResponse response) throws IOException {
        datPhongService.xacNhanDatPhong(required(request, "ma"));
        response.sendRedirect(request.getContextPath() + "/dat-phong?action=list&success=confirmed");
    }

    // Hủy đơn đặt phòng
    private void cancel(HttpServletRequest request, HttpServletResponse response) throws IOException {
        datPhongService.huyDatPhong(required(request, "ma"));
        response.sendRedirect(request.getContextPath() + "/dat-phong?action=list&success=cancelled");
    }

    // Nhận phòng (TV3 phụ trách)
    private void checkIn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        datPhongService.checkIn(required(request, "ma"));
        response.sendRedirect(request.getContextPath() + "/dat-phong?action=list&success=checkin");
    }

    // Trả phòng và lập hóa đơn (TV3 và TV5 phụ trách)
    private void checkOut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        var hoaDon = checkoutService.checkout(required(request, "ma"));
        response.sendRedirect(request.getContextPath()
                + "/hoa-don?action=detail&id=" + hoaDon.getMaHoaDon() + "&success=checkout");
    }

    /**
     * Đọc các trường gửi lên từ form HTML, kiểm tra dữ liệu bắt buộc và đóng gói vào DTO
     */
    private DatPhongRequestDTO parseRequest(HttpServletRequest request) {
        String maKH = required(request, "maKH");
        String[] maPhongs = request.getParameterValues("maPhong");
        String ngayNhanParam = required(request, "ngayNhan");
        String ngayTraParam = required(request, "ngayTra");

        if (maPhongs == null || maPhongs.length == 0) {
            throw new IllegalArgumentException("Phải chọn ít nhất một phòng.");
        }

        final LocalDate ngayNhan;
        final LocalDate ngayTra;
        try {
            ngayNhan = LocalDate.parse(ngayNhanParam);
            ngayTra = LocalDate.parse(ngayTraParam);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Ngày nhận/trả không hợp lệ.");
        }

        // Bắt buộc nhập số lượng khách và kiểm tra định dạng số nguyên
        String soLuongKhachParam = required(request, "soLuongKhach");
        final Integer soLuongKhach;
        try {
            soLuongKhach = Integer.valueOf(soLuongKhachParam);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Số lượng khách phải là số nguyên.");
        }

        DatPhongRequestDTO dto = new DatPhongRequestDTO();
        dto.setMaKH(maKH);
        dto.setGhiChu(request.getParameter("ghiChu"));
        dto.setSoLuongKhach(soLuongKhach);
        for (String maPhong : maPhongs) {
            if (notBlank(maPhong)) {
                dto.addChiTiet(maPhong.trim(), ngayNhan, ngayTra);
            }
        }
        if (dto.getChiTiet().isEmpty()) {
            throw new IllegalArgumentException("Phải chọn ít nhất một phòng.");
        }
        return dto;
    }

    // Chuyển đổi dữ liệu Entity thành DTO để tối ưu dữ liệu truyền ra view JSP
    private DatPhongListDTO toListDTO(DatPhong d) {
        DatPhongListDTO dto = new DatPhongListDTO();
        dto.setMaDatPhong(d.getMaDatPhong());
        dto.setNgayDat(d.getNgayDat());
        dto.setMaKH(d.getKhachHang().getMaKH());
        dto.setTenKhachHang(d.getKhachHang().getHoTen());
        dto.setTrangThai(d.getTrangThai());
        dto.setGhiChu(d.getGhiChu());
        return dto;
    }

    // Đọc parameter bắt buộc; nếu rỗng hoặc null thì ném ngoại lệ
    private String required(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (!notBlank(value)) throw new IllegalArgumentException("Thiếu tham số: " + name);
        return value.trim();
    }

    // Đọc parameter có giá trị mặc định khi null hoặc rỗng
    private String value(String value, String fallback) {
        return notBlank(value) ? value : fallback;
    }

    // Kiểm tra chuỗi có ký tự hợp lệ hay không
    private boolean notBlank(String value) {
        return value != null && !value.isBlank();
    }

    // Chuẩn hóa chuỗi rỗng thành null để tiện kiểm tra điều kiện truy vấn động
    private String trimToNull(String value) {
        return notBlank(value) ? value.trim() : null;
    }

    // Parse chuỗi giá tiền sang BigDecimal an toàn
    private BigDecimal parseGia(String value) {
        if (!notBlank(value)) return null;
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Giá lọc phải là số.");
        }
    }
}