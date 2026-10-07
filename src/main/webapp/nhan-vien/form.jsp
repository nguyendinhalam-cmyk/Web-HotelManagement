<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html><html lang="vi"><head><meta charset="UTF-8"><title>${mode == 'new' ? 'Thêm nhân viên' : 'Sửa nhân viên'}</title><style>body{font-family:Arial;max-width:760px;margin:30px auto}label{display:block;margin-top:12px}input,select{width:100%;padding:9px;box-sizing:border-box;margin-top:5px}.btn{margin-top:20px;padding:10px 18px}.error{padding:10px;background:#ffe5e5}</style></head><body>
<a href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
<h1>${mode == 'new' ? 'Tạo nhân viên và tài khoản' : 'Cập nhật thông tin nhân viên'}</h1><a href="${pageContext.request.contextPath}/nhan-vien?action=list">← Danh sách</a>
<c:if test="${not empty error}"><div class="error">${error}</div></c:if>
<form method="post" action="${pageContext.request.contextPath}/nhan-vien">
<input type="hidden" name="action" value="save"><c:if test="${mode == 'edit'}"><input type="hidden" name="maNV" value="${item.maNV}"></c:if>
<label>Họ tên <input name="hoTen" value="${item.hoTen}" required></label>
<label>Ngày sinh <input type="date" name="ngaySinh" value="${item.ngaySinh}"></label>
<label>Giới tính <select name="gioiTinh"><option value="">-- Chọn --</option><c:forEach var="g" items="${gioiTinhs}"><option value="${g}" ${item.gioiTinh == g ? 'selected' : ''}>${g}</option></c:forEach></select></label>
<label>Số điện thoại <input name="soDienThoai" value="${item.soDienThoai}" required></label>
<label>Email <input type="email" name="email" value="${item.email}" required></label>
<label>Địa chỉ <input name="diaChi" value="${item.diaChi}"></label>
<c:if test="${mode == 'new'}">
<label>Vai trò <select name="vaiTro" required><option value="">-- Chọn vai trò --</option><c:forEach var="v" items="${vaiTros}"><option value="${v}">${v}</option></c:forEach></select></label>
<label>Mật khẩu ban đầu <input type="password" name="matKhauBanDau" required></label>
<label>Xác nhận mật khẩu <input type="password" name="xacNhanMatKhau" required></label>
</c:if>
<button class="btn" type="submit">Lưu</button></form></body></html>
