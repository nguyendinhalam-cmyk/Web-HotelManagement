<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%-- TV2 – Câu 11: chi tiết một đặt phòng (các phòng, ngày nhận/trả, giá đã chốt) --%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Chi tiết đặt phòng</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 1100px; margin: 30px auto; }
        table { width:100%; border-collapse:collapse; margin-top:20px; }
        th,td { border:1px solid #ddd; padding:8px; text-align:left; }
        a { margin-right:8px; }
    </style>
</head>
<body>
<h1>Chi tiết đặt phòng ${item.maDatPhong}</h1>
<a href="${pageContext.request.contextPath}/dat-phong?action=list">← Danh sách đặt phòng</a>

<table>
    <tr><th>Mã đặt phòng</th><td>${item.maDatPhong}</td></tr>
    <tr><th>Khách hàng</th><td>${item.khachHang.maKH} - ${item.khachHang.hoTen} - ${item.khachHang.soDienThoai}</td></tr>
    <tr><th>Ngày đặt</th><td>${item.ngayDat}</td></tr>
    <tr><th>Trạng thái</th><td>${item.trangThai}</td></tr>
    <tr><th>Ghi chú</th><td>${item.ghiChu}</td></tr>
</table>

<h3>Các phòng trong đặt phòng</h3>
<table>
    <thead>
    <tr>
        <th>Mã chi tiết</th><th>Phòng</th><th>Loại phòng</th>
        <th>Ngày nhận</th><th>Ngày trả</th><th>Giá đã chốt</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="ct" items="${item.chiTietDatPhong}">
        <tr>
            <td>${ct.maCTDatPhong}</td>
            <td>${ct.phong.soPhong}</td>
            <td>${ct.phong.loaiPhong.tenLoaiPhong}</td>
            <td>${ct.ngayNhan}</td>
            <td>${ct.ngayTra}</td>
            <td>${ct.giaPhong}</td>
        </tr>
    </c:forEach>
    <c:if test="${empty item.chiTietDatPhong}">
        <tr><td colspan="6">Đặt phòng chưa có chi tiết.</td></tr>
    </c:if>
    </tbody>
</table>
</body>
</html>
