package vn.civilpro.congdan.dto.request;

import lombok.Data;

/**
 * SEARCH REQUEST - Tham số tìm kiếm công dân
 * Tất cả field đều optional (null = bỏ qua điều kiện đó)
 */
@Data
public class SearchCongDanRequest {

    /** Tìm theo tên (hỗ trợ không dấu) */
    private String hoTen;

    /** Lọc theo CCCD */
    private String soCccd;

    /** Lọc theo đơn vị hành chính */
    private String maDvhc;

    /** Lọc theo loại đối tượng: LAO_DONG/TRE_EM/NGUOI_CAO_TUOI/... */
    private String loaiDoiTuong;

    /** Lọc theo trạng thái: HOAT_DONG/DA_CHET/XUAT_CANH */
    private String trangThai;

    /** Năm sinh từ */
    private Integer namSinhTu;

    /** Năm sinh đến */
    private Integer namSinhDen;
}