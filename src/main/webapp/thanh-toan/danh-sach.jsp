<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Danh sách thanh toán</title>
    <style>
        body{font-family:Arial,sans-serif;margin:30px} table{border-collapse:collapse;width:100%}
        th,td{border:1px solid #ddd;padding:10px;text-align:left} th{background:#f5f5f5}
        a,button{margin-right:8px}.success{color:green}.error{color:#b00020}
    </style>
</head>
<body>
<h1>Danh sách thanh toán</h1>
<p><a href="${pageContext.request.contextPath}/thanh-toan?action=new">+ Tạo thanh toán</a></p>
<c:if test="${not empty param.success}"><p class="success">Thao tác thành công.</p></c:if>
<c:if test="${not empty error}"><p class="error">${error}</p></c:if>
<table>
    <thead><tr>
        <th>Mã thanh toán</th><th>Mã đặt phòng</th><th>Số tiền</th>
        <th>Phương thức</th><th>Trạng thái</th><th>Mã giao dịch</th>
        <th>Thời gian tạo</th><th>Thời gian thanh toán</th><th>Thao tác</th>
    </tr></thead>
    <tbody>
    <c:choose>
        <c:when test="${empty items}"><tr><td colspan="9">Chưa có thanh toán.</td></tr></c:when>
        <c:otherwise>
            <c:forEach var="item" items="${items}">
                <tr>
                    <td>${item.maThanhToan}</td>
                    <td>${item.datPhong.maDatPhong}</td>
                    <td>${item.soTien}</td>
                    <td>${item.phuongThuc}</td>
                    <td>${item.trangThai}</td>
                    <td>${item.maGiaoDich}</td>
                    <td>${item.thoiGianTao}</td>
                    <td>${item.thoiGianThanhToan}</td>
                    <td>
                        <a href="${pageContext.request.contextPath}/thanh-toan?action=detail&id=${item.maThanhToan}">Chi tiết</a>
                        <c:if test="${item.trangThai == 'CHO_THANH_TOAN'}">
                            <a href="${pageContext.request.contextPath}/thanh-toan?action=success&id=${item.maThanhToan}">Xác nhận</a>
                            <a href="${pageContext.request.contextPath}/thanh-toan?action=fail&id=${item.maThanhToan}">Thất bại</a>
                        </c:if>
                        <c:if test="${item.trangThai == 'DA_THANH_TOAN'}">
                            <a href="${pageContext.request.contextPath}/thanh-toan?action=refund&id=${item.maThanhToan}">Hoàn tiền</a>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
        </c:otherwise>
    </c:choose>
    </tbody>
</table>
<p><a href="${pageContext.request.contextPath}/hoa-don?action=list">← Hóa đơn</a></p>
</body>
</html>
