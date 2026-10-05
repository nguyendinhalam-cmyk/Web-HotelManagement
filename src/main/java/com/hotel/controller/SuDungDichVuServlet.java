package com.hotel.controller;

import com.hotel.entity.DatPhong;
import com.hotel.entity.DichVu;
import com.hotel.entity.SuDungDichVu;
import com.hotel.service.DatPhongService;
import com.hotel.service.DichVuService;
import com.hotel.service.SuDungDichVuService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/su-dung-dich-vu")
public class SuDungDichVuServlet extends HttpServlet{
 private SuDungDichVuService service;private DatPhongService datPhongService;private DichVuService dichVuService;@Override public void init(){service=new SuDungDichVuService();datPhongService=new DatPhongService();dichVuService=new DichVuService();}
 @Override protected void doGet(HttpServletRequest r,HttpServletResponse p)throws ServletException,IOException{String a=v(r.getParameter("action"),"list");try{switch(a){case"new"->form(r,p,null);case"edit"->form(r,p,service.findById(req(r,"id")));case"delete"->del(r,p);case"by-dat-phong"->byDatPhong(r,p);case"detail"->detail(r,p);default->list(r,p);}}catch(IllegalArgumentException|IllegalStateException e){r.setAttribute("error",e.getMessage());list(r,p);}}
 @Override protected void doPost(HttpServletRequest r,HttpServletResponse p)throws ServletException,IOException{try{SuDungDichVu x=new SuDungDichVu();x.setMaSuDungDichVu(trim(r.getParameter("maSuDungDichVu")));DatPhong dp=datPhongService.findById(req(r,"maDatPhong"));DichVu dv=dichVuService.findById(req(r,"maDichVu"));if(dp==null||dv==null)throw new IllegalArgumentException("Đặt phòng hoặc dịch vụ không tồn tại.");x.setDatPhong(dp);x.setDichVu(dv);x.setSoLuong(Integer.valueOf(req(r,"soLuong")));x.setDonGia(dv.getDonGia());if(blank(x.getMaSuDungDichVu()))service.them(x);else service.capNhatSoLuong(x.getMaSuDungDichVu(),x.getSoLuong());p.sendRedirect(r.getContextPath()+"/su-dung-dich-vu?action=list&success=saved");}catch(IllegalArgumentException|IllegalStateException e){r.setAttribute("error",e.getMessage());list(r,p);}}
 private void byDatPhong(HttpServletRequest r,HttpServletResponse p)throws ServletException,IOException{r.setAttribute("items",service.findByDatPhong(req(r,"maDatPhong")));r.setAttribute("maDatPhong",req(r,"maDatPhong"));r.getRequestDispatcher("/dich-vu/su-dung-danh-sach.jsp").forward(r,p);}private void list(HttpServletRequest r,HttpServletResponse p)throws ServletException,IOException{r.setAttribute("items",service.findAll());r.getRequestDispatcher("/dich-vu/su-dung-danh-sach.jsp").forward(r,p);}private void form(HttpServletRequest r,HttpServletResponse p,SuDungDichVu x)throws ServletException,IOException{r.setAttribute("item",x);r.setAttribute("datPhongs",datPhongService.findAllWithKhachHang());r.setAttribute("dichVus",dichVuService.findDangKinhDoanh());r.getRequestDispatcher("/dich-vu/su-dung-form.jsp").forward(r,p);}private void detail(HttpServletRequest r,HttpServletResponse p)throws ServletException,IOException{SuDungDichVu x=service.findById(req(r,"id"));if(x==null)throw new IllegalArgumentException("Không tìm thấy sử dụng dịch vụ.");r.setAttribute("item",x);r.getRequestDispatcher("/dich-vu/su-dung-chi-tiet.jsp").forward(r,p);}private void del(HttpServletRequest r,HttpServletResponse p)throws IOException{service.xoa(req(r,"id"));p.sendRedirect(r.getContextPath()+"/su-dung-dich-vu?action=list&success=deleted");}private static String req(HttpServletRequest r,String n){String x=r.getParameter(n);if(blank(x))throw new IllegalArgumentException("Thiếu tham số: "+n);return x.trim();}private static String trim(String x){return x==null?null:x.trim();}private static boolean blank(String x){return x==null||x.isBlank();}private static String v(String x,String f){return blank(x)?f:x;}
}
