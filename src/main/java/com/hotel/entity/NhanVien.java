package com.hotel.entity;

import com.hotel.enums.GioiTinh;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "NHAN_VIEN")
public class NhanVien {

    @Id
    @Column(name = "maNV", length = 20)
    private String maNV;

    @Column(name = "hoTen", nullable = false, length = 100)
    private String hoTen;

    @Column(name = "ngaySinh")
    private LocalDate ngaySinh;

    @Enumerated(EnumType.STRING)
    @Column(name = "gioiTinh", length = 10)
    private GioiTinh gioiTinh;

    @Column(name = "soDienThoai", nullable = false, unique = true, length = 15)
    private String soDienThoai;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "diaChi", length = 255)
    private String diaChi;

    public NhanVien() {
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
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

    @OneToOne(mappedBy = "nhanVien", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private TaiKhoan taiKhoan;

    public TaiKhoan getTaiKhoan() {
        return taiKhoan;
    }

    public void setTaiKhoan(TaiKhoan taiKhoan) {
        this.taiKhoan = taiKhoan;
        if (taiKhoan != null && taiKhoan.getNhanVien() != this) {
            taiKhoan.setNhanVien(this);
        }
    }
}