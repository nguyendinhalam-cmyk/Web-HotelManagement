<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html><html lang="vi"><head><meta charset="UTF-8"><title>Chi tiết nhân viên</title></head><body>
<a href="${pageContext.request.contextPath}/logout">Đăng xuất</a><h2>Chi tiết nhân viên</h2>
<p>Mã NV: <b>${item.maNV}</b></p><p>Họ tên: ${item.hoTen}</p><p>Ngày sinh: ${item.ngaySinh}</p><p>Giới tính: ${item.gioiTinh}</p><p>SĐT: ${item.soDienThoai}</p><p>Email: ${item.email}</p><p>Địa chỉ: ${item.diaChi}</p><p>Vai trò: <b>${taiKhoan.vaiTro}</b></p><p>Trạng thái: <b>${taiKhoan.trangThai}</b></p><p><a href="${pageContext.request.contextPath}/nhan-vien?action=list">Quay lại</a></p></body></html>
