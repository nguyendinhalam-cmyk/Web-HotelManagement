package com.hotel.controller;

import com.hotel.entity.NhanVien;
import com.hotel.enums.GioiTinh;
import com.hotel.enums.TrangThaiTaiKhoan;
import com.hotel.enums.VaiTro;
import com.hotel.service.NhanVienService;
import com.hotel.service.TaiKhoanService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/nhan-vien")
public class NhanVienServlet extends HttpServlet {

    private NhanVienService nhanVienService;
    private TaiKhoanService taiKhoanService;

    @Override
    public void init() {
        this.nhanVienService = new NhanVienService();
        this.taiKhoanService = new TaiKhoanService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = getOrDefault(req.getParameter("action"), "list");

        try {
            switch (action) {
                case "new" -> renderForm(req, resp, null);
                case "edit" -> renderForm(req, resp, nhanVienService.findById(getRequiredParam(req, "id")));
                case "detail" -> renderDetail(req, resp);
                case "toggle" -> handleToggleAccountStatus(req, resp);
                case "role" -> renderChangeRoleForm(req, resp);
                case "password" -> renderChangePasswordForm(req, resp);
                default -> renderList(req, resp);
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            req.setAttribute("error", e.getMessage());
            renderList(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = getOrDefault(req.getParameter("action"), "save");

        try {
            switch (action) {
                case "save" -> handleSaveNhanVien(req, resp);
                case "role" -> handleChangeRole(req, resp);
                case "password" -> handleChangePassword(req, resp);
                default -> throw new IllegalArgumentException("Thao tác không hợp lệ.");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            req.setAttribute("error", e.getMessage());
            renderList(req, resp);
        }
    }

    private void handleSaveNhanVien(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String maNV = trimToNull(req.getParameter("maNV"));

        NhanVien nhanVien = new NhanVien();
        nhanVien.setMaNV(maNV);
        nhanVien.setHoTen(getRequiredParam(req, "hoTen"));
        nhanVien.setNgaySinh(parseDate(req.getParameter("ngaySinh")));
        nhanVien.setGioiTinh(parseEnum(GioiTinh.class, req.getParameter("gioiTinh")));
        nhanVien.setSoDienThoai(getRequiredParam(req, "soDienThoai"));
        nhanVien.setEmail(getRequiredParam(req, "email"));
        nhanVien.setDiaChi(trimToNull(req.getParameter("diaChi")));

        if (isBlank(nhanVien.getMaNV())) {
            String matKhau = getRequiredParam(req, "matKhauBanDau");
            String confirmMatKhau = getRequiredParam(req, "xacNhanMatKhau");

            if (!matKhau.equals(confirmMatKhau)) {
                throw new IllegalArgumentException("Mật khẩu xác nhận không khớp.");
            }

            VaiTro vaiTro = parseEnum(VaiTro.class, getRequiredParam(req, "vaiTro"));
            nhanVienService.taoNhanVienVaTaiKhoan(nhanVien, matKhau, vaiTro);
        } else {
            nhanVienService.capNhatThongTin(nhanVien);
        }

        redirectToSuccess(req, resp, "saved");
    }

    private void handleToggleAccountStatus(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String maNV = getRequiredParam(req, "id");
        var taiKhoan = taiKhoanService.findByMaNV(maNV);

        if (taiKhoan == null) {
            throw new IllegalArgumentException("Nhân viên chưa có tài khoản.");
        }

        TrangThaiTaiKhoan nextStatus = (taiKhoan.getTrangThai() == TrangThaiTaiKhoan.HOAT_DONG)
                ? TrangThaiTaiKhoan.KHOA
                : TrangThaiTaiKhoan.HOAT_DONG;

        taiKhoanService.doiTrangThai(maNV, nextStatus);
        redirectToSuccess(req, resp, "status");
    }

    private void handleChangeRole(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String maNV = getRequiredParam(req, "maNV");
        VaiTro vaiTro = parseEnum(VaiTro.class, getRequiredParam(req, "vaiTro"));

        taiKhoanService.doiVaiTro(maNV, vaiTro);
        redirectToSuccess(req, resp, "saved");
    }

    private void handleChangePassword(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String maNV = getRequiredParam(req, "maNV");
        String matKhauMoi = getRequiredParam(req, "matKhauMoi");

        taiKhoanService.doiMatKhau(maNV, matKhauMoi);
        redirectToSuccess(req, resp, "password");
    }

    private void renderList(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("items", nhanVienService.findAll());
        req.setAttribute("taiKhoanService", taiKhoanService);
        req.getRequestDispatcher("/nhan-vien/danh-sach.jsp").forward(req, resp);
    }

    private void renderForm(HttpServletRequest req, HttpServletResponse resp, NhanVien nhanVien)
            throws ServletException, IOException {
        req.setAttribute("mode", nhanVien == null ? "new" : "edit");
        req.setAttribute("item", nhanVien);
        req.setAttribute("gioiTinhs", GioiTinh.values());
        req.setAttribute("vaiTros", VaiTro.values());
        req.getRequestDispatcher("/nhan-vien/form.jsp").forward(req, resp);
    }

    private void renderDetail(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String id = getRequiredParam(req, "id");
        NhanVien nhanVien = nhanVienService.findById(id);

        if (nhanVien == null) {
            throw new IllegalArgumentException("Không tìm thấy nhân viên.");
        }

        req.setAttribute("item", nhanVien);
        req.setAttribute("taiKhoan", taiKhoanService.findByMaNV(nhanVien.getMaNV()));
        req.getRequestDispatcher("/nhan-vien/chi-tiet.jsp").forward(req, resp);
    }

    private void renderChangeRoleForm(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String id = getRequiredParam(req, "id");
        req.setAttribute("item", nhanVienService.findById(id));
        req.setAttribute("vaiTros", VaiTro.values());
        req.getRequestDispatcher("/nhan-vien/doi-vai-tro.jsp").forward(req, resp);
    }

    private void renderChangePasswordForm(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String id = getRequiredParam(req, "id");
        req.setAttribute("item", nhanVienService.findById(id));
        req.getRequestDispatcher("/nhan-vien/doi-mat-khau.jsp").forward(req, resp);
    }

    private static void redirectToSuccess(HttpServletRequest req, HttpServletResponse resp, String resultKey)
            throws IOException {
        resp.sendRedirect(req.getContextPath() + "/nhan-vien?action=list&success=" + resultKey);
    }

    private static String getRequiredParam(HttpServletRequest req, String paramName) {
        String value = req.getParameter(paramName);
        if (isBlank(value)) {
            throw new IllegalArgumentException("Thiếu tham số bắt buộc: " + paramName);
        }
        return value.trim();
    }

    private static String trimToNull(String str) {
        return isBlank(str) ? null : str.trim();
    }

    private static boolean isBlank(String str) {
        return str == null || str.isBlank();
    }

    private static String getOrDefault(String value, String defaultValue) {
        return isBlank(value) ? defaultValue : value.trim();
    }

    private static LocalDate parseDate(String dateStr) {
        return isBlank(dateStr) ? null : LocalDate.parse(dateStr.trim());
    }

    private static <E extends Enum<E>> E parseEnum(Class<E> enumClass, String value) {
        return isBlank(value) ? null : Enum.valueOf(enumClass, value.trim());
    }
}