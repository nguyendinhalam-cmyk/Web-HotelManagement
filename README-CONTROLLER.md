# Controller/Servlet layer

Added 8 servlets to the existing MVC project. Existing `DatPhongServlet` is preserved.

| Servlet | URL | Main actions |
|---|---|---|
| KhachHangServlet | `/khach-hang` | list/new/edit/detail/delete/save |
| NhanVienServlet | `/nhan-vien` | list/new/edit/detail/delete/save |
| LoaiPhongServlet | `/loai-phong` | list/new/edit/detail/delete/save |
| PhongServlet | `/phong` | list/new/edit/detail/delete/save/status |
| DatPhongServlet | `/dat-phong` | existing working controller: list/new/available/confirm/cancel/checkin/checkout |
| DichVuServlet | `/dich-vu` | list/new/edit/detail/delete/start/stop/save |
| SuDungDichVuServlet | `/su-dung-dich-vu` | list/new/edit/detail/delete/by-dat-phong/save |
| HoaDonServlet | `/hoa-don` | list/detail/create/by-dat-phong/issue/cancel/delete |
| ThanhToanServlet | `/thanh-toan` | list/detail/new/by-dat-phong/success/fail/refund/create |

## Important

These controllers are matched to the current Service/Entity APIs in the uploaded project.
The JSP screens for the new modules are not present in the uploaded project yet, so the controllers forward to the following planned JSP paths:
- `khach-hang/{danh-sach,form,chi-tiet}.jsp`
- `nhan-vien/{danh-sach,form,chi-tiet}.jsp`
- `loai-phong/{danh-sach,form,chi-tiet}.jsp`
- `phong/{danh-sach,form,chi-tiet}.jsp`
- `dich-vu/{danh-sach,form,chi-tiet}.jsp`
- `dich-vu/su-dung-{danh-sach,form,chi-tiet}.jsp`
- `hoa-don/{danh-sach,chi-tiet}.jsp`
- `thanh-toan/{danh-sach,form,chi-tiet}.jsp`

`DatPhongServlet` and its existing JSPs are kept unchanged.
