package com.hotel.controller;

import com.hotel.entity.DichVu;
import com.hotel.service.DichVuService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/dich-vu")
public class DichVuServlet extends HttpServlet{
 private DichVuService service;@Override public void init(){service=new DichVuService();}
 @Override protected void doGet(HttpServletRequest r,HttpServletResponse p)throws ServletException,IOException{String a=v(r.getParameter("action"),"list");try{switch(a){case"new"->form(r,p,null);case"edit"->form(r,p,service.findById(req(r,"id")));case"delete"->del(r,p);case"stop"->change(r,p,false);case"start"->change(r,p,true);case"detail"->detail(r,p);default->list(r,p);}}catch(IllegalArgumentException|IllegalStateException e){r.setAttribute("error",e.getMessage());list(r,p);}}
 @Override protected void doPost(HttpServletRequest r,HttpServletResponse p)throws ServletException,IOException{try{DichVu x=new DichVu();x.setMaDichVu(trim(r.getParameter("maDichVu")));x.setTenDichVu(req(r,"tenDichVu"));x.setMoTa(r.getParameter("moTa"));x.setDonGia(new BigDecimal(req(r,"donGia")));x.setLoaiDichVu(r.getParameter("loaiDichVu"));String d=r.getParameter("dangKinhDoanh");x.setDangKinhDoanh(d==null||Boolean.parseBoolean(d));if(blank(x.getMaDichVu()))service.them(x);else service.capNhat(x);p.sendRedirect(r.getContextPath()+"/dich-vu?action=list&success=saved");}catch(IllegalArgumentException|IllegalStateException e){r.setAttribute("error",e.getMessage());list(r,p);}}
 private void change(HttpServletRequest r,HttpServletResponse p,boolean start)throws IOException{String id=req(r,"id");if(start)service.moKinhDoanh(id);else service.ngungKinhDoanh(id);p.sendRedirect(r.getContextPath()+"/dich-vu?action=list&success=status");}private void list(HttpServletRequest r,HttpServletResponse p)throws ServletException,IOException{r.setAttribute("items",service.findAll());r.getRequestDispatcher("/dich-vu/danh-sach.jsp").forward(r,p);}private void form(HttpServletRequest r,HttpServletResponse p,DichVu x)throws ServletException,IOException{r.setAttribute("item",x);r.getRequestDispatcher("/dich-vu/form.jsp").forward(r,p);}private void detail(HttpServletRequest r,HttpServletResponse p)throws ServletException,IOException{DichVu x=service.findById(req(r,"id"));if(x==null)throw new IllegalArgumentException("Không tìm thấy dịch vụ.");r.setAttribute("item",x);r.getRequestDispatcher("/dich-vu/chi-tiet.jsp").forward(r,p);}private void del(HttpServletRequest r,HttpServletResponse p)throws IOException{service.xoa(req(r,"id"));p.sendRedirect(r.getContextPath()+"/dich-vu?action=list&success=deleted");}private static String req(HttpServletRequest r,String n){String x=r.getParameter(n);if(blank(x))throw new IllegalArgumentException("Thiếu tham số: "+n);return x.trim();}private static String trim(String x){return x==null?null:x.trim();}private static boolean blank(String x){return x==null||x.isBlank();}private static String v(String x,String f){return blank(x)?f:x;}
}
