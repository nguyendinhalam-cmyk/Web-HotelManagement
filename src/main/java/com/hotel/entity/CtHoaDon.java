package com.hotel.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "CT_HOA_DON")
public class CtHoaDon {

    @Id
    @Column(name = "maCtHoaDon", length = 20)
    private String maCtHoaDon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maHoaDon", nullable = false)
    private HoaDon hoaDon;

    @Column(name = "tenKhoanThu", nullable = false, length = 255)
    private String tenKhoanThu;

    @Column(name = "soLuong", nullable = false)
    private Integer soLuong;

    @Column(name = "donGia", nullable = false, precision = 12, scale = 2)
    private BigDecimal donGia;

    @Column(name = "thanhTien", nullable = false, precision = 12, scale = 2)
    private BigDecimal thanhTien;

    public CtHoaDon() {
    }

    public String getMaCtHoaDon() {
        return maCtHoaDon;
    }

    public void setMaCtHoaDon(String maCtHoaDon) {
        this.maCtHoaDon = maCtHoaDon;
    }

    public HoaDon getHoaDon() {
        return hoaDon;
    }

    public void setHoaDon(HoaDon hoaDon) {
        this.hoaDon = hoaDon;
    }

    public String getTenKhoanThu() {
        return tenKhoanThu;
    }

    public void setTenKhoanThu(String tenKhoanThu) {
        this.tenKhoanThu = tenKhoanThu;
    }

    public Integer getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(Integer soLuong) {
        this.soLuong = soLuong;
    }

    public BigDecimal getDonGia() {
        return donGia;
    }

    public void setDonGia(BigDecimal donGia) {
        this.donGia = donGia;
    }

    public BigDecimal getThanhTien() {
        return thanhTien;
    }

    public void setThanhTien(BigDecimal thanhTien) {
        this.thanhTien = thanhTien;
    }
}