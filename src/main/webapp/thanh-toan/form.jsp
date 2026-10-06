<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head><meta charset="UTF-8"><title>Tạo thanh toán</title></head>
<body>
<h1>Tạo thanh toán</h1>
<c:if test="${not empty error}"><p style="color:#b00020">${error}</p></c:if>
<form method="post" action="${pageContext.request.contextPath}/thanh-toan">
    <div>
        <label>Mã thanh toán (để trống để tự sinh):</label>
        <input type="text" name="maThanhToan">
    </div><br>
    <div>
        <label>Đặt phòng:</label>
        <select name="maDatPhong" required>
            <option value="">-- Chọn đặt phòng --</option>
            <c:forEach var="dp" items="${datPhongs}">
                <option value="${dp.maDatPhong}">${dp.maDatPhong} - ${dp.khachHang.hoTen} - ${dp.trangThai}</option>
            </c:forEach>
        </select>
    </div><br>
    <div>
        <label>Số tiền:</label>
        <input type="number" name="soTien" min="0.01" step="0.01" required>
    </div><br>
    <div>
        <label>Phương thức:</label>
        <select name="phuongThuc" required>
            <c:forEach var="p" items="${phuongThucs}">
                <option value="${p}">${p}</option>
            </c:forEach>
        </select>
    </div><br>
    <button type="submit">Tạo thanh toán</button>
    <a href="${pageContext.request.contextPath}/thanh-toan?action=list">Hủy</a>
</form>
</body>
</html>
