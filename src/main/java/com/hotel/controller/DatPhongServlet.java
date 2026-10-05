package com.hotel.controller;

import com.hotel.dto.DatPhongListDTO;
import com.hotel.dto.DatPhongMapper;
import com.hotel.dto.DatPhongRequestDTO;
import com.hotel.dto.PhongOptionDTO;
import com.hotel.entity.DatPhong;
import com.hotel.entity.KhachHang;
import com.hotel.entity.Phong;
import com.hotel.enums.TrangThaiDatPhong;
import com.hotel.service.DatPhongService;
import com.hotel.service.CheckoutService;
import com.hotel.service.KhachHangService;
import com.hotel.service.PhongService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = "/dat-phong")
public class DatPhongServlet extends HttpServlet {
    private DatPhongService datPhongService;
    private KhachHangService khachHangService;
    private PhongService phongService;
    private CheckoutService checkoutService;

    @Override
    public void init() {
        datPhongService = new DatPhongService();
        khachHangService = new KhachHangService();
        phongService = new PhongService();
        checkoutService = new CheckoutService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = value(request.getParameter("action"), "list");

        try {
            switch (action) {
                case "new" -> showCreateForm(request, response);
                case "available" -> showCreateForm(request, response);
                case "confirm" -> confirm(request, response);
                case "cancel" -> cancel(request, response);
                case "checkin" -> checkIn(request, response);
                case "checkout" -> checkOut(request, response);
                default -> list(request, response);
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            request.setAttribute("error", e.getMessage());
            list(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        try {
            DatPhongRequestDTO dto = parseRequest(request);
            KhachHang khachHang = new KhachHang();
            khachHang.setMaKH(dto.getMaKH());

            List<Phong> rooms = new ArrayList<>();
            for (DatPhongRequestDTO.ChiTiet item : dto.getChiTiet()) {
                Phong room = phongService.findByIdWithLoaiPhong(item.getMaPhong());
                if (room == null) {
                    throw new IllegalArgumentException("Không tìm thấy phòng: " + item.getMaPhong());
                }
                rooms.add(room);
            }

            DatPhong datPhong = DatPhongMapper.toEntity(dto, khachHang, rooms);
            datPhongService.datPhong(datPhong, datPhong.getChiTietDatPhong());

            response.sendRedirect(request.getContextPath()
                    + "/dat-phong?action=list&success=created&ma=" + datPhong.getMaDatPhong());
        } catch (IllegalArgumentException | IllegalStateException e) {
            request.setAttribute("error", e.getMessage());
            showCreateForm(request, response);
        }
    }

    private void list(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<DatPhongListDTO> items = datPhongService.findAllWithKhachHang().stream()
                .map(this::toListDTO)
                .toList();
        request.setAttribute("items", items);
        request.setAttribute("success", request.getParameter("success"));
        request.getRequestDispatcher("/dat-phong/danh-sach.jsp").forward(request, response);
    }

    private void showCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("khachHangs", khachHangService.findAll());

        String ngayNhanParam = request.getParameter("ngayNhan");
        String ngayTraParam = request.getParameter("ngayTra");
        if (notBlank(ngayNhanParam) && notBlank(ngayTraParam)) {
            try {
                LocalDate ngayNhan = LocalDate.parse(ngayNhanParam);
                LocalDate ngayTra = LocalDate.parse(ngayTraParam);
                request.setAttribute("phongOptions", phongService.findPhongCoTheDat(ngayNhan, ngayTra)
                        .stream()
                        .map(p -> new PhongOptionDTO(
                                p.getMaPhong(), p.getSoPhong(),
                                p.getLoaiPhong().getMaLoaiPhong(),
                                p.getLoaiPhong().getTenLoaiPhong(),
                                p.getLoaiPhong().getGiaCoBan()))
                        .toList());
                request.setAttribute("ngayNhan", ngayNhanParam);
                request.setAttribute("ngayTra", ngayTraParam);
            } catch (IllegalArgumentException e) {
                request.setAttribute("error", "Ngày nhận/trả không hợp lệ.");
            }
        }
        request.getRequestDispatcher("/dat-phong/dat-phong.jsp").forward(request, response);
    }

    private void confirm(HttpServletRequest request, HttpServletResponse response) throws IOException {
        datPhongService.xacNhanDatPhong(required(request, "ma"));
        response.sendRedirect(request.getContextPath() + "/dat-phong?action=list&success=confirmed");
    }

    private void cancel(HttpServletRequest request, HttpServletResponse response) throws IOException {
        datPhongService.huyDatPhong(required(request, "ma"));
        response.sendRedirect(request.getContextPath() + "/dat-phong?action=list&success=cancelled");
    }

    private void checkIn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        datPhongService.checkIn(required(request, "ma"));
        response.sendRedirect(request.getContextPath() + "/dat-phong?action=list&success=checkin");
    }

    private void checkOut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // CheckoutService thực hiện toàn bộ nghiệp vụ trong một transaction:
        // tính tiền phòng + dịch vụ, tạo hóa đơn và trả phòng.
        var hoaDon = checkoutService.checkout(required(request, "ma"));
        response.sendRedirect(request.getContextPath()
                + "/hoa-don?action=detail&id=" + hoaDon.getMaHoaDon() + "&success=checkout");
    }

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

        DatPhongRequestDTO dto = new DatPhongRequestDTO();
        dto.setMaKH(maKH);
        dto.setGhiChu(request.getParameter("ghiChu"));
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

    private String required(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (!notBlank(value)) throw new IllegalArgumentException("Thiếu tham số: " + name);
        return value.trim();
    }

    private String value(String value, String fallback) {
        return notBlank(value) ? value : fallback;
    }

    private boolean notBlank(String value) {
        return value != null && !value.isBlank();
    }
}
