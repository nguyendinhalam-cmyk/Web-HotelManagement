package com.hotel.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebFilter("/nhan-vien")
public class AdminFilter implements Filter {
    @Override public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        Object role = req.getSession(false) == null ? null : req.getSession(false).getAttribute("vaiTro");
        if ("ADMIN".equals(role)) { chain.doFilter(request, response); return; }
        resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Chỉ ADMIN được quản lý nhân viên và tài khoản.");
    }
}
