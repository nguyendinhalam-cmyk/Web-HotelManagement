<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thông tin khách hàng</title>

    <style>
        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            padding: 30px 15px;
            font-family: Arial, sans-serif;
            background: #f3f4f6;
            color: #1f2937;
        }

        .form-box {
            max-width: 650px;
            margin: 0 auto;
            padding: 28px;
            background: white;
            border-radius: 10px;
            box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
        }

        h1 {
            margin: 0 0 24px;
            font-size: 26px;
            color: #1e3a8a;
        }

        label {
            display: block;
            margin: 15px 0 6px;
            font-weight: bold;
        }

        input,
        select {
            display: block;
            width: 100%;
            padding: 10px 12px;
            border: 1px solid #d1d5db;
            border-radius: 6px;
            background: white;
            font-size: 15px;
        }

        input:focus,
        select:focus {
            outline: none;
            border-color: #2563eb;
        }

        input[readonly] {
            background: #f3f4f6;
            color: #6b7280;
        }

        .buttons {
            display: flex;
            gap: 10px;
            margin-top: 24px;
        }

        button,
        .back {
            display: inline-block;
            padding: 11px 16px;
            border: none;
            border-radius: 6px;
            font-size: 14px;
            text-decoration: none;
            cursor: pointer;
        }

        button {
            background: #2563eb;
            color: white;
        }

        .back {
            background: #e5e7eb;
            color: #111827;
        }

        button:hover,
        .back:hover {
            opacity: 0.85;
        }

        .error {
            padding: 12px;
            margin-bottom: 15px;
            background: #fee2e2;
            color: #991b1b;
            border-radius: 6px;
        }

        @media (max-width: 480px) {
            .form-box {
                padding: 18px;
            }

            h1 {
                font-size: 22px;
            }
        }
    </style>
</head>

<body>
<div class="form-box">

    <h1>
        <c:choose>
            <c:when test="${not empty item}">
                Sửa thông tin khách hàng
            </c:when>
            <c:otherwise>
                Thêm khách hàng
            </c:otherwise>
        </c:choose>
    </h1>

    <c:if test="${not empty error}">
        <div class="error">
            <c:out value="${error}"/>
        </div>
    </c:if>

    <form method="post"
          accept-charset="UTF-8"
          action="${pageContext.request.contextPath}/khach-hang">

        <label for="maKH">Mã khách hàng</label>
        <input type="text"
               id="maKH"
               name="maKH"
               value="<c:out value='${item.maKH}'/>"
               placeholder="Để trống nếu thêm mới"
               readonly>

        <label for="hoTen">Họ và tên *</label>
        <input type="text"
               id="hoTen"
               name="hoTen"
               value="<c:out value='${item.hoTen}'/>"
               maxlength="100"
               required>

        <label for="cccd">Số CCCD *</label>
        <input type="text"
               id="cccd"
               name="cccd"
               value="<c:out value='${item.cccd}'/>"
               maxlength="12"
               pattern="[0-9]{12}"
               title="CCCD phải gồm đúng 12 chữ số"
               required>

        <label for="ngaySinh">Ngày sinh</label>
        <input type="date"
               id="ngaySinh"
               name="ngaySinh"
               value="${item.ngaySinh}">

        <label for="gioiTinh">Giới tính</label>
        <select id="gioiTinh" name="gioiTinh">
            <option value="">-- Chọn giới tính --</option>

            <c:choose>
                <c:when test="${gioiTinhDaChon eq 'NAM'}">
                    <option value="NAM" selected>Nam</option>
                </c:when>
                <c:otherwise>
                    <option value="NAM">Nam</option>
                </c:otherwise>
            </c:choose>

            <c:choose>
                <c:when test="${gioiTinhDaChon eq 'NU'}">
                    <option value="NU" selected>Nữ</option>
                </c:when>
                <c:otherwise>
                    <option value="NU">Nữ</option>
                </c:otherwise>
            </c:choose>
        </select>

        <label for="soDienThoai">Số điện thoại *</label>
        <input type="tel"
               id="soDienThoai"
               name="soDienThoai"
               value="<c:out value='${item.soDienThoai}'/>"
               maxlength="15"
               required>

        <label for="email">Email</label>
        <input type="email"
               id="email"
               name="email"
               value="<c:out value='${item.email}'/>"
               maxlength="100">

        <label for="diaChi">Địa chỉ</label>
        <input type="text"
               id="diaChi"
               name="diaChi"
               value="<c:out value='${item.diaChi}'/>"
               maxlength="255">

        <div class="buttons">
            <button type="submit">Lưu thông tin</button>

            <a class="back"
               href="${pageContext.request.contextPath}/khach-hang?action=list">
                Quay lại
            </a>
        </div>

    </form>
</div>
</body>
</html>
