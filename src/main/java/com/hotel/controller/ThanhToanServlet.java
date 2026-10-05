package com.hotel.controller;

import com.hotel.entity.DatPhong;
import com.hotel.entity.ThanhToan;
import com.hotel.enums.PhuongThucThanhToan;
import com.hotel.service.DatPhongService;
import com.hotel.service.ThanhToanService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/thanh-toan")
public class ThanhToanServlet extends HttpServlet{
 private ThanhToanService service;private DatPhongService datPhongService;@Override public void init(){service=new ThanhToanService();datPhongService=new DatPhongService();}
 @Override protected void doGet(HttpServletRequest r,HttpServletResponse p)throws ServletException,IOException{String a=v(r.getParameter("action"),"list");try{switch(a){case"new"->form(r,p);case"success"->success(r,p);case"fail"->fail(r,p);case"refund"->refund(r,p);case"by-dat-phong"->byDatPhong(r,p);case"detail"->detail(r,p);default->list(r,p);}}catch(IllegalArgumentException|IllegalStateException e){r.setAttribute("error",e.getMessage());list(r,p);}}
 @Override protected void doPost(HttpServletRequest r,HttpServletResponse p)throws ServletException,IOException{try{ThanhToan x=new ThanhToan();x.setMaThanhToan(r.getParameter("maThanhToan"));DatPhong dp=datPhongService.findById(req(r,"maDatPhong"));if(dp==null)throw new IllegalArgumentException("Không tìm thấy đặt phòng.");x.setDatPhong(dp);x.setSoTien(new BigDecimal(req(r,"soTien")));x.setPhuongThuc(en(PhuongThucThanhToan.class,req(r,"phuongThuc")));service.taoThanhToan(x);p.sendRedirect(r.getContextPath()+"/thanh-toan?action=list&success=created");}catch(IllegalArgumentException|IllegalStateException e){r.setAttribute("error",e.getMessage());list(r,p);}}
 private void success(HttpServletRequest r,HttpServletResponse p)throws IOException{service.thanhToanThanhCong(req(r,"id"),r.getParameter("maGiaoDich"));p.sendRedirect(r.getContextPath()+"/thanh-toan?action=list&success=paid");}private void fail(HttpServletRequest r,HttpServletResponse p)throws IOException{service.thanhToanThatBai(req(r,"id"),r.getParameter("maGiaoDich"));p.sendRedirect(r.getContextPath()+"/thanh-toan?action=list&success=failed");}private void refund(HttpServletRequest r,HttpServletResponse p)throws IOException{service.hoanTien(req(r,"id"));p.sendRedirect(r.getContextPath()+"/thanh-toan?action=list&success=refunded");}private void byDatPhong(HttpServletRequest r,HttpServletResponse p)throws ServletException,IOException{r.setAttribute("items",service.findByDatPhong(req(r,"maDatPhong")));r.getRequestDispatcher("/thanh-toan/danh-sach.jsp").forward(r,p);}private void list(HttpServletRequest r,HttpServletResponse p)throws ServletException,IOException{r.setAttribute("items",service.findAll());r.getRequestDispatcher("/thanh-toan/danh-sach.jsp").forward(r,p);}private void form(HttpServletRequest r,HttpServletResponse p)throws ServletException,IOException{r.setAttribute("datPhongs",datPhongService.findAllWithKhachHang());r.setAttribute("phuongThucs",PhuongThucThanhToan.values());r.getRequestDispatcher("/thanh-toan/form.jsp").forward(r,p);}private void detail(HttpServletRequest r,HttpServletResponse p)throws ServletException,IOException{ThanhToan x=service.findById(req(r,"id"));if(x==null)throw new IllegalArgumentException("Không tìm thấy thanh toán.");r.setAttribute("item",x);r.getRequestDispatcher("/thanh-toan/chi-tiet.jsp").forward(r,p);}private static String req(HttpServletRequest r,String n){String x=r.getParameter(n);if(x==null||x.isBlank())throw new IllegalArgumentException("Thiếu tham số: "+n);return x.trim();}private static String v(String x,String f){return x==null||x.isBlank()?f:x;}private static<E extends Enum<E>>E en(Class<E> c,String x){return Enum.valueOf(c,x);}
}
