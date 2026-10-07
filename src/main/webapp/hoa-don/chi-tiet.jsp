<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Chi tiết hóa đơn</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 1000px; margin: 30px auto; }
        .info { display:grid; grid-template-columns:180px 1fr; gap:8px; margin:20px 0; }
        table { width:100%; border-collapse:collapse; margin-top:20px; }
        th,td { border:1px solid #ddd; padding:8px; }
        th { background:#f5f5f5; }
        .total { text-align:right; font-size:20px; margin-top:20px; }
        .success { padding:10px; background:#e7f7e7; margin:10px 0; }
        .error { padding:10px; background:#ffe5e5; margin:10px 0; }
        a { margin-right:8px; }
    </style>
</head>
<body>
<a href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
<h1>Chi tiết hóa đơn</h1>
<a href="${pageContext.request.contextPath}/hoa-don?action=list">← Danh sách hóa đơn</a>
<a href="${pageContext.request.contextPath}/dat-phong?action=list">Đặt phòng</a>

<c:if test="${not empty param.success}">
    <div class="success">Checkout và lập hóa đơn thành công.</div>
</c:if>
<c:if test="${not empty error}">
    <div class="error">${error}</div>
</c:if>

<div class="info">
    <strong>Mã hóa đơn:</strong><span>${item.maHoaDon}</span>
    <strong>Mã đặt phòng:</strong><span>${item.datPhong.maDatPhong}</span>
    <strong>Khách hàng:</strong><span>${item.datPhong.khachHang.hoTen}</span>
    <strong>Ngày lập:</strong><span>${item.ngayLap}</span>
    <strong>Trạng thái:</strong><span>${item.trangThai}</span>
</div>

<table>
    <thead>
    <tr>
        <th>Khoản thu</th>
        <th>Số lượng</th>
        <th>Đơn giá</th>
        <th>Thành tiền</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="ct" items="${item.chiTietHoaDon}">
        <tr>
            <td>${ct.tenKhoanThu}</td>
            <td>${ct.soLuong}</td>
            <td>${ct.donGia}</td>
            <td>${ct.thanhTien}</td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<div class="total">
    Tiền phòng: <strong>${item.tongTienPhong}</strong><br>
    Tiền dịch vụ: <strong>${item.tongTienDichVu}</strong><br>
    Tổng cộng: <strong>${item.tongTien}</strong>
</div>

<p>
    <c:if test="${item.trangThai == 'NHAP'}">
        <a href="${pageContext.request.contextPath}/hoa-don?action=issue&id=${item.maHoaDon}">Phát hành hóa đơn</a>
    </c:if>
    <a href="${pageContext.request.contextPath}/thanh-toan?action=new&maDatPhong=${item.datPhong.maDatPhong}&soTien=${item.tongTien}">Tạo thanh toán</a>
</p>
</body>
</html>
