<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản lý khách sạn</title>
</head>
<body>
<a href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
<h1>Quản lý khách sạn</h1>
<p>Backend MVC đã sẵn sàng cho module đặt phòng.</p>
<ul>
    <li><a href="${pageContext.request.contextPath}/dat-phong?action=list">Danh sách đặt phòng</a></li>
    <li><a href="${pageContext.request.contextPath}/dat-phong?action=new">Tạo đặt phòng</a></li>
    <li><a href="${pageContext.request.contextPath}/khach-hang">Khách hàng</a></li>
</ul>
</body>
</html>

