package vn.civilpro.congdan.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

// ================================================================
// UPDATE REQUEST - Dùng @Null-safe: chỉ update field nào được gửi
// ================================================================
@Data
public class UpdateCongDanRequest {

    @Size(max = 255)
    private String hoTen;

    @Min(1) @Max(3)
    private Integer gioiTinh;

    @Past
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate ngaySinh;

    @Size(max = 500)
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

    @Pattern(regexp = "^(0|\\+84)[0-9]{8,10}$")
    private String soDienThoai;

    @Email
    private String email;

    private String maDvhcThuongTru;

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