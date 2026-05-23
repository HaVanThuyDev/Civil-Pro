package vn.civilpro.congdan.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * ================================================================
 * ENTITY: CÔNG DÂN
 * Map với bảng CONG_DAN trong DB_CONG_DAN
 * ================================================================
 */
@Entity
@Table(
        name = "CONG_DAN",
        indexes = {
                @Index(name = "IDX_HO_TEN", columnList = "HO_TEN"),
                @Index(name = "IDX_NGAY_SINH", columnList = "NGAY_SINH"),
                @Index(name = "IDX_MA_DVHC_TT", columnList = "MA_DVHC_THUONG_TRU"),
                @Index(name = "IDX_LOAI_DOI_TUONG", columnList = "LOAI_DOI_TUONG"),
                @Index(name = "IDX_TRANG_THAI", columnList = "TRANG_THAI")
        }
)
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CongDan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "MA_CONG_DAN", nullable = false, unique = true, length = 20)
    private String maCongDan;

    @Column(name = "HO_TEN", nullable = false, length = 255)
    private String hoTen;

    @Column(name = "HO_TEN_KHONG_DAU", length = 255)
    private String hoTenKhongDau;           // Phục vụ tìm kiếm không dấu

    @Column(name = "GIOI_TINH", nullable = false)
    private Integer gioiTinh;               // 1=Nam, 2=Nữ

    @Column(name = "NGAY_SINH", nullable = false)
    private LocalDate ngaySinh;

    @Column(name = "NOI_SINH", length = 500)
    private String noiSinh;

    @Column(name = "DAN_TOC", length = 50)
    private String danToc;

    @Column(name = "TON_GIAO", length = 50)
    private String tonGiao;

    @Column(name = "QUOC_TICH", length = 50)
    @Builder.Default
    private String quocTich = "VIỆT NAM";

    // ---- Giấy tờ định danh ----
    @Column(name = "SO_CCCD", unique = true, length = 12)
    private String soCccd;

    @Column(name = "NGAY_CAP_CCCD")
    private LocalDate ngayCapCccd;

    @Column(name = "NOI_CAP_CCCD", length = 255)
    private String noiCapCccd;

    @Column(name = "NGAY_HET_HAN_CCCD")
    private LocalDate ngayHetHanCccd;       // Cảnh báo sắp hết hạn

    // ---- Liên hệ ----
    @Column(name = "SO_DIEN_THOAI", length = 15)
    private String soDienThoai;

    @Column(name = "EMAIL", length = 255)
    private String email;

    // ---- Cư trú ----
    @Column(name = "MA_DVHC_THUONG_TRU", length = 20)
    private String maDvhcThuongTru;

    @Column(name = "DIA_CHI_THUONG_TRU", length = 500)
    private String diaChiThuongTru;

    @Column(name = "MA_DVHC_TAM_TRU", length = 20)
    private String maDvhcTamTru;

    @Column(name = "DIA_CHI_TAM_TRU", length = 500)
    private String diaChiTamTru;

    // ---- Nghề nghiệp & trình độ ----
    @Column(name = "NGHE_NGHIEP", length = 255)
    private String ngheNghiep;

    @Column(name = "TRINH_DO_HOC_VAN", length = 100)
    private String trinhDoHocVan;

    @Column(name = "NOI_LAM_VIEC", length = 500)
    private String noiLamViec;

    // ---- Phân loại ----
    @Column(name = "LOAI_DOI_TUONG", length = 50)
    private String loaiDoiTuong;            // LAO_DONG/TRE_EM/NGUOI_CAO_TUOI/...

    @Column(name = "LA_CHU_HO")
    @Builder.Default
    private Boolean laChuHo = false;

    @Column(name = "ID_HO_KHAU")
    private Long idHoKhau;                  // Reference sang DB_HO_KHAU (không FK trực tiếp)

    // ---- Trạng thái ----
    @Column(name = "TRANG_THAI", nullable = false, length = 20)
    @Builder.Default
    private String trangThai = "HOAT_DONG"; // HOAT_DONG/DA_CHET/XUAT_CANH

    @Column(name = "NGAY_KHAI_TU")
    private LocalDate ngayKhaiTu;

    @Column(name = "LY_DO_TRANG_THAI", length = 500)
    private String lyDoTrangThai;

    // ---- JPA Auditing (tự động fill) ----
    @CreatedDate
    @Column(name = "NGAY_TAO", nullable = false, updatable = false)
    private LocalDateTime ngayTao;

    @LastModifiedDate
    @Column(name = "NGAY_CAP_NHAT")
    private LocalDateTime ngayCapNhat;

    @CreatedBy
    @Column(name = "NGUOI_TAO", length = 100, updatable = false)
    private String nguoiTao;

    @LastModifiedBy
    @Column(name = "NGUOI_CAP_NHAT", length = 100)
    private String nguoiCapNhat;

    // ---- Optimistic Locking: tránh concurrent update ----
    @Version
    @Column(name = "VERSION")
    @Builder.Default
    private Integer version = 0;
}