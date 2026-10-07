package com.hotel.dto;

import java.math.BigDecimal;

/** DTO dùng cho danh sách phòng có thể đặt. */
public class PhongOptionDTO {
    private final String maPhong;
    private final String soPhong;
    private final String maLoaiPhong;
    private final String tenLoaiPhong;
    private final BigDecimal giaCoBan;

    public PhongOptionDTO(String maPhong, String soPhong, String maLoaiPhong,
                          String tenLoaiPhong, BigDecimal giaCoBan) {
        this.maPhong = maPhong;
        this.soPhong = soPhong;
        this.maLoaiPhong = maLoaiPhong;
        this.tenLoaiPhong = tenLoaiPhong;
        this.giaCoBan = giaCoBan;
    }
    public String getMaPhong() { return maPhong; }
    public String getSoPhong() { return soPhong; }
    public String getMaLoaiPhong() { return maLoaiPhong; }
    public String getTenLoaiPhong() { return tenLoaiPhong; }
    public BigDecimal getGiaCoBan() { return giaCoBan; }
}
