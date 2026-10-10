<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Quản lý khách hàng</title>

    <style>
        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            padding: 30px;
            font-family: Arial, sans-serif;
            background: #f4f6f9;
            color: #243047;
        }

        .container {
            max-width: 1500px;
            margin: auto;
            background: white;
            padding: 28px;
            border-radius: 12px;
            box-shadow: 0 3px 14px #0000000d;
        }

        h1 {
            margin-top: 20px;
        }

        .back {
            color: #2563eb;
            text-decoration: none;
        }

        .toolbar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            gap: 12px;
            flex-wrap: wrap;
            margin: 24px 0;
        }

        .search {
            display: flex;
            gap: 8px;
            flex: 1;
            min-width: 280px;
        }

        input {
            padding: 11px;
            border: 1px solid #d4dbe5;
            border-radius: 6px;
        }

        .search input {
            flex: 1;
            min-width: 100px;
        }

        .btn {
            display: inline-block;
            padding: 10px 14px;
            border: none;
            border-radius: 6px;
            text-decoration: none;
            cursor: pointer;
            font-size: 14px;
            white-space: nowrap;
        }

        .primary {
            background: #2563eb;
            color: white;
        }

        .edit {
            background: #f59e0b;
            color: #111827;
        }

        .delete {
            background: #dc2626;
            color: white;
        }

        .table-wrap {
            overflow-x: auto;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            white-space: nowrap;
        }

        th, td {
            padding: 12px 10px;
            border-bottom: 1px solid #e5e7eb;
            text-align: left;
        }

        th {
            background: #eef2ff;
        }

        .actions {
            display: flex;
            gap: 6px;
        }

        .message {
            padding: 12px;
            background: #dcfce7;
            color: #166534;
            border-radius: 6px;
            margin: 12px 0;
        }

        .error {
            padding: 12px;
            background: #fee2e2;
            color: #991b1b;
            border-radius: 6px;
            margin: 12px 0;
        }
    </style>
</head>

<body>
<div class="container">

    <a class="back" href="${pageContext.request.contextPath}/">
        ← Về trang chủ
    </a>

    <h1>Quản lý khách hàng</h1>
    <p>Thêm, sửa, xóa và tìm kiếm thông tin khách hàng.</p>

    <c:if test="${param.success == 'saved'}">
        <div class="message">Lưu thông tin khách hàng thành công.</div>
    </c:if>

    <c:if test="${param.success == 'deleted'}">
        <div class="message">Xóa khách hàng thành công.</div>
    </c:if>

    <c:if test="${not empty error}">
        <div class="error">
            <c:out value="${error}"/>
        </div>
    </c:if>

    <div class="toolbar">

        <form class="search"
              method="get"
              action="${pageContext.request.contextPath}/khach-hang">

            <input type="hidden" name="action" value="list">

            <input type="text"
                   name="keyword"
                   value="<c:out value='${keyword}'/>"
                   placeholder="Nhập mã, họ tên hoặc số điện thoại">

            <button class="btn primary" type="submit">
                Tìm kiếm
            </button>
        </form>

        <a class="btn primary"
           href="${pageContext.request.contextPath}/khach-hang?action=new">
            + Thêm khách hàng
        </a>

    </div>

    <div class="table-wrap">
        <table>
            <thead>
            <tr>
                <th>Mã KH</th>
                <th>Họ tên</th>
                <th>CCCD</th>
                <th>Ngày sinh</th>
                <th>Giới tính</th>
                <th>Số điện thoại</th>
                <th>Email</th>
                <th>Địa chỉ</th>
                <th>Thao tác</th>
            </tr>
            </thead>

            <tbody>
            <c:forEach var="kh" items="${items}">
                <tr>
                    <td><c:out value="${kh.maKH}"/></td>
                    <td><c:out value="${kh.hoTen}"/></td>
                    <td><c:out value="${kh.cccd}"/></td>
                    <td><c:out value="${kh.ngaySinh}"/></td>
                    <td><c:out value="${kh.gioiTinh}"/></td>
                    <td><c:out value="${kh.soDienThoai}"/></td>
                    <td><c:out value="${kh.email}"/></td>
                    <td><c:out value="${kh.diaChi}"/></td>

                    <td>
                        <div class="actions">
                            <a class="btn edit"
                               href="${pageContext.request.contextPath}/khach-hang?action=edit&id=${kh.maKH}">
                                Sửa
                            </a>

                            <form method="post"
                                  action="${pageContext.request.contextPath}/khach-hang"
                                  onsubmit="return confirm('Bạn có chắc muốn xóa khách hàng này?');"
                                  style="display: inline;">

                                <input type="hidden" name="action" value="delete">

                                <input type="hidden"
                                       name="id"
                                       value="<c:out value='${kh.maKH}'/>">

                                <button type="submit" class="btn delete">
                                    Xóa
                                </button>
                            </form>
                        </div>
                    </td>
                </tr>
            </c:forEach>

            <c:if test="${empty items}">
                <tr>
                    <td colspan="9" style="text-align: center;">
                        Không tìm thấy khách hàng.
                    </td>
                </tr>
            </c:if>
            </tbody>
        </table>
    </div>

    <p>
        Số khách hàng hiển thị:
        <c:out value="${items.size()}"/>
    </p>

</div>
</body>
</html>
