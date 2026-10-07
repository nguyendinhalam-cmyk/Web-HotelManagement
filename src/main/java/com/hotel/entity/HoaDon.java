package com.hotel.entity;

import com.hotel.enums.TrangThaiHoaDon;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "HOA_DON")
public class HoaDon {

    @Id
    @Column(name = "maHoaDon", length = 20)
    private String maHoaDon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maDatPhong", nullable = false)
    private DatPhong datPhong;

    @Column(name = "ngayLap", nullable = false)
    private LocalDateTime ngayLap;

    @Column(name = "tongTienPhong", nullable = false, precision = 12, scale = 2)
    private BigDecimal tongTienPhong;

    @Column(name = "tongTienDichVu", nullable = false, precision = 12, scale = 2)
    private BigDecimal tongTienDichVu;

    @Column(name = "tongTien", nullable = false, precision = 12, scale = 2)
    private BigDecimal tongTien;

    @Enumerated(EnumType.STRING)
    @Column(name = "trangThai", nullable = false, length = 20)
    private TrangThaiHoaDon trangThai;

    @OneToMany(mappedBy = "hoaDon")
    private List<CtHoaDon> chiTietHoaDon = new ArrayList<>();

    public HoaDon() {
    }

    public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public DatPhong getDatPhong() {
        return datPhong;
    }

    public void setDatPhong(DatPhong datPhong) {
        this.datPhong = datPhong;
    }

    public LocalDateTime getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(LocalDateTime ngayLap) {
        this.ngayLap = ngayLap;
    }

    public BigDecimal getTongTienPhong() {
        return tongTienPhong;
    }

    public void setTongTienPhong(BigDecimal tongTienPhong) {
        this.tongTienPhong = tongTienPhong;
    }

    public BigDecimal getTongTienDichVu() {
        return tongTienDichVu;
    }

    public void setTongTienDichVu(BigDecimal tongTienDichVu) {
        this.tongTienDichVu = tongTienDichVu;
    }

    public BigDecimal getTongTien() {
        return tongTien;
    }

    public void setTongTien(BigDecimal tongTien) {
        this.tongTien = tongTien;
    }

    public TrangThaiHoaDon getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiHoaDon trangThai) {
        this.trangThai = trangThai;
    }

    public List<CtHoaDon> getChiTietHoaDon() {
        return chiTietHoaDon;
    }

    public void setChiTietHoaDon(List<CtHoaDon> chiTietHoaDon) {
        this.chiTietHoaDon = chiTietHoaDon;
    }
}