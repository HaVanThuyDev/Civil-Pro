package vn.civilpro.congdan.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * SUMMARY RESPONSE - Thông tin tóm tắt (dùng cho danh sách / tìm kiếm)
 * Nhẹ hơn DetailResponse, tránh over-fetch
 */
@Data
@Builder
public class CongDanSummaryResponse {

    private Long id;
    private String maCongDan;
    private String hoTen;
    private String gioiTinhLabel;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate ngaySinh;

    private Integer tuoi;
    private String soCccd;
    private String diaChiThuongTru;
    private String ngheNghiep;
    private String loaiDoiTuong;
    private String trangThai;
    private String trangThaiLabel;
}