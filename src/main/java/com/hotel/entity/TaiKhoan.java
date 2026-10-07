package com.hotel.entity;

import com.hotel.enums.TrangThaiTaiKhoan;
import com.hotel.enums.VaiTro;
import jakarta.persistence.*;

@Entity
@Table(name = "TAI_KHOAN")
public class TaiKhoan {
    @Id
    @Column(name = "maTK", length = 20)
    private String maTK;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "maNV", nullable = false, unique = true)
    private NhanVien nhanVien;

    @Column(name = "matKhauHash", nullable = false, length = 255)
    private String matKhauHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "vaiTro", nullable = false, length = 20)
    private VaiTro vaiTro;

    @Enumerated(EnumType.STRING)
    @Column(name = "trangThai", nullable = false, length = 20)
    private TrangThaiTaiKhoan trangThai = TrangThaiTaiKhoan.HOAT_DONG;

    public TaiKhoan() {}

    public String getMaTK() { return maTK; }
    public void setMaTK(String maTK) { this.maTK = maTK; }
    public NhanVien getNhanVien() { return nhanVien; }
    public void setNhanVien(NhanVien nhanVien) { this.nhanVien = nhanVien; }
    public String getMatKhauHash() { return matKhauHash; }
    public void setMatKhauHash(String matKhauHash) { this.matKhauHash = matKhauHash; }
    public VaiTro getVaiTro() { return vaiTro; }
    public void setVaiTro(VaiTro vaiTro) { this.vaiTro = vaiTro; }
    public TrangThaiTaiKhoan getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiTaiKhoan trangThai) { this.trangThai = trangThai; }
}
