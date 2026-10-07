<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi"><head><meta charset="UTF-8"><title>Đăng nhập nhân viên</title>
<style>body{font-family:Arial,sans-serif;background:#f4f6f8}.box{width:380px;margin:90px auto;background:#fff;padding:28px;border-radius:8px;box-shadow:0 2px 12px #ddd}label{display:block;margin-top:14px}input{width:100%;box-sizing:border-box;padding:10px;margin-top:6px}.btn{margin-top:20px;width:100%;padding:10px}.error{margin:10px 0;padding:10px;background:#ffe5e5}</style></head>
<body><div class="box"><h2>Đăng nhập nhân viên</h2>
<c:if test="${not empty error}"><div class="error">${error}</div></c:if>
<form method="post" action="${pageContext.request.contextPath}/login">
<label>Mã nhân viên</label><input name="maNV" value="${maNV}" required autofocus>
<label>Mật khẩu</label><input type="password" name="matKhau" required>
<button class="btn" type="submit">Đăng nhập</button>
</form></div></body></html>
