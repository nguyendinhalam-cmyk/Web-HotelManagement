package com.hotel.service;

import com.hotel.config.TransactionManager;
import com.hotel.dao.LoaiPhongDAO;
import com.hotel.entity.LoaiPhong;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;
import com.hotel.util.MaCodeGenerator;

public class LoaiPhongService {
    private final LoaiPhongDAO dao = new LoaiPhongDAO();
    public void them(LoaiPhong x){validate(x);TransactionManager tm=new TransactionManager();try{tm.begin();EntityManager em=tm.getEntityManager();if(x.getMaLoaiPhong()==null||x.getMaLoaiPhong().isBlank())x.setMaLoaiPhong(MaCodeGenerator.nextId(dao.findAll(em), "maLoaiPhong", "LP"));if(dao.findById(em,x.getMaLoaiPhong())!=null)throw new IllegalArgumentException("Mã loại phòng đã tồn tại: "+x.getMaLoaiPhong());ensureName(em,x,null);dao.save(em,x);tm.commit();}catch(Exception e){tm.rollback();throw e;}finally{tm.close();}}
    public LoaiPhong findById(String id){TransactionManager tm=new TransactionManager();try{return dao.findById(tm.getEntityManager(),id);}finally{tm.close();}}
    public List<LoaiPhong> findAll(){TransactionManager tm=new TransactionManager();try{return dao.findAll(tm.getEntityManager());}finally{tm.close();}}
    public void capNhat(LoaiPhong x){validate(x);TransactionManager tm=new TransactionManager();try{tm.begin();EntityManager em=tm.getEntityManager();if(dao.findById(em,x.getMaLoaiPhong())==null)throw new IllegalArgumentException("Không tìm thấy loại phòng: "+x.getMaLoaiPhong());ensureName(em,x,x.getMaLoaiPhong());dao.update(em,x);tm.commit();}catch(Exception e){tm.rollback();throw e;}finally{tm.close();}}
    public void xoa(String id){TransactionManager tm=new TransactionManager();try{tm.begin();EntityManager em=tm.getEntityManager();LoaiPhong x=dao.findById(em,id);if(x==null)throw new IllegalArgumentException("Không tìm thấy loại phòng: "+id);dao.delete(em,x);tm.commit();}catch(Exception e){tm.rollback();throw e;}finally{tm.close();}}
    private void validate(LoaiPhong x){if(x==null||blank(x.getTenLoaiPhong())||x.getGiaCoBan()==null||x.getGiaCoBan().compareTo(BigDecimal.ZERO)<0||x.getSoNguoiToiDa()==null||x.getSoNguoiToiDa()<=0)throw new IllegalArgumentException("Thông tin loại phòng không hợp lệ.");}
    private void ensureName(EntityManager em,LoaiPhong x,String current){for(LoaiPhong i:dao.findAll(em)){if(current!=null&&current.equals(i.getMaLoaiPhong()))continue;if(x.getTenLoaiPhong().equalsIgnoreCase(i.getTenLoaiPhong()))throw new IllegalArgumentException("Tên loại phòng đã tồn tại.");}}
    private boolean blank(String s){return s==null||s.isBlank();}
}
