package com.hotel.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "DICH_VU")
public class DichVu {

    @Id
    @Column(name = "maDichVu", length = 20)
    private String maDichVu;

    @Column(name = "tenDichVu", nullable = false, length = 100)
    private String tenDichVu;

    @Column(name = "moTa", length = 255)
    private String moTa;

    @Column(name = "donGia", nullable = false, precision = 12, scale = 2)
    private BigDecimal donGia;

    @Column(name = "loaiDichVu", length = 50)
    private String loaiDichVu;

    @Column(name = "dangKinhDoanh", nullable = false)
    private Boolean dangKinhDoanh = true;

    @OneToMany(mappedBy = "dichVu")
    private List<SuDungDichVu> suDungDichVu = new ArrayList<>();

    public DichVu() {
    }

    public String getMaDichVu() {
        return maDichVu;
    }

    public void setMaDichVu(String maDichVu) {
        this.maDichVu = maDichVu;
    }

    public String getTenDichVu() {
        return tenDichVu;
    }

    public void setTenDichVu(String tenDichVu) {
        this.tenDichVu = tenDichVu;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public BigDecimal getDonGia() {
        return donGia;
    }

    public void setDonGia(BigDecimal donGia) {
        this.donGia = donGia;
    }

    public String getLoaiDichVu() {
        return loaiDichVu;
    }

    public void setLoaiDichVu(String loaiDichVu) {
        this.loaiDichVu = loaiDichVu;
    }

    public Boolean getDangKinhDoanh() {
        return dangKinhDoanh;
    }

    public void setDangKinhDoanh(Boolean dangKinhDoanh) {
        this.dangKinhDoanh = dangKinhDoanh;
    }

    public List<SuDungDichVu> getSuDungDichVu() {
        return suDungDichVu;
    }

    public void setSuDungDichVu(List<SuDungDichVu> suDungDichVu) {
        this.suDungDichVu = suDungDichVu;
    }
}