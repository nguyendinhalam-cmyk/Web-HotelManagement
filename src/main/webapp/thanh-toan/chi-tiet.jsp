<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head><meta charset="UTF-8"><title>Chi tiết thanh toán</title></head>
<body>
<a href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
<h1>Chi tiết thanh toán</h1>
<c:if test="${not empty error}"><p style="color:#b00020">${error}</p></c:if>
<c:if test="${not empty item}">
<table border="1" cellpadding="8" cellspacing="0">
    <tr><th>Mã thanh toán</th><td>${item.maThanhToan}</td></tr>
    <tr><th>Mã đặt phòng</th><td>${item.datPhong.maDatPhong}</td></tr>
    <tr><th>Số tiền</th><td>${item.soTien}</td></tr>
    <tr><th>Phương thức</th><td>${item.phuongThuc}</td></tr>
    <tr><th>Trạng thái</th><td>${item.trangThai}</td></tr>
    <tr><th>Mã giao dịch</th><td>${item.maGiaoDich}</td></tr>
    <tr><th>Thời gian tạo</th><td>${item.thoiGianTao}</td></tr>
    <tr><th>Thời gian thanh toán</th><td>${item.thoiGianThanhToan}</td></tr>
</table>
<br>
<c:if test="${item.trangThai == 'CHO_THANH_TOAN'}">
    <a href="${pageContext.request.contextPath}/thanh-toan?action=success&id=${item.maThanhToan}">✓ Xác nhận thanh toán</a>
    <a href="${pageContext.request.contextPath}/thanh-toan?action=fail&id=${item.maThanhToan}">✕ Đánh dấu thất bại</a>
</c:if>
<c:if test="${item.trangThai == 'DA_THANH_TOAN'}">
    <a href="${pageContext.request.contextPath}/thanh-toan?action=refund&id=${item.maThanhToan}">Hoàn tiền</a>
</c:if>
</c:if>
<p><a href="${pageContext.request.contextPath}/thanh-toan?action=list">← Danh sách thanh toán</a></p>
</body>
</html>
