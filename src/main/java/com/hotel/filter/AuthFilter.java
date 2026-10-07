package com.hotel.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebFilter("/*")
public class AuthFilter implements Filter {
    @Override public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String uri = req.getRequestURI().substring(req.getContextPath().length());
        boolean publicPath = uri.equals("/login") || uri.equals("/logout") || uri.startsWith("/css/") || uri.startsWith("/js/") || uri.startsWith("/images/") || uri.equals("/favicon.ico");
        if (publicPath || req.getSession(false) != null && req.getSession(false).getAttribute("maNV") != null) {
            chain.doFilter(request, response); return;
        }
        resp.sendRedirect(req.getContextPath() + "/login");
    }
}
