package com.hotel.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "CT_DAT_PHONG",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_ct_dat_phong",
                        columnNames = {"maDatPhong", "maPhong"}
                )
        }
)
public class CtDatPhong {

    @Id
    @Column(name = "maCTDatPhong", length = 20)
    private String maCTDatPhong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maDatPhong", nullable = false)
    private DatPhong datPhong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maPhong", nullable = false)
    private Phong phong;

    @Column(name = "ngayNhan", nullable = false)
    private LocalDate ngayNhan;

    @Column(name = "ngayTra", nullable = false)
    private LocalDate ngayTra;

    @Column(name = "giaPhong", nullable = false, precision = 12, scale = 2)
    private BigDecimal giaPhong;

    public CtDatPhong() {
    }

    public String getMaCTDatPhong() {
        return maCTDatPhong;
    }

    public void setMaCTDatPhong(String maCTDatPhong) {
        this.maCTDatPhong = maCTDatPhong;
    }

    public DatPhong getDatPhong() {
        return datPhong;
    }

    public void setDatPhong(DatPhong datPhong) {
        this.datPhong = datPhong;
    }

    public Phong getPhong() {
        return phong;
    }

    public void setPhong(Phong phong) {
        this.phong = phong;
    }

    public LocalDate getNgayNhan() {
        return ngayNhan;
    }

    public void setNgayNhan(LocalDate ngayNhan) {
        this.ngayNhan = ngayNhan;
    }

    public LocalDate getNgayTra() {
        return ngayTra;
    }

    public void setNgayTra(LocalDate ngayTra) {
        this.ngayTra = ngayTra;
    }

    public BigDecimal getGiaPhong() {
        return giaPhong;
    }

    public void setGiaPhong(BigDecimal giaPhong) {
        this.giaPhong = giaPhong;
    }
}