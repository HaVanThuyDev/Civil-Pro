package vn.civilpro.thongke.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * ================================================================
 * ENTITY: THỐNG KÊ DÂN SỐ THEO ĐVHC VÀ THỜI KỲ
 * Bảng cache được cập nhật bởi scheduled job.
 * Dashboard đọc từ bảng này thay vì query real-time từ DB_CONG_DAN.
 * ================================================================
 */
@Entity
@Table(name = "THONG_KE_DAN_SO_DVHC", indexes = {
        @Index(name = "IDX_TK_NAM",  columnList = "NAM"),
        @Index(name = "IDX_TK_DVHC", columnList = "MA_DVHC")
}, uniqueConstraints = {
        @UniqueConstraint(name = "UQ_TK_DVHC_NAM_THANG", columnNames = {"MA_DVHC", "NAM", "THANG"})
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ThongKeDanSo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "MA_DVHC", nullable = false, length = 20)
    private String maDvhc;

    @Column(name = "NAM", nullable = false)
    private Integer nam;

    /** Null = thống kê cả năm, có giá trị = thống kê theo tháng */
    @Column(name = "THANG")
    private Integer thang;

    @Column(name = "TONG_DAN_SO")
    @Builder.Default
    private Integer tongDanSo = 0;

    @Column(name = "TONG_NAM")
    @Builder.Default
    private Integer tongNam = 0;

    @Column(name = "TONG_NU")
    @Builder.Default
    private Integer tongNu = 0;

    @Column(name = "SO_HO")
    @Builder.Default
    private Integer soHo = 0;

    /** Dân số 0-14 tuổi */
    @Column(name = "DAN_SO_0_14")
    @Builder.Default
    private Integer danSo0_14 = 0;

    /** Dân số 15-64 tuổi */
    @Column(name = "DAN_SO_15_64")
    @Builder.Default
    private Integer danSo15_64 = 0;

    /** Dân số trên 65 tuổi */
    @Column(name = "DAN_SO_TREN_65")
    @Builder.Default
    private Integer danSoTren65 = 0;

    @Column(name = "MAT_DO_DAN_SO", precision = 10)
    private Double matDoDanSo;

    @Column(name = "NGAY_TINH")
    private LocalDateTime ngayTinh;
}