package vn.civilpro.congdan.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

/**
 * REQUEST DTO: TẠO CÔNG DÂN MỚI
 * Validate đầu vào trước khi đến Service layer
 */
@Data
public class CreateCongDanRequest {

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 255, message = "Họ tên tối đa 255 ký tự")
    private String hoTen;

    @NotNull(message = "Giới tính không được để trống")
    @Min(value = 1, message = "Giới tính không hợp lệ")
    @Max(value = 3, message = "Giới tính không hợp lệ")
    private Integer gioiTinh;

    @NotNull(message = "Ngày sinh không được để trống")
    @Past(message = "Ngày sinh phải là ngày trong quá khứ")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate ngaySinh;

    @Size(max = 500, message = "Nơi sinh tối đa 500 ký tự")
    private String noiSinh;

    @Size(max = 50)
    private String danToc;

    @Size(max = 50)
    private String tonGiao;

    @Pattern(regexp = "^[0-9]{12}$", message = "Số CCCD phải đúng 12 chữ số")
    private String soCccd;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate ngayCapCccd;

    @Size(max = 255)
    private String noiCapCccd;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate ngayHetHanCccd;

    @Pattern(regexp = "^(0|\\+84)[0-9]{8,10}$", message = "Số điện thoại không hợp lệ")
    private String soDienThoai;

    @Email(message = "Email không đúng định dạng")
    private String email;

    @NotBlank(message = "Mã ĐVHC thường trú không được để trống")
    private String maDvhcThuongTru;

    @NotBlank(message = "Địa chỉ thường trú không được để trống")
    @Size(max = 500)
    private String diaChiThuongTru;

    @Size(max = 255)
    private String ngheNghiep;

    @Size(max = 100)
    private String trinhDoHocVan;

    @Size(max = 500)
    private String noiLamViec;

    private String loaiDoiTuong;
}