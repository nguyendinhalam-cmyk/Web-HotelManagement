package com.hotel.dto;

import com.hotel.entity.CtDatPhong;
import com.hotel.entity.DatPhong;
import com.hotel.entity.KhachHang;
import com.hotel.entity.Phong;

import java.util.ArrayList;
import java.util.List;

/** Mapper giữa request DTO và Entity; Controller không tự ghép Entity phức tạp. */
public final class DatPhongMapper {
    private DatPhongMapper() {}

    public static DatPhong toEntity(DatPhongRequestDTO dto, KhachHang khachHang,
                                    List<Phong> rooms) {
        DatPhong datPhong = new DatPhong();
        datPhong.setKhachHang(khachHang);
        datPhong.setGhiChu(dto.getGhiChu());

        List<CtDatPhong> chiTiets = new ArrayList<>();
        for (int i = 0; i < dto.getChiTiet().size(); i++) {
            DatPhongRequestDTO.ChiTiet item = dto.getChiTiet().get(i);
            Phong phong = rooms.get(i);
            CtDatPhong ct = new CtDatPhong();
            ct.setPhong(phong);
            ct.setNgayNhan(item.getNgayNhan());
            ct.setNgayTra(item.getNgayTra());
            ct.setGiaPhong(phong.getLoaiPhong().getGiaCoBan());
            chiTiets.add(ct);
        }
        datPhong.setChiTietDatPhong(chiTiets);
        return datPhong;
    }
}
