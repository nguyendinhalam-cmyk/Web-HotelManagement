package com.hotel.dto;

import com.hotel.enums.TrangThaiDatPhong;
import java.time.LocalDateTime;

/** DTO nhẹ dùng cho danh sách đặt phòng. */
public class DatPhongListDTO {
    private String maDatPhong;
    private LocalDateTime ngayDat;
    private String maKH;
    private String tenKhachHang;
    private String maNVXuLy;
    private String maNVCheckIn;
    private String maNVCheckOut;
    private TrangThaiDatPhong trangThai;
    private String ghiChu;

    public String getMaDatPhong() { return maDatPhong; }
    public void setMaDatPhong(String maDatPhong) { this.maDatPhong = maDatPhong; }
    public LocalDateTime getNgayDat() { return ngayDat; }
    public void setNgayDat(LocalDateTime ngayDat) { this.ngayDat = ngayDat; }
    public String getMaKH() { return maKH; }
    public void setMaKH(String maKH) { this.maKH = maKH; }
    public String getTenKhachHang() { return tenKhachHang; }
    public void setTenKhachHang(String tenKhachHang) { this.tenKhachHang = tenKhachHang; }
    public String getMaNVXuLy() { return maNVXuLy; }
    public void setMaNVXuLy(String maNVXuLy) { this.maNVXuLy = maNVXuLy; }
    public String getMaNVCheckIn() { return maNVCheckIn; }
    public void setMaNVCheckIn(String maNVCheckIn) { this.maNVCheckIn = maNVCheckIn; }
    public String getMaNVCheckOut() { return maNVCheckOut; }
    public void setMaNVCheckOut(String maNVCheckOut) { this.maNVCheckOut = maNVCheckOut; }
    public TrangThaiDatPhong getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiDatPhong trangThai) { this.trangThai = trangThai; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
}
