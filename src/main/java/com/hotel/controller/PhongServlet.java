package com.hotel.controller;

import com.hotel.entity.LoaiPhong;
import com.hotel.entity.Phong;
import com.hotel.enums.TrangThaiPhong;
import com.hotel.service.LoaiPhongService;
import com.hotel.service.PhongService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/phong")
public class PhongServlet extends HttpServlet{
    private PhongService service;private LoaiPhongService loaiPhongService;@Override public void init(){service=new PhongService();loaiPhongService=new LoaiPhongService();}
    @Override protected void doGet(HttpServletRequest r,HttpServletResponse p)throws ServletException,IOException{String a=v(r.getParameter("action"),"list");try{switch(a){case"new"->form(r,p,null);case"edit"->form(r,p,service.findById(req(r,"id")));case"delete"->del(r,p);case"status"->status(r,p);case"detail"->detail(r,p);default->list(r,p);}}catch(IllegalArgumentException|IllegalStateException e){r.setAttribute("error",e.getMessage());list(r,p);}}
    @Override protected void doPost(HttpServletRequest r,HttpServletResponse p)throws ServletException,IOException{try{Phong x=new Phong();x.setMaPhong(trim(r.getParameter("maPhong")));x.setSoPhong(req(r,"soPhong"));x.setViTri(r.getParameter("viTri"));x.setTrangThai(en(TrangThaiPhong.class,r.getParameter("trangThai")));LoaiPhong lp=loaiPhongService.findById(req(r,"maLoaiPhong"));if(lp==null)throw new IllegalArgumentException("Không tìm thấy loại phòng.");x.setLoaiPhong(lp);if(blank(x.getMaPhong()))service.them(x);else service.capNhat(x);p.sendRedirect(r.getContextPath()+"/phong?action=list&success=saved");}catch(IllegalArgumentException|IllegalStateException e){r.setAttribute("error",e.getMessage());list(r,p);}}
    private void status(HttpServletRequest r,HttpServletResponse p)throws IOException{service.capNhatTrangThai(req(r,"id"),en(TrangThaiPhong.class,req(r,"trangThai")));p.sendRedirect(r.getContextPath()+"/phong?action=list&success=status");}private void list(HttpServletRequest r,HttpServletResponse p)throws ServletException,IOException{r.setAttribute("items",service.findAll());r.getRequestDispatcher("/phong/danh-sach.jsp").forward(r,p);}private void form(HttpServletRequest r,HttpServletResponse p,Phong x)throws ServletException,IOException{r.setAttribute("item",x);r.setAttribute("loaiPhongs",loaiPhongService.findAll());r.setAttribute("trangThais",TrangThaiPhong.values());r.getRequestDispatcher("/phong/form.jsp").forward(r,p);}private void detail(HttpServletRequest r,HttpServletResponse p)throws ServletException,IOException{Phong x=service.findById(req(r,"id"));if(x==null)throw new IllegalArgumentException("Không tìm thấy phòng.");r.setAttribute("item",x);r.getRequestDispatcher("/phong/chi-tiet.jsp").forward(r,p);}private void del(HttpServletRequest r,HttpServletResponse p)throws IOException{service.xoa(req(r,"id"));p.sendRedirect(r.getContextPath()+"/phong?action=list&success=deleted");}private static String req(HttpServletRequest r,String n){String x=r.getParameter(n);if(blank(x))throw new IllegalArgumentException("Thiếu tham số: "+n);return x.trim();}private static String trim(String x){return x==null?null:x.trim();}private static boolean blank(String x){return x==null||x.isBlank();}private static String v(String x,String f){return blank(x)?f:x;}private static<E extends Enum<E>>E en(Class<E> c,String x){return blank(x)?null:Enum.valueOf(c,x);}
}
