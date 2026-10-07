<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html><html lang="vi"><head><meta charset="UTF-8"><title>Quản lý nhân viên</title>
<style>body{font-family:Arial,sans-serif;max-width:1250px;margin:30px auto}table{width:100%;border-collapse:collapse;margin-top:18px}th,td{border:1px solid #ddd;padding:8px}th{background:#f5f5f5}.success{padding:10px;background:#e7f7e7}.error{padding:10px;background:#ffe5e5}a{margin-right:8px}.locked{color:#b00020;font-weight:bold}.active{color:#087f23;font-weight:bold}.actions{line-height:2}</style></head>
<body>
<h1>Quản lý nhân viên & tài khoản</h1>
<p><a href="${pageContext.request.contextPath}/">Trang chủ</a> <a href="${pageContext.request.contextPath}/logout">Đăng xuất</a></p>
<c:if test="${not empty param.success}"><div class="success">Thao tác thành công.</div></c:if>
<c:if test="${not empty error}"><div class="error">${error}</div></c:if>
<p><a href="${pageContext.request.contextPath}/nhan-vien?action=new">+ Thêm nhân viên</a></p>
<table><thead><tr><th>Mã NV</th><th>Họ tên</th><th>SĐT</th><th>Email</th><th>Vai trò</th><th>Trạng thái TK</th><th>Thao tác</th></tr></thead><tbody>
<c:forEach var="item" items="${items}"><tr>
<td>${item.maNV}</td><td>${item.hoTen}</td><td>${item.soDienThoai}</td><td>${item.email}</td>
<td>${item.taiKhoan.vaiTro}</td>
<td><c:choose><c:when test="${item.taiKhoan.trangThai == 'HOAT_DONG'}"><span class="active">Hoạt động</span></c:when><c:otherwise><span class="locked">Đã khóa</span></c:otherwise></c:choose></td>
<td class="actions"><a href="${pageContext.request.contextPath}/nhan-vien?action=detail&id=${item.maNV}">Chi tiết</a><a href="${pageContext.request.contextPath}/nhan-vien?action=edit&id=${item.maNV}">Sửa</a><a href="${pageContext.request.contextPath}/nhan-vien?action=role&id=${item.maNV}">Đổi vai trò</a><a href="${pageContext.request.contextPath}/nhan-vien?action=password&id=${item.maNV}">Đổi mật khẩu</a><a href="${pageContext.request.contextPath}/nhan-vien?action=toggle&id=${item.maNV}" onclick="return confirm('Thay đổi trạng thái tài khoản?')">${item.taiKhoan.trangThai == 'HOAT_DONG' ? 'Khóa' : 'Mở khóa'}</a></td>
</tr></c:forEach><c:if test="${empty items}"><tr><td colspan="7">Chưa có dữ liệu.</td></tr></c:if></tbody></table>
</body></html>
