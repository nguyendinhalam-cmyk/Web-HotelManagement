package com.hotel.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "SU_DUNG_DICH_VU")
public class SuDungDichVu {

    @Id
    @Column(name = "maSuDungDichVu", length = 20)
    private String maSuDungDichVu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maDatPhong", nullable = false)
    private DatPhong datPhong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maDichVu", nullable = false)
    private DichVu dichVu;

    @Column(name = "soLuong", nullable = false)
    private Integer soLuong;

    @Column(name = "donGia", nullable = false, precision = 12, scale = 2)
    private BigDecimal donGia;

    @Column(name = "thoiGianSuDung", nullable = false)
    private LocalDateTime thoiGianSuDung;

    public SuDungDichVu() {
    }

    public String getMaSuDungDichVu() {
        return maSuDungDichVu;
    }

    public void setMaSuDungDichVu(String maSuDungDichVu) {
        this.maSuDungDichVu = maSuDungDichVu;
    }

    public DatPhong getDatPhong() {
        return datPhong;
    }

    public void setDatPhong(DatPhong datPhong) {
        this.datPhong = datPhong;
    }

    public DichVu getDichVu() {
        return dichVu;
    }

    public void setDichVu(DichVu dichVu) {
        this.dichVu = dichVu;
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

    public LocalDateTime getThoiGianSuDung() {
        return thoiGianSuDung;
    }

    public void setThoiGianSuDung(LocalDateTime thoiGianSuDung) {
        this.thoiGianSuDung = thoiGianSuDung;
    }
}