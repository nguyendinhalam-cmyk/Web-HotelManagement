package com.hotel.entity;

import com.hotel.enums.GioiTinh;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "KHACH_HANG")
public class KhachHang {

    @Id
    @Column(name = "maKH", length = 20)
    private String maKH;

    @Column(name = "hoTen", nullable = false, length = 100)
    private String hoTen;

    @Column(name = "cccd", length = 12, unique = true)
    private String cccd;

    @Column(name = "ngaySinh")
    private LocalDate ngaySinh;

    @Enumerated(EnumType.STRING)
    @Column(name = "gioiTinh", length = 10)
    private GioiTinh gioiTinh;

    @Column(name = "soDienThoai", nullable = false, unique = true, length = 15)
    private String soDienThoai;

    @Column(name = "email", unique = true, length = 100)
    private String email;

    @Column(name = "diaChi", length = 255)
    private String diaChi;

    @OneToMany(mappedBy = "khachHang")
    private List<DatPhong> danhSachDatPhong = new ArrayList<>();

    public KhachHang() {
    }

    public String getMaKH() {
        return maKH;
    }

    public void setMaKH(String maKH) {
        this.maKH = maKH;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public GioiTinh getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(GioiTinh gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public List<DatPhong> getDanhSachDatPhong() {
        return danhSachDatPhong;
    }

    public void setDanhSachDatPhong(List<DatPhong> danhSachDatPhong) {
        this.danhSachDatPhong = danhSachDatPhong;
    }
}

