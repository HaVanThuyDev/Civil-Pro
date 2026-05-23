package vn.civilpro.hokhau.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ================================================================
 * ENTITY: HỘ KHẨU
 * Map bảng HO_KHAU trong DB_HO_KHAU
 * ================================================================
 */
@Entity
@Table(name = "HO_KHAU", indexes = {
        @Index(name = "IDX_MA_DVHC_HK",    columnList = "MA_DVHC"),
        @Index(name = "IDX_TRANG_THAI_HK",  columnList = "TRANG_THAI"),
        @Index(name = "IDX_ID_CHU_HO",      columnList = "ID_CHU_HO")
})
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class HoKhau {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "MA_HO_KHAU", nullable = false, unique = true, length = 30)
    private String maHoKhau;

    @Column(name = "SO_HO_KHAU", length = 50)
    private String soHoKhau;

    /** ID công dân làm chủ hộ - tham chiếu sang DB_CONG_DAN qua gRPC */
    @Column(name = "ID_CHU_HO", nullable = false)
    private Long idChuHo;

    /** Denormalized để tránh gRPC call mỗi lần hiển thị */
    @Column(name = "TEN_CHU_HO", nullable = false, length = 255)
    private String tenChuHo;

    /** Mã ĐVHC tham chiếu sang DB_DVHC */
    @Column(name = "MA_DVHC", nullable = false, length = 20)
    private String maDvhc;

    @Column(name = "DIA_CHI_DAY_DU", nullable = false, length = 500)
    private String diaChiDayDu;

    @Column(name = "SO_THANH_VIEN")
    @Builder.Default
    private Integer soThanhVien = 0;

    @Column(name = "LOAI_HO", length = 50)
    private String loaiHo;              // HO_NGHEO / HO_CAN_NGHEO / HO_BINH_THUONG

    @Column(name = "NGAY_DANG_KY", nullable = false)
    private LocalDate ngayDangKy;

    @Column(name = "TRANG_THAI", nullable = false, length = 20)
    @Builder.Default
    private String trangThai = "HOAT_DONG";

    @Column(name = "GHI_CHU", columnDefinition = "TEXT")
    private String ghiChu;

    /** Quan hệ 1-N với ThanhVienHoKhau (lazy để tránh over-fetch) */
    @OneToMany(mappedBy = "hoKhau", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ThanhVienHoKhau> thanhViens = new ArrayList<>();

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

    @Version
    @Builder.Default
    private Integer version = 0;
}