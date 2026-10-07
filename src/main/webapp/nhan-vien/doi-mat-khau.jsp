<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html><html lang="vi"><head><meta charset="UTF-8"><title>Đổi mật khẩu</title></head><body>
<a href="${pageContext.request.contextPath}/logout">Đăng xuất</a><h2>Đổi mật khẩu</h2><p>${item.maNV} - ${item.hoTen}</p>
<form method="post" action="${pageContext.request.contextPath}/nhan-vien"><input type="hidden" name="action" value="password"><input type="hidden" name="maNV" value="${item.maNV}"><label>Mật khẩu mới <input type="password" name="matKhauMoi" required></label><button type="submit">Lưu</button></form><p><a href="${pageContext.request.contextPath}/nhan-vien?action=list">Quay lại</a></p></body></html>
