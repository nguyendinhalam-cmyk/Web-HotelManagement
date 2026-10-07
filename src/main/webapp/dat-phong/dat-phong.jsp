<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Tạo đặt phòng</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 1100px; margin: 30px auto; }
        label { display:block; margin-top:12px; font-weight:bold; }
        input, select, textarea { padding:7px; min-width:250px; }
        table { width:100%; border-collapse:collapse; margin-top:20px; }
        th,td { border:1px solid #ddd; padding:8px; }
        .error { padding:10px; background:#ffe5e5; margin:10px 0; }
        .info { padding:10px; background:#eef6ff; margin:10px 0; }
    </style>
</head>
<body>
<h1>Tạo đặt phòng</h1>
<a href="${pageContext.request.contextPath}/dat-phong?action=list">← Danh sách đặt phòng</a>

<c:if test="${not empty error}">
    <div class="error">${error}</div>
</c:if>

<form method="get" action="${pageContext.request.contextPath}/dat-phong">
    <input type="hidden" name="action" value="available">
    <label>Ngày nhận</label>
    <input type="date" name="ngayNhan" value="${ngayNhan}" required>
    <label>Ngày trả</label>
    <input type="date" name="ngayTra" value="${ngayTra}" required>

    <%-- TV2 – Câu 2: lọc loại phòng, giá (để trống = không lọc).
         Không lọc theo tình trạng phòng hiện tại vì danh sách đã là phòng trống theo ngày. --%>
    <label>Loại phòng</label>
    <select name="maLoaiPhong">
        <option value="">-- Tất cả --</option>
        <c:forEach var="lp" items="${loaiPhongs}">
            <option value="${lp.maLoaiPhong}" ${lp.maLoaiPhong == param.maLoaiPhong ? 'selected' : ''}>${lp.tenLoaiPhong}</option>
        </c:forEach>
    </select>
    <label>Giá từ</label>
    <input type="number" name="giaTu" min="0" step="100000" value="${param.giaTu}">
    <label>Giá đến</label>
    <input type="number" name="giaDen" min="0" step="100000" value="${param.giaDen}">
    <br><br>
    <button type="submit">Kiểm tra phòng trống</button>
</form>

<%-- Đã bấm kiểm tra (có ngày, không lỗi) nhưng không còn phòng phù hợp --%>
<c:if test="${not empty ngayNhan and not empty ngayTra and empty error and empty phongOptions}">
    <div class="info">Không còn phòng trống phù hợp trong khoảng ngày đã chọn.</div>
</c:if>

<c:if test="${not empty phongOptions}">
    <div class="info">Chọn phòng cần đặt. Có thể chọn nhiều phòng.</div>
    <form method="post" action="${pageContext.request.contextPath}/dat-phong">
        <label>Khách hàng</label>
        <select name="maKH" required>
            <option value="">-- Chọn khách hàng --</option>
            <c:forEach var="kh" items="${khachHangs}">
                <option value="${kh.maKH}">${kh.maKH} - ${kh.hoTen} - ${kh.soDienThoai}</option>
            </c:forEach>
        </select>

        <input type="hidden" name="ngayNhan" value="${ngayNhan}">
        <input type="hidden" name="ngayTra" value="${ngayTra}">
        <%-- Giữ bộ lọc để nếu lưu lỗi, danh sách phòng hiển thị lại đúng như trước --%>
        <input type="hidden" name="maLoaiPhong" value="${param.maLoaiPhong}">
        <input type="hidden" name="giaTu" value="${param.giaTu}">
        <input type="hidden" name="giaDen" value="${param.giaDen}">

        <%-- TV2 – Câu 7: chỉ để kiểm tra sức chứa, không lưu vào DB --%>
        <label>Số lượng khách</label>
        <input type="number" name="soLuongKhach" min="1" value="${param.soLuongKhach}" required>

        <table>
            <thead>
            <tr>
                <th>Chọn</th><th>Phòng</th><th>Loại</th><th>Sức chứa</th><th>Giá cơ bản</th>
                <th>Ngày nhận</th><th>Ngày trả</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="p" items="${phongOptions}">
                <tr>
                    <td><input type="checkbox" name="maPhong" value="${p.maPhong}"></td>
                    <td>${p.soPhong}</td>
                    <td>${p.tenLoaiPhong}</td>
                    <td>${p.soNguoiToiDa} người</td>
                    <td>${p.giaCoBan}</td>
                    <td>${ngayNhan}</td>
                    <td>${ngayTra}</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>

        <label>Ghi chú</label>
        <textarea name="ghiChu" rows="3" maxlength="500" style="width:100%;"></textarea>
        <br><br>
        <button type="submit">Tạo đặt phòng</button>
    </form>
</c:if>
</body>
</html>



