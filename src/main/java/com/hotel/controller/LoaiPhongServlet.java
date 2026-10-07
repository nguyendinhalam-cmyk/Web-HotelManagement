package com.hotel.controller;

import com.hotel.entity.LoaiPhong;
import com.hotel.service.LoaiPhongService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/loai-phong")
public class LoaiPhongServlet extends HttpServlet{
 private LoaiPhongService service;@Override public void init(){service=new LoaiPhongService();}
 @Override protected void doGet(HttpServletRequest r,HttpServletResponse p)throws ServletException,IOException{String a=v(r.getParameter("action"),"list");try{switch(a){case"new"->form(r,p,null);case"edit"->form(r,p,service.findById(req(r,"id")));case"delete"->del(r,p);case"detail"->detail(r,p);default->list(r,p);}}catch(IllegalArgumentException|IllegalStateException e){r.setAttribute("error",e.getMessage());list(r,p);}}
 @Override protected void doPost(HttpServletRequest r,HttpServletResponse p)throws ServletException,IOException{try{LoaiPhong x=new LoaiPhong();x.setMaLoaiPhong(trim(r.getParameter("maLoaiPhong")));x.setTenLoaiPhong(req(r,"tenLoaiPhong"));x.setMoTa(r.getParameter("moTa"));x.setGiaCoBan(new BigDecimal(req(r,"giaCoBan")));x.setSoNguoiToiDa(Integer.valueOf(req(r,"soNguoiToiDa")));if(blank(x.getMaLoaiPhong()))service.them(x);else service.capNhat(x);p.sendRedirect(r.getContextPath()+"/loai-phong?action=list&success=saved");}catch(IllegalArgumentException|IllegalStateException e){r.setAttribute("error",e.getMessage());list(r,p);}}
 private void list(HttpServletRequest r,HttpServletResponse p)throws ServletException,IOException{r.setAttribute("items",service.findAll());r.getRequestDispatcher("/loai-phong/danh-sach.jsp").forward(r,p);}private void form(HttpServletRequest r,HttpServletResponse p,LoaiPhong x)throws ServletException,IOException{r.setAttribute("item",x);r.getRequestDispatcher("/loai-phong/form.jsp").forward(r,p);}private void detail(HttpServletRequest r,HttpServletResponse p)throws ServletException,IOException{LoaiPhong x=service.findById(req(r,"id"));if(x==null)throw new IllegalArgumentException("Không tìm thấy loại phòng.");r.setAttribute("item",x);r.getRequestDispatcher("/loai-phong/chi-tiet.jsp").forward(r,p);}private void del(HttpServletRequest r,HttpServletResponse p)throws IOException{service.xoa(req(r,"id"));p.sendRedirect(r.getContextPath()+"/loai-phong?action=list&success=deleted");}private static String req(HttpServletRequest r,String n){String x=r.getParameter(n);if(blank(x))throw new IllegalArgumentException("Thiếu tham số: "+n);return x.trim();}private static String trim(String x){return x==null?null:x.trim();}private static boolean blank(String x){return x==null||x.isBlank();}private static String v(String x,String f){return blank(x)?f:x;}
}
