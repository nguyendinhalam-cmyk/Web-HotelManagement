package com.hotel.controller;

import com.hotel.entity.TaiKhoan;
import com.hotel.service.TaiKhoanService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private TaiKhoanService service;
    @Override public void init() { service = new TaiKhoanService(); }

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getSession(false) != null && req.getSession(false).getAttribute("maNV") != null) {
            resp.sendRedirect(req.getContextPath() + "/"); return;
        }
        req.getRequestDispatcher("/login/login.jsp").forward(req, resp);
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String maNV = req.getParameter("maNV");
        String matKhau = req.getParameter("matKhau");
        try {
            if (maNV == null || maNV.isBlank() || matKhau == null || matKhau.isBlank()) throw new IllegalArgumentException("Vui lòng nhập mã nhân viên và mật khẩu.");
            TaiKhoan tk = service.dangNhap(maNV.trim(), matKhau);
            HttpSession session = req.getSession(true);
            session.setAttribute("maNV", tk.getNhanVien().getMaNV());
            session.setAttribute("hoTen", tk.getNhanVien().getHoTen());
            session.setAttribute("vaiTro", tk.getVaiTro().name());
            session.setMaxInactiveInterval(30 * 60);
            resp.sendRedirect(req.getContextPath() + "/");
        } catch (IllegalArgumentException | IllegalStateException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("maNV", maNV);
            req.getRequestDispatcher("/login/login.jsp").forward(req, resp);
        }
    }
}
