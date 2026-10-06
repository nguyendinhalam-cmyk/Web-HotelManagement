package com.hotel.service;

import com.hotel.config.TransactionManager;
import com.hotel.dao.DichVuDAO;
import com.hotel.entity.DichVu;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;
import com.hotel.util.MaCodeGenerator;

public class DichVuService {
    private final DichVuDAO dao = new DichVuDAO();
    public void them(DichVu x){validate(x);TransactionManager tm=new TransactionManager();try{tm.begin();EntityManager em=tm.getEntityManager();if(x.getMaDichVu()==null||x.getMaDichVu().isBlank())x.setMaDichVu(MaCodeGenerator.nextId(dao.findAll(em), "maDichVu", "DV"));if(dao.findById(em,x.getMaDichVu())!=null)throw new IllegalArgumentException("Mã dịch vụ đã tồn tại: "+x.getMaDichVu());if(x.getDangKinhDoanh()==null)x.setDangKinhDoanh(true);dao.save(em,x);tm.commit();}catch(Exception e){tm.rollback();throw e;}finally{tm.close();}}
    public DichVu findById(String id){TransactionManager tm=new TransactionManager();try{return dao.findById(tm.getEntityManager(),id);}finally{tm.close();}}
    public List<DichVu> findAll(){TransactionManager tm=new TransactionManager();try{return dao.findAll(tm.getEntityManager());}finally{tm.close();}}
    public List<DichVu> findDangKinhDoanh(){return findAll().stream().filter(x->Boolean.TRUE.equals(x.getDangKinhDoanh())).toList();}
    public void capNhat(DichVu x){validate(x);TransactionManager tm=new TransactionManager();try{tm.begin();EntityManager em=tm.getEntityManager();if(dao.findById(em,x.getMaDichVu())==null)throw new IllegalArgumentException("Không tìm thấy dịch vụ: "+x.getMaDichVu());dao.update(em,x);tm.commit();}catch(Exception e){tm.rollback();throw e;}finally{tm.close();}}
    public void ngungKinhDoanh(String id){capNhatTrangThai(id,false);}
    public void moKinhDoanh(String id){capNhatTrangThai(id,true);}
    private void capNhatTrangThai(String id,boolean value){TransactionManager tm=new TransactionManager();try{tm.begin();EntityManager em=tm.getEntityManager();DichVu x=dao.findById(em,id);if(x==null)throw new IllegalArgumentException("Không tìm thấy dịch vụ: "+id);x.setDangKinhDoanh(value);tm.commit();}catch(Exception e){tm.rollback();throw e;}finally{tm.close();}}
    public void xoa(String id){TransactionManager tm=new TransactionManager();try{tm.begin();EntityManager em=tm.getEntityManager();DichVu x=dao.findById(em,id);if(x==null)throw new IllegalArgumentException("Không tìm thấy dịch vụ: "+id);dao.delete(em,x);tm.commit();}catch(Exception e){tm.rollback();throw e;}finally{tm.close();}}
    private void validate(DichVu x){if(x==null||blank(x.getTenDichVu())||x.getDonGia()==null||x.getDonGia().compareTo(BigDecimal.ZERO)<0)throw new IllegalArgumentException("Thông tin dịch vụ không hợp lệ.");}
    private boolean blank(String s){return s==null||s.isBlank();}
}
