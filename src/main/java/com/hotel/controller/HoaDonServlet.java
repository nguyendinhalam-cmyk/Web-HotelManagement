package com.hotel.controller;

import com.hotel.entity.HoaDon;
import com.hotel.service.HoaDonService;
import com.hotel.service.DatPhongService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/hoa-don")
public class HoaDonServlet extends HttpServlet{
 private HoaDonService service;private DatPhongService datPhongService;@Override public void init(){service=new HoaDonService();datPhongService=new DatPhongService();}
 @Override protected void doGet(HttpServletRequest r,HttpServletResponse p)throws ServletException,IOException{String a=v(r.getParameter("action"),"list");try{switch(a){case"create"->create(r,p);case"issue"->issue(r,p);case"cancel"->cancel(r,p);case"delete"->del(r,p);case"by-dat-phong"->byDatPhong(r,p);case"detail"->detail(r,p);default->list(r,p);}}catch(IllegalArgumentException|IllegalStateException e){r.setAttribute("error",e.getMessage());list(r,p);}}
 private void create(HttpServletRequest r,HttpServletResponse p)throws IOException{HoaDon x=service.taoHoaDon(req(r,"maDatPhong"));p.sendRedirect(r.getContextPath()+"/hoa-don?action=detail&id="+x.getMaHoaDon()+"&success=created");}private void issue(HttpServletRequest r,HttpServletResponse p)throws IOException{service.phatHanhHoaDon(req(r,"id"));p.sendRedirect(r.getContextPath()+"/hoa-don?action=list&success=issued");}private void cancel(HttpServletRequest r,HttpServletResponse p)throws IOException{service.huyHoaDon(req(r,"id"));p.sendRedirect(r.getContextPath()+"/hoa-don?action=list&success=cancelled");}private void del(HttpServletRequest r,HttpServletResponse p)throws IOException{service.xoa(req(r,"id"));p.sendRedirect(r.getContextPath()+"/hoa-don?action=list&success=deleted");}private void list(HttpServletRequest r,HttpServletResponse p)throws ServletException,IOException{r.setAttribute("items",service.findAll());r.getRequestDispatcher("/hoa-don/danh-sach.jsp").forward(r,p);}private void byDatPhong(HttpServletRequest r,HttpServletResponse p)throws ServletException,IOException{r.setAttribute("items",service.findByDatPhong(req(r,"maDatPhong")));r.getRequestDispatcher("/hoa-don/danh-sach.jsp").forward(r,p);}private void detail(HttpServletRequest r,HttpServletResponse p)throws ServletException,IOException{HoaDon x=service.findById(req(r,"id"));if(x==null)throw new IllegalArgumentException("Không tìm thấy hóa đơn.");r.setAttribute("item",x);r.getRequestDispatcher("/hoa-don/chi-tiet.jsp").forward(r,p);}private static String req(HttpServletRequest r,String n){String x=r.getParameter(n);if(x==null||x.isBlank())throw new IllegalArgumentException("Thiếu tham số: "+n);return x.trim();}private static String v(String x,String f){return x==null||x.isBlank()?f:x;}
}
