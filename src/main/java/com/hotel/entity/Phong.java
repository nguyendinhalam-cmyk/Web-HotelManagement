package com.hotel.entity;

import com.hotel.enums.TrangThaiPhong;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PHONG")
public class Phong {

    @Id
    @Column(name = "maPhong", length = 20)
    private String maPhong;

    @Column(name = "soPhong", nullable = false, unique = true, length = 20)
    private String soPhong;

    @Column(name = "viTri", length = 100)
    private String viTri;

    @Enumerated(EnumType.STRING)
    @Column(name = "trangThai", nullable = false, length = 20)
    private TrangThaiPhong trangThai;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maLoaiPhong", nullable = false)
    private LoaiPhong loaiPhong;

    @OneToMany(mappedBy = "phong")
    private List<CtDatPhong> chiTietDatPhong = new ArrayList<>();

    public Phong() {
    }

    public String getMaPhong() {
        return maPhong;
    }

    public void setMaPhong(String maPhong) {
        this.maPhong = maPhong;
    }

    public String getSoPhong() {
        return soPhong;
    }

    public void setSoPhong(String soPhong) {
        this.soPhong = soPhong;
    }

    public String getViTri() {
        return viTri;
    }

    public void setViTri(String viTri) {
        this.viTri = viTri;
    }

    public TrangThaiPhong getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiPhong trangThai) {
        this.trangThai = trangThai;
    }

    public LoaiPhong getLoaiPhong() {
        return loaiPhong;
    }

    public void setLoaiPhong(LoaiPhong loaiPhong) {
        this.loaiPhong = loaiPhong;
    }

    public List<CtDatPhong> getChiTietDatPhong() {
        return chiTietDatPhong;
    }

    public void setChiTietDatPhong(List<CtDatPhong> chiTietDatPhong) {
        this.chiTietDatPhong = chiTietDatPhong;
    }
}