package vn.civilpro.congdan.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

// ================================================================
// DETAIL RESPONSE - Đầy đủ thông tin (dùng cho GET /cong-dan/{id})
// ================================================================
@Data
@Builder
public class CongDanDetailResponse {

    private Long id;
    private String maCongDan;
    private String hoTen;
    private String gioiTinhLabel;           // "Nam" / "Nữ" (đã convert từ số)

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate ngaySinh;

    private Integer tuoi;                   // Tính tuổi tự động
    private String noiSinh;
    private String danToc;
    private String tonGiao;
    private String quocTich;

    // Định danh
    private String soCccd;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate ngayCapCccd;

    private String noiCapCccd;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate ngayHetHanCccd;

    private boolean cccdSapHetHan;          // Cờ cảnh báo CCCD sắp hết hạn (< 90 ngày)

    // Liên hệ
    private String soDienThoai;
    private String email;

    // Cư trú
    private String maDvhcThuongTru;
    private String diaChiThuongTru;
    private String maDvhcTamTru;
    private String diaChiTamTru;

    // Nghề nghiệp
    private String ngheNghiep;
    private String trinhDoHocVan;
    private String noiLamViec;

    // Phân loại
    private String loaiDoiTuong;
    private Boolean laChuHo;
    private Long idHoKhau;

    // Trạng thái
    private String trangThai;
    private String trangThaiLabel;          // "Đang hoạt động" / "Đã khai tử"

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate ngayKhaiTu;

    // Audit
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime ngayTao;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime ngayCapNhat;

    private String nguoiTao;
    private String nguoiCapNhat;
}