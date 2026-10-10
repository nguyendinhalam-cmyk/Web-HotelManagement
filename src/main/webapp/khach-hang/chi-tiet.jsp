```jsp
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi tiết khách hàng</title>

    <style>
        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            padding: 30px 15px;
            font-family: Arial, sans-serif;
            background: #f4f6f9;
            color: #263238;
        }

        .container {
            max-width: 1000px;
            margin: auto;
        }

        .card {
            margin-bottom: 22px;
            padding: 25px;
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 8px #0000000d;
        }

        h1 {
            margin-top: 0;
            font-size: 26px;
        }

        h2 {
            margin-top: 0;
            font-size: 20px;
        }

        .info-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
        }

        .info-item {
            padding-bottom: 12px;
            border-bottom: 1px solid #edf0f2;
        }

        .label {
            display: block;
            margin-bottom: 6px;
            color: #718096;
            font-size: 13px;
        }

        .value {
            font-size: 15px;
            font-weight: bold;
            overflow-wrap: anywhere;
        }

        .table-wrapper {
            overflow-x: auto;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th, td {
            padding: 13px 10px;
            text-align: left;
            border-bottom: 1px solid #e5e9ed;
        }

        th {
            background: #eef3f8;
            white-space: nowrap;
        }

        .empty {
            padding: 25px;
            text-align: center;
            color: #75818c;
        }

        .actions {
            display: flex;
            gap: 10px;
            margin-top: 20px;
        }

        .btn {
            display: inline-block;
            padding: 10px 16px;
            border-radius: 5px;
            text-decoration: none;
            font-size: 14px;
        }

        .btn-primary {
            background: #1976d2;
            color: white;
        }

        .btn-secondary {
            background: #607d8b;
            color: white;
        }

        @media (max-width: 600px) {
            .info-grid {
                grid-template-columns: 1fr;
            }

            body {
                padding: 15px;
            }
        }
    </style>
</head>

<body>
<div class="container">

    <div class="card">
        <h1>Chi tiết khách hàng</h1>

        <div class="info-grid">

            <div class="info-item">
                <span class="label">Mã khách hàng</span>
                <span class="value"><c:out value="${kh.maKH}"/></span>
            </div>

            <div class="info-item">
                <span class="label">Họ và tên</span>
                <span class="value"><c:out value="${kh.hoTen}"/></span>
            </div>

            <div class="info-item">
                <span class="label">CCCD</span>
                <span class="value"><c:out value="${kh.cccd}"/></span>
            </div>

            <div class="info-item">
                <span class="label">Ngày sinh</span>
                <span class="value"><c:out value="${kh.ngaySinh}"/></span>
            </div>

            <div class="info-item">
                <span class="label">Giới tính</span>
                <span class="value"><c:out value="${kh.gioiTinh}"/></span>
            </div>

            <div class="info-item">
                <span class="label">Số điện thoại</span>
                <span class="value"><c:out value="${kh.soDienThoai}"/></span>
            </div>

            <div class="info-item">
                <span class="label">Email</span>
                <span class="value"><c:out value="${kh.email}"/></span>
            </div>

            <div class="info-item">
                <span class="label">Địa chỉ</span>
                <span class="value"><c:out value="${kh.diaChi}"/></span>
            </div>

        </div>

        <div class="actions">
            <a class="btn btn-primary"
               href="${pageContext.request.contextPath}/khach-hang?action=edit&id=${kh.maKH}">
                Chỉnh sửa
            </a>

            <a class="btn btn-secondary"
               href="${pageContext.request.contextPath}/khach-hang?action=list">
                Quay lại danh sách
            </a>
        </div>
    </div>

    <div class="card">
        <h2>Lịch sử đặt phòng và lưu trú</h2>

        <div class="table-wrapper">
            <table>
                <thead>
                <tr>
                    <th>Mã đặt phòng</th>
                    <th>Phòng</th>
                    <th>Ngày nhận phòng</th>
                    <th>Ngày trả phòng</th>
                    <th>Trạng thái</th>
                </tr>
                </thead>

                <tbody>
                <c:choose>
                    <c:when test="${not empty kh.danhSachDatPhong}">
                        <c:forEach var="dp" items="${kh.danhSachDatPhong}">
                            <tr>
                                <td><c:out value="${dp.maDatPhong}"/></td>
                                <td><c:out value="${dp.phong.soPhong}"/></td>
                                <td><c:out value="${dp.ngayNhanPhong}"/></td>
                                <td><c:out value="${dp.ngayTraPhong}"/></td>
                                <td><c:out value="${dp.trangThai}"/></td>
                            </tr>
                        </c:forEach>
                    </c:when>

                    <c:otherwise>
                        <tr>
                            <td colspan="5" class="empty">
                                Khách hàng chưa có lịch sử đặt phòng hoặc lưu trú.
                            </td>
                        </tr>
                    </c:otherwise>
                </c:choose>
                </tbody>
            </table>
        </div>
    </div>

</div>
</body>
</html>
```
