package com.hotel.controller;

import com.hotel.entity.KhachHang;
import com.hotel.enums.GioiTinh;
import com.hotel.service.KhachHangService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/khach-hang")
public class KhachHangServlet extends HttpServlet {

    private KhachHangService service;

    @Override
    public void init() {
        service = new KhachHangService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        String action = val(req.getParameter("action"), "list");

        try {
            switch (action) {
                case "new":
                    form(req, resp, null);
                    break;

                case "edit":
                    form(req, resp, service.findById(required(req, "id")));
                    break;

                case "delete":
                    delete(req, resp);
                    break;

                case "detail":
                    detail(req, resp);
                    break;

                default:
                    list(req, resp);
                    break;
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            req.setAttribute("error", e.getMessage());
            list(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        String action = req.getParameter("action");

        // Xóa khách hàng từ giao diện
        if ("delete".equals(action)) {
            try {
                service.xoa(required(req, "id"));

                resp.sendRedirect(
                        req.getContextPath()
                                + "/khach-hang?action=list&success=deleted"
                );
            } catch (RuntimeException e) {
                req.setAttribute(
                        "error",
                        "Không thể xóa khách hàng. Khách hàng có thể đang liên kết với dữ liệu đặt phòng."
                );
                list(req, resp);
            }
            return;
        }

        // Thêm hoặc cập nhật khách hàng
        KhachHang x = new KhachHang();

        x.setMaKH(trim(req.getParameter("maKH")));
        x.setHoTen(trim(req.getParameter("hoTen")));
        x.setCccd(trim(req.getParameter("cccd")));
        x.setNgaySinh(date(req.getParameter("ngaySinh")));
        x.setGioiTinh(enumVal(
                GioiTinh.class,
                req.getParameter("gioiTinh")
        ));
        x.setSoDienThoai(trim(req.getParameter("soDienThoai")));
        x.setEmail(trim(req.getParameter("email")));
        x.setDiaChi(trim(req.getParameter("diaChi")));

        try {
            if (blank(x.getMaKH())) {
                service.them(x);

                resp.sendRedirect(
                        req.getContextPath()
                                + "/khach-hang?action=list&success=saved"
                );
            } else {
                service.capNhat(x);

                resp.sendRedirect(
                        req.getContextPath()
                                + "/khach-hang?action=list&success=saved"
                );
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            req.setAttribute("error", e.getMessage());
            form(req, resp, x);
        }
    }

    private void list(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String keyword = trim(req.getParameter("keyword"));
        List<KhachHang> items = service.findAll();

        if (!blank(keyword)) {
            String key = keyword.toLowerCase();

            items = items.stream()
                    .filter(kh ->
                            (kh.getMaKH() != null
                                    && kh.getMaKH().toLowerCase().contains(key))
                                    || (kh.getHoTen() != null
                                    && kh.getHoTen().toLowerCase().contains(key))
                                    || (kh.getSoDienThoai() != null
                                    && kh.getSoDienThoai().contains(key))
                    )
                    .collect(Collectors.toList());
        }

        req.setAttribute("items", items);
        req.setAttribute("keyword", keyword);

        req.getRequestDispatcher("/khach-hang/danh-sach.jsp")
                .forward(req, resp);
    }

    private void form(HttpServletRequest req, HttpServletResponse resp,
                      KhachHang x)
            throws ServletException, IOException {

        req.setAttribute("item", x);
        req.setAttribute("gioiTinhs", GioiTinh.values());

        String gioiTinhDaChon = "";

        if (x != null && x.getGioiTinh() != null) {
            gioiTinhDaChon = x.getGioiTinh().name();
        }

        req.setAttribute("gioiTinhDaChon", gioiTinhDaChon);

        req.getRequestDispatcher("/khach-hang/form.jsp")
                .forward(req, resp);
    }

    private void detail(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        KhachHang x = service.findById(required(req, "id"));

        if (x == null) {
            throw new IllegalArgumentException(
                    "Không tìm thấy khách hàng."
            );
        }

        req.setAttribute("item", x);

        req.getRequestDispatcher("/khach-hang/chi-tiet.jsp")
                .forward(req, resp);
    }

    private void delete(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        service.xoa(required(req, "id"));

        resp.sendRedirect(
                req.getContextPath()
                        + "/khach-hang?action=list&success=deleted"
        );
    }

    private static String required(HttpServletRequest req, String name) {
        String value = req.getParameter(name);

        if (blank(value)) {
            throw new IllegalArgumentException(
                    "Vui lòng nhập đầy đủ thông tin: " + name
            );
        }

        return value.trim();
    }

    private static String trim(String value) {
        return value == null ? null : value.trim();
    }

    private static boolean blank(String value) {
        return value == null || value.isBlank();
    }

    private static String val(String value, String fallback) {
        return blank(value) ? fallback : value;
    }

    private static LocalDate date(String value) {
        return blank(value) ? null : LocalDate.parse(value);
    }

    private static <E extends Enum<E>> E enumVal(
            Class<E> type, String value) {

        return blank(value) ? null : Enum.valueOf(type, value);
    }
}