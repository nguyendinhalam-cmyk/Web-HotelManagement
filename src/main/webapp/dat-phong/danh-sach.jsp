<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Danh sách đặt phòng</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 1200px; margin: 30px auto; }
        table { width:100%; border-collapse:collapse; margin-top:20px; }
        th,td { border:1px solid #ddd; padding:8px; }
        .success { padding:10px; background:#e7f7e7; }
        .error { padding:10px; background:#ffe5e5; }
        a { margin-right:8px; }
    </style>
</head>
<body>
<h1>Danh sách đặt phòng</h1>
<a href="${pageContext.request.contextPath}/">Trang chủ</a>
<a href="${pageContext.request.contextPath}/dat-phong?action=new">+ Tạo đặt phòng</a>

<c:if test="${not empty success}"><div class="success">Thao tác thành công.</div></c:if>
<c:if test="${not empty error}"><div class="error">${error}</div></c:if>

<%-- TV2 – Câu 11: tra cứu. Để trống tất cả = danh sách đầy đủ như trước --%>
<form method="get" action="${pageContext.request.contextPath}/dat-phong" style="margin-top:15px;">
    <input type="hidden" name="action" value="list">
    <input type="text" name="maDatPhong" value="${param.maDatPhong}" placeholder="Mã đặt phòng">
    <input type="text" name="khachHang" value="${param.khachHang}" placeholder="Tên khách hoặc SĐT">
    <select name="trangThai">
        <option value="">-- Mọi trạng thái --</option>
        <c:forEach var="tt" items="${trangThais}">
            <option value="${tt}" ${tt == param.trangThai ? 'selected' : ''}>${tt}</option>
        </c:forEach>
    </select>
    Ngày nhận từ <input type="date" name="tuNgay" value="${param.tuNgay}">
    đến <input type="date" name="denNgay" value="${param.denNgay}">
    <button type="submit">Tra cứu</button>
    <a href="${pageContext.request.contextPath}/dat-phong?action=list">Bỏ lọc</a>
</form>

<table>
    <thead>
    <tr>
        <th>Mã đặt phòng</th><th>Khách hàng</th><th>Ngày đặt</th><th>Trạng thái</th><th>Ghi chú</th><th>Thao tác</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="item" items="${items}">
        <tr>
            <td>${item.maDatPhong}</td>
            <td>${item.maKH} - ${item.tenKhachHang}</td>
            <td>${item.ngayDat}</td>
            <td>${item.trangThai}</td>
            <td>${item.ghiChu}</td>
            <td>
                <a href="${pageContext.request.contextPath}/dat-phong?action=detail&ma=${item.maDatPhong}">Chi tiết</a>
                <c:if test="${item.trangThai == 'CHO_XAC_NHAN'}">
                    <a href="${pageContext.request.contextPath}/dat-phong?action=confirm&ma=${item.maDatPhong}">Xác nhận</a>
                    <a href="${pageContext.request.contextPath}/dat-phong?action=cancel&ma=${item.maDatPhong}">Hủy</a>
                </c:if>
                <c:if test="${item.trangThai == 'DA_XAC_NHAN'}">
                    <a href="${pageContext.request.contextPath}/dat-phong?action=checkin&ma=${item.maDatPhong}">Check-in</a>
                    <a href="${pageContext.request.contextPath}/dat-phong?action=cancel&ma=${item.maDatPhong}">Hủy</a>
                </c:if>
                <c:if test="${item.trangThai == 'DANG_O'}">
                    <a href="${pageContext.request.contextPath}/dat-phong?action=checkout&ma=${item.maDatPhong}">Check-out</a>
                </c:if>
                <c:if test="${item.trangThai == 'DA_TRA_PHONG'}">
                    <a href="${pageContext.request.contextPath}/hoa-don?action=by-dat-phong&maDatPhong=${item.maDatPhong}">Hóa đơn</a>
                </c:if>
            </td>
        </tr>
    </c:forEach>
    <c:if test="${empty items}">
        <tr><td colspan="6">Chưa có dữ liệu.</td></tr>
    </c:if>
    </tbody>
</table>
</body>
</html>
