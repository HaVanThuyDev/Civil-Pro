package vn.civilpro.biendong.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * ================================================================
 * ENTITY: BIẾN ĐỘNG DÂN CƯ
 * Ghi nhận mọi sự kiện SINH/TỬ/NHẬP CƯ/XUẤT CƯ/DI CƯ NỘI BỘ
 * ================================================================
 */
@Entity
@Table(name = "BIEN_DONG_DAN_CU", indexes = {
        @Index(name = "IDX_BD_LOAI",        columnList = "LOAI_BIEN_DONG"),
        @Index(name = "IDX_BD_DVHC",        columnList = "MA_DVHC"),
        @Index(name = "IDX_BD_THANG_NAM",   columnList = "THANG_BIEN_DONG,NAM_BIEN_DONG"),
        @Index(name = "IDX_BD_NGAY",        columnList = "NGAY_BIEN_DONG")
})
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class BienDongDanCu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "MA_BIEN_DONG", nullable = false, unique = true, length = 30)
    private String maBienDong;

    /** SINH / TU / NHAP_CU / XUAT_CU / DI_CU_NOI_BO */
    @Column(name = "LOAI_BIEN_DONG", nullable = false, length = 30)
    private String loaiBienDong;

    /** ID công dân liên quan (null nếu chưa có hồ sơ - chỉ khai sinh) */
    @Column(name = "ID_CONG_DAN")
    private Long idCongDan;

    @Column(name = "HO_TEN", length = 255)
    private String hoTen;

    @Column(name = "NGAY_SINH")
    private LocalDate ngaySinh;

    @Column(name = "NGAY_TU_VONG")
    private LocalDate ngayTuVong;

    @Column(name = "MA_DVHC", nullable = false, length = 20)
    private String maDvhc;

    @Column(name = "NGAY_BIEN_DONG", nullable = false)
    private LocalDate ngayBienDong;

    @Column(name = "THANG_BIEN_DONG", nullable = false)
    private Integer thangBienDong;

    @Column(name = "NAM_BIEN_DONG", nullable = false)
    private Integer namBienDong;

    @Column(name = "MO_TA", columnDefinition = "TEXT")
    private String moTa;

    @Column(name = "SO_GIAY_TO", length = 100)
    private String soGiayTo;

    @Column(name = "NGUOI_KHAI_BAO", length = 200)
    private String nguoiKhaiBao;

    @CreatedDate
    @Column(name = "NGAY_TAO", nullable = false, updatable = false)
    private LocalDateTime ngayTao;

    @Column(name = "NGUOI_TAO", length = 100)
    private String nguoiTao;
}