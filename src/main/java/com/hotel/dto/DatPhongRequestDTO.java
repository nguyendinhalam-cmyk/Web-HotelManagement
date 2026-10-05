package com.hotel.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Dữ liệu form tạo đặt phòng, không chứa Entity. */
public class DatPhongRequestDTO {
    private String maKH;
    private String ghiChu;
    private final List<ChiTiet> chiTiet = new ArrayList<>();

    public String getMaKH() { return maKH; }
    public void setMaKH(String maKH) { this.maKH = maKH; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    public List<ChiTiet> getChiTiet() { return chiTiet; }

    public void addChiTiet(String maPhong, LocalDate ngayNhan, LocalDate ngayTra) {
        chiTiet.add(new ChiTiet(maPhong, ngayNhan, ngayTra));
    }

    public static class ChiTiet {
        private final String maPhong;
        private final LocalDate ngayNhan;
        private final LocalDate ngayTra;

        public ChiTiet(String maPhong, LocalDate ngayNhan, LocalDate ngayTra) {
            this.maPhong = maPhong;
            this.ngayNhan = ngayNhan;
            this.ngayTra = ngayTra;
        }
        public String getMaPhong() { return maPhong; }
        public LocalDate getNgayNhan() { return ngayNhan; }
        public LocalDate getNgayTra() { return ngayTra; }
    }
}
