package com.hotel.controller;

import com.hotel.entity.NhanVien;
import com.hotel.enums.GioiTinh;
import com.hotel.service.NhanVienService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/nhan-vien")
public class NhanVienServlet extends HttpServlet {
    private NhanVienService service; @Override public void init(){service=new NhanVienService();}
    @Override protected void doGet(HttpServletRequest r,HttpServletResponse p)throws ServletException,IOException{r.setCharacterEncoding("UTF-8");String a=v(r.getParameter("action"),"list");try{switch(a){case"new"->form(r,p,null);case"edit"->form(r,p,service.findById(req(r,"id")));case"delete"->del(r,p);case"detail"->detail(r,p);default->list(r,p);}}catch(IllegalArgumentException|IllegalStateException e){r.setAttribute("error",e.getMessage());list(r,p);}}
    @Override protected void doPost(HttpServletRequest r,HttpServletResponse p)throws ServletException,IOException{r.setCharacterEncoding("UTF-8");try{NhanVien x=new NhanVien();x.setMaNV(trim(r.getParameter("maNV")));x.setHoTen(req(r,"hoTen"));x.setNgaySinh(date(r.getParameter("ngaySinh")));x.setGioiTinh(en(GioiTinh.class,r.getParameter("gioiTinh")));x.setSoDienThoai(r.getParameter("soDienThoai"));x.setEmail(r.getParameter("email"));x.setMatKhauHash(r.getParameter("matKhauHash"));x.setDiaChi(r.getParameter("diaChi"));x.setChucVu(r.getParameter("chucVu"));if(blank(x.getMaNV()))service.them(x);else service.capNhat(x);p.sendRedirect(r.getContextPath()+"/nhan-vien?action=list&success=saved");}catch(IllegalArgumentException|IllegalStateException e){r.setAttribute("error",e.getMessage());list(r,p);}}
    private void list(HttpServletRequest r,HttpServletResponse p)throws ServletException,IOException{r.setAttribute("items",service.findAll());r.getRequestDispatcher("/nhan-vien/danh-sach.jsp").forward(r,p);} private void form(HttpServletRequest r,HttpServletResponse p,NhanVien x)throws ServletException,IOException{r.setAttribute("item",x);r.setAttribute("gioiTinhs",GioiTinh.values());r.getRequestDispatcher("/nhan-vien/form.jsp").forward(r,p);} private void detail(HttpServletRequest r,HttpServletResponse p)throws ServletException,IOException{NhanVien x=service.findById(req(r,"id"));if(x==null)throw new IllegalArgumentException("Không tìm thấy nhân viên.");r.setAttribute("item",x);r.getRequestDispatcher("/nhan-vien/chi-tiet.jsp").forward(r,p);} private void del(HttpServletRequest r,HttpServletResponse p)throws IOException{service.xoa(req(r,"id"));p.sendRedirect(r.getContextPath()+"/nhan-vien?action=list&success=deleted");}
    private static String req(HttpServletRequest r,String n){String x=r.getParameter(n);if(blank(x))throw new IllegalArgumentException("Thiếu tham số: "+n);return x.trim();}private static String trim(String x){return x==null?null:x.trim();}private static boolean blank(String x){return x==null||x.isBlank();}private static String v(String x,String f){return blank(x)?f:x;}private static LocalDate date(String x){return blank(x)?null:LocalDate.parse(x);}private static<E extends Enum<E>>E en(Class<E> c,String x){return blank(x)?null:Enum.valueOf(c,x);}
}
