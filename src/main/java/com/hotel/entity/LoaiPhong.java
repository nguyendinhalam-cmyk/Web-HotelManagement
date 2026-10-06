package com.hotel.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "LOAI_PHONG")
public class LoaiPhong {

    @Id
    @Column(name = "maLoaiPhong", length = 20)
    private String maLoaiPhong;

    @Column(name = "tenLoaiPhong", nullable = false, unique = true, length = 100)
    private String tenLoaiPhong;

    @Column(name = "moTa", length = 255)
    private String moTa;

    @Column(name = "giaCoBan", nullable = false, precision = 12, scale = 2)
    private BigDecimal giaCoBan;

    @Column(name = "soNguoiToiDa", nullable = false)
    private Integer soNguoiToiDa;

    @OneToMany(mappedBy = "loaiPhong")
    private List<Phong> danhSachPhong = new ArrayList<>();

    public LoaiPhong() {
    }

    public String getMaLoaiPhong() {
        return maLoaiPhong;
    }

    public void setMaLoaiPhong(String maLoaiPhong) {
        this.maLoaiPhong = maLoaiPhong;
    }

    public String getTenLoaiPhong() {
        return tenLoaiPhong;
    }

    public void setTenLoaiPhong(String tenLoaiPhong) {
        this.tenLoaiPhong = tenLoaiPhong;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public BigDecimal getGiaCoBan() {
        return giaCoBan;
    }

    public void setGiaCoBan(BigDecimal giaCoBan) {
        this.giaCoBan = giaCoBan;
    }

    public Integer getSoNguoiToiDa() {
        return soNguoiToiDa;
    }

    public void setSoNguoiToiDa(Integer soNguoiToiDa) {
        this.soNguoiToiDa = soNguoiToiDa;
    }

    public List<Phong> getDanhSachPhong() {
        return danhSachPhong;
    }

    public void setDanhSachPhong(List<Phong> danhSachPhong) {
        this.danhSachPhong = danhSachPhong;
    }
}