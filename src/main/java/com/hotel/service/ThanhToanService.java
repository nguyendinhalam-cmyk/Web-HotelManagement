package com.hotel.service;

import com.hotel.config.TransactionManager;
import com.hotel.dao.DatPhongDAO;
import com.hotel.dao.ThanhToanDAO;
import com.hotel.entity.DatPhong;
import com.hotel.entity.ThanhToan;
import com.hotel.enums.PhuongThucThanhToan;
import com.hotel.enums.TrangThaiDatPhong;
import com.hotel.enums.TrangThaiThanhToan;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.hotel.util.MaCodeGenerator;

public class ThanhToanService {
    private final ThanhToanDAO dao=new ThanhToanDAO();
    private final DatPhongDAO datPhongDAO=new DatPhongDAO();

    public void taoThanhToan(ThanhToan x){
        validate(x);
        TransactionManager tm=new TransactionManager();
        try{tm.begin();EntityManager em=tm.getEntityManager();
            if(x.getMaThanhToan()==null||x.getMaThanhToan().isBlank())x.setMaThanhToan(MaCodeGenerator.nextId(dao.findAll(em), "maThanhToan", "TT"));
            if(dao.findById(em,x.getMaThanhToan())!=null)throw new IllegalArgumentException("Mã thanh toán đã tồn tại: "+x.getMaThanhToan());
            if(x.getDatPhong()==null||x.getDatPhong().getMaDatPhong()==null)throw new IllegalArgumentException("Thiếu mã đặt phòng.");
            DatPhong dp=datPhongDAO.findById(em,x.getDatPhong().getMaDatPhong());
            if(dp==null)throw new IllegalArgumentException("Không tìm thấy đặt phòng: "+x.getDatPhong().getMaDatPhong());
            if(dp.getTrangThai()==TrangThaiDatPhong.DA_HUY)throw new IllegalStateException("Không thể thanh toán cho đặt phòng đã hủy.");
            x.setDatPhong(dp); if(x.getThoiGianTao()==null)x.setThoiGianTao(LocalDateTime.now()); if(x.getTrangThai()==null)x.setTrangThai(TrangThaiThanhToan.CHO_THANH_TOAN);
            dao.save(em,x);tm.commit();
        }catch(Exception e){tm.rollback();throw e;}finally{tm.close();}
    }

    public ThanhToan findById(String id){TransactionManager tm=new TransactionManager();try{return dao.findById(tm.getEntityManager(),id);}finally{tm.close();}}
    public List<ThanhToan> findByDatPhong(String maDatPhong){TransactionManager tm=new TransactionManager();try{return dao.findByDatPhong(tm.getEntityManager(),maDatPhong);}finally{tm.close();}}
    public List<ThanhToan> findAll(){TransactionManager tm=new TransactionManager();try{return dao.findAll(tm.getEntityManager());}finally{tm.close();}}

    public void thanhToanThanhCong(String id,String maGiaoDich){updateStatus(id,TrangThaiThanhToan.DA_THANH_TOAN,maGiaoDich);}
    public void thanhToanThatBai(String id,String maGiaoDich){updateStatus(id,TrangThaiThanhToan.THAT_BAI,maGiaoDich);}
    public void hoanTien(String id){
        TransactionManager tm=new TransactionManager();
        try{tm.begin();EntityManager em=tm.getEntityManager();ThanhToan x=dao.findById(em,id);if(x==null)throw new IllegalArgumentException("Không tìm thấy thanh toán: "+id);if(x.getTrangThai()!=TrangThaiThanhToan.DA_THANH_TOAN)throw new IllegalStateException("Chỉ thanh toán đã thành công mới được hoàn tiền.");x.setTrangThai(TrangThaiThanhToan.DA_HOAN_TIEN);tm.commit();}catch(Exception e){tm.rollback();throw e;}finally{tm.close();}
    }
    public void capNhatPhuongThuc(String id,PhuongThucThanhToan phuongThuc){if(phuongThuc==null)throw new IllegalArgumentException("Phương thức không được null.");TransactionManager tm=new TransactionManager();try{tm.begin();EntityManager em=tm.getEntityManager();ThanhToan x=dao.findById(em,id);if(x==null)throw new IllegalArgumentException("Không tìm thấy thanh toán: "+id);if(x.getTrangThai()!=TrangThaiThanhToan.CHO_THANH_TOAN)throw new IllegalStateException("Chỉ thanh toán đang chờ mới được đổi phương thức.");x.setPhuongThuc(phuongThuc);tm.commit();}catch(Exception e){tm.rollback();throw e;}finally{tm.close();}}

    private void updateStatus(String id,TrangThaiThanhToan status,String maGiaoDich){TransactionManager tm=new TransactionManager();try{tm.begin();EntityManager em=tm.getEntityManager();ThanhToan x=dao.findById(em,id);if(x==null)throw new IllegalArgumentException("Không tìm thấy thanh toán: "+id);if(x.getTrangThai()==TrangThaiThanhToan.DA_HOAN_TIEN)throw new IllegalStateException("Thanh toán đã hoàn tiền.");x.setTrangThai(status);if(maGiaoDich!=null&&!maGiaoDich.isBlank())x.setMaGiaoDich(maGiaoDich);if(status==TrangThaiThanhToan.DA_THANH_TOAN)x.setThoiGianThanhToan(LocalDateTime.now());else x.setThoiGianThanhToan(null);tm.commit();}catch(Exception e){tm.rollback();throw e;}finally{tm.close();}}
    private void validate(ThanhToan x){if(x==null||x.getSoTien()==null||x.getSoTien().compareTo(BigDecimal.ZERO)<=0||x.getPhuongThuc()==null)throw new IllegalArgumentException("Thông tin thanh toán không hợp lệ.");}
    private boolean blank(String s){return s==null||s.isBlank();}
}
