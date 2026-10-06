package com.hotel.service;

import com.hotel.config.TransactionManager;
import com.hotel.dao.NhanVienDAO;
import com.hotel.entity.NhanVien;
import jakarta.persistence.EntityManager;
import java.util.List;
import com.hotel.util.MaCodeGenerator;

public class NhanVienService {
    private final NhanVienDAO dao = new NhanVienDAO();

    public void them(NhanVien x) { validate(x); TransactionManager tm=new TransactionManager(); try { tm.begin(); EntityManager em=tm.getEntityManager();if(x.getMaNV()==null||x.getMaNV().isBlank())x.setMaNV(MaCodeGenerator.nextId(dao.findAll(em), "maNV", "NV")); if(dao.findById(em,x.getMaNV())!=null) throw new IllegalArgumentException("Mã nhân viên đã tồn tại: "+x.getMaNV()); ensureUnique(em,x,null); dao.save(em,x); tm.commit(); } catch(Exception e){tm.rollback();throw e;} finally{tm.close();} }
    public NhanVien findById(String id){TransactionManager tm=new TransactionManager();try{return dao.findById(tm.getEntityManager(),id);}finally{tm.close();}}
    public List<NhanVien> findAll(){TransactionManager tm=new TransactionManager();try{return dao.findAll(tm.getEntityManager());}finally{tm.close();}}
    public void capNhat(NhanVien x){validate(x);TransactionManager tm=new TransactionManager();try{tm.begin();EntityManager em=tm.getEntityManager();if(dao.findById(em,x.getMaNV())==null)throw new IllegalArgumentException("Không tìm thấy nhân viên: "+x.getMaNV());ensureUnique(em,x,x.getMaNV());dao.update(em,x);tm.commit();}catch(Exception e){tm.rollback();throw e;}finally{tm.close();}}
    public void xoa(String id){TransactionManager tm=new TransactionManager();try{tm.begin();EntityManager em=tm.getEntityManager();NhanVien x=dao.findById(em,id);if(x==null)throw new IllegalArgumentException("Không tìm thấy nhân viên: "+id);dao.delete(em,x);tm.commit();}catch(Exception e){tm.rollback();throw e;}finally{tm.close();}}
    private void validate(NhanVien x){if(x==null||blank(x.getHoTen())||blank(x.getSoDienThoai())||blank(x.getEmail())||blank(x.getMatKhauHash())||blank(x.getChucVu()))throw new IllegalArgumentException("Thông tin nhân viên không hợp lệ.");}
    private void ensureUnique(EntityManager em,NhanVien x,String current){for(NhanVien i:dao.findAll(em)){if(current!=null&&current.equals(i.getMaNV()))continue;if(x.getSoDienThoai().equals(i.getSoDienThoai()))throw new IllegalArgumentException("Số điện thoại đã được sử dụng.");if(x.getEmail().equals(i.getEmail()))throw new IllegalArgumentException("Email đã được sử dụng.");}}
    private boolean blank(String s){return s==null||s.isBlank();}
}
