package com.hotel.controller;

import com.hotel.entity.KhachHang;
import com.hotel.enums.GioiTinh;
import com.hotel.service.KhachHangService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/khach-hang")
public class KhachHangServlet extends HttpServlet {
    private KhachHangService service;
    @Override public void init(){ service=new KhachHangService(); }
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{
        req.setCharacterEncoding("UTF-8"); String action=val(req.getParameter("action"),"list");
        try { switch(action){case "new"->form(req,resp,null); case "edit"->form(req,resp,service.findById(req.getParameter("id"))); case "delete"->delete(req,resp); case "detail"->detail(req,resp); default->list(req,resp);} }
        catch(IllegalArgumentException|IllegalStateException e){ req.setAttribute("error",e.getMessage()); list(req,resp); }
    }
    @Override protected void doPost(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{
        req.setCharacterEncoding("UTF-8");
        try { KhachHang x=new KhachHang(); x.setMaKH(trim(req.getParameter("maKH"))); x.setHoTen(required(req,"hoTen")); x.setNgaySinh(date(req.getParameter("ngaySinh"))); x.setGioiTinh(enumVal(GioiTinh.class,req.getParameter("gioiTinh"))); x.setSoDienThoai(req.getParameter("soDienThoai")); x.setEmail(req.getParameter("email")); x.setMatKhauHash(req.getParameter("matKhauHash")); x.setDiaChi(req.getParameter("diaChi"));
            if(blank(x.getMaKH())) service.them(x); else service.capNhat(x); resp.sendRedirect(req.getContextPath()+"/khach-hang?action=list&success=saved"); }
        catch(IllegalArgumentException|IllegalStateException e){req.setAttribute("error",e.getMessage()); list(req,resp);}
    }
    private void list(HttpServletRequest r,HttpServletResponse p)throws ServletException,IOException{r.setAttribute("items",service.findAll());r.getRequestDispatcher("/khach-hang/danh-sach.jsp").forward(r,p);}
    private void form(HttpServletRequest r,HttpServletResponse p,KhachHang x)throws ServletException,IOException{r.setAttribute("item",x);r.setAttribute("gioiTinhs",GioiTinh.values());r.getRequestDispatcher("/khach-hang/form.jsp").forward(r,p);}
    private void detail(HttpServletRequest r,HttpServletResponse p)throws ServletException,IOException{KhachHang x=service.findById(required(r,"id"));if(x==null)throw new IllegalArgumentException("Không tìm thấy khách hàng.");r.setAttribute("item",x);r.getRequestDispatcher("/khach-hang/chi-tiet.jsp").forward(r,p);}
    private void delete(HttpServletRequest r,HttpServletResponse p)throws IOException{service.xoa(required(r,"id"));p.sendRedirect(r.getContextPath()+"/khach-hang?action=list&success=deleted");}
    private static String required(HttpServletRequest r,String n){String v=r.getParameter(n);if(blank(v))throw new IllegalArgumentException("Thiếu tham số: "+n);return v.trim();}
    private static String trim(String v){return v==null?null:v.trim();} private static boolean blank(String v){return v==null||v.isBlank();} private static String val(String v,String f){return blank(v)?f:v;}
    private static LocalDate date(String v){return blank(v)?null:LocalDate.parse(v);} private static <E extends Enum<E>> E enumVal(Class<E> c,String v){return blank(v)?null:Enum.valueOf(c,v);}
}
