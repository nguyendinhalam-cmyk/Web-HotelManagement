<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Danh sách hóa đơn</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 1200px; margin: 30px auto; }
        table { width:100%; border-collapse:collapse; margin-top:20px; }
        th,td { border:1px solid #ddd; padding:8px; }
        th { background:#f5f5f5; }
        .success { padding:10px; background:#e7f7e7; margin:10px 0; }
        .error { padding:10px; background:#ffe5e5; margin:10px 0; }
        a { margin-right:8px; }
    </style>
</head>
<body>
<a href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
<h1>Danh sách hóa đơn</h1>
<a href="${pageContext.request.contextPath}/">Trang chủ</a>
<a href="${pageContext.request.contextPath}/dat-phong?action=list">Đặt phòng</a>

<c:if test="${not empty param.success}">
    <div class="success">Thao tác thành công.</div>
</c:if>
<c:if test="${not empty error}">
    <div class="error">${error}</div>
</c:if>

<table>
    <thead>
    <tr>
        <th>Mã hóa đơn</th>
        <th>Mã đặt phòng</th>
        <th>Ngày lập</th>
        <th>Tiền phòng</th>
        <th>Tiền dịch vụ</th>
        <th>Tổng tiền</th>
        <th>Trạng thái</th>
        <th>Thao tác</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="item" items="${items}">
        <tr>
            <td>${item.maHoaDon}</td>
            <td>${item.datPhong.maDatPhong}</td>
            <td>${item.ngayLap}</td>
            <td>${item.tongTienPhong}</td>
            <td>${item.tongTienDichVu}</td>
            <td><strong>${item.tongTien}</strong></td>
            <td>${item.trangThai}</td>
            <td>
                <a href="${pageContext.request.contextPath}/hoa-don?action=detail&id=${item.maHoaDon}">Chi tiết</a>
                <c:if test="${item.trangThai == 'NHAP'}">
                    <a href="${pageContext.request.contextPath}/hoa-don?action=issue&id=${item.maHoaDon}">Phát hành</a>
                    <a href="${pageContext.request.contextPath}/hoa-don?action=delete&id=${item.maHoaDon}"
                       onclick="return confirm('Xóa hóa đơn này?');">Xóa</a>
                </c:if>
                <c:if test="${item.trangThai != 'DA_HUY'}">
                    <a href="${pageContext.request.contextPath}/hoa-don?action=cancel&id=${item.maHoaDon}"
                       onclick="return confirm('Hủy hóa đơn này?');">Hủy</a>
                </c:if>
            </td>
        </tr>
    </c:forEach>
    <c:if test="${empty items}">
        <tr><td colspan="8">Chưa có dữ liệu.</td></tr>
    </c:if>
    </tbody>
</table>
</body>
</html>
