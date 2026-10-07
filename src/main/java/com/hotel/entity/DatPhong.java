package com.hotel.entity;

import com.hotel.enums.TrangThaiDatPhong;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "DAT_PHONG")
public class DatPhong {

    @Id
    @Column(name = "maDatPhong", length = 20)
    private String maDatPhong;

    @Column(name = "ngayDat", nullable = false)
    private LocalDateTime ngayDat;

    @Enumerated(EnumType.STRING)
    @Column(name = "trangThai", nullable = false, length = 20)
    private TrangThaiDatPhong trangThai;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maKH", nullable = false)
    private KhachHang khachHang;

    /** Nhân viên tiếp nhận/xử lý đặt phòng tại quầy/điện thoại. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maNVXuLy", nullable = false)
    private NhanVien nhanVienXuLy;

    /** Nhân viên thực hiện check-in. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maNVCheckIn")
    private NhanVien nhanVienCheckIn;

    /** Nhân viên thực hiện check-out. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maNVCheckOut")
    private NhanVien nhanVienCheckOut;

    @Column(name = "ghiChu", length = 255)
    private String ghiChu;

    @OneToMany(mappedBy = "datPhong")
    private List<CtDatPhong> chiTietDatPhong = new ArrayList<>();

    @OneToMany(mappedBy = "datPhong")
    private List<SuDungDichVu> suDungDichVu = new ArrayList<>();

    @OneToMany(mappedBy = "datPhong")
    private List<ThanhToan> thanhToans = new ArrayList<>();

    @OneToMany(mappedBy = "datPhong")
    private List<HoaDon> hoaDons = new ArrayList<>();

    public DatPhong() {
    }

    public String getMaDatPhong() {
        return maDatPhong;
    }

    public void setMaDatPhong(String maDatPhong) {
        this.maDatPhong = maDatPhong;
    }

    public LocalDateTime getNgayDat() {
        return ngayDat;
    }

    public void setNgayDat(LocalDateTime ngayDat) {
        this.ngayDat = ngayDat;
    }

    public TrangThaiDatPhong getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiDatPhong trangThai) {
        this.trangThai = trangThai;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
    }

    public NhanVien getNhanVienXuLy() { return nhanVienXuLy; }
    public void setNhanVienXuLy(NhanVien nhanVienXuLy) { this.nhanVienXuLy = nhanVienXuLy; }

    public NhanVien getNhanVienCheckIn() { return nhanVienCheckIn; }
    public void setNhanVienCheckIn(NhanVien nhanVienCheckIn) { this.nhanVienCheckIn = nhanVienCheckIn; }

    public NhanVien getNhanVienCheckOut() { return nhanVienCheckOut; }
    public void setNhanVienCheckOut(NhanVien nhanVienCheckOut) { this.nhanVienCheckOut = nhanVienCheckOut; }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public List<CtDatPhong> getChiTietDatPhong() {
        return chiTietDatPhong;
    }

    public void setChiTietDatPhong(List<CtDatPhong> chiTietDatPhong) {
        this.chiTietDatPhong = chiTietDatPhong;
    }

    public List<SuDungDichVu> getSuDungDichVu() {
        return suDungDichVu;
    }

    public void setSuDungDichVu(List<SuDungDichVu> suDungDichVu) {
        this.suDungDichVu = suDungDichVu;
    }

    public List<ThanhToan> getThanhToans() {
        return thanhToans;
    }

    public void setThanhToans(List<ThanhToan> thanhToans) {
        this.thanhToans = thanhToans;
    }

    public List<HoaDon> getHoaDons() {
        return hoaDons;
    }

    public void setHoaDons(List<HoaDon> hoaDons) {
        this.hoaDons = hoaDons;
    }
}