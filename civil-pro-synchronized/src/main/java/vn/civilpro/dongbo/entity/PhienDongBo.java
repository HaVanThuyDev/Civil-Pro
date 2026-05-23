package vn.civilpro.dongbo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * ENTITY: PHIÊN ĐỒNG BỘ DỮ LIỆU VỚI CSDL QUỐC GIA
 * Theo dõi tiến độ từng lần đồng bộ.
 * Dashboard "Tích hợp CSDL Quốc gia - 80% Hoàn thành" lấy từ đây.
 */
@Entity
@Table(name = "PHIEN_DONG_BO", indexes = {
        @Index(name = "IDX_PDB_TRANG_THAI", columnList = "TRANG_THAI"),
        @Index(name = "IDX_PDB_THOI_GIAN",  columnList = "THOI_GIAN_BAT_DAU")
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PhienDongBo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "MA_PHIEN", nullable = false, unique = true, length = 50)
    private String maPhien;

    /** TOAN_BO / TANG_TIEN / KHOI_PHUC */
    @Column(name = "LOAI_DONG_BO", nullable = false, length = 50)
    private String loaiDongBo;

    @Column(name = "THOI_GIAN_BAT_DAU", nullable = false)
    private LocalDateTime thoiGianBatDau;

    @Column(name = "THOI_GIAN_KET_THUC")
    private LocalDateTime thoiGianKetThuc;

    /** DANG_CHAY / HOAN_THANH / THAT_BAI / RETRY */
    @Column(name = "TRANG_THAI", nullable = false, length = 20)
    @Builder.Default
    private String trangThai = "DANG_CHAY";

    @Column(name = "TONG_BAN_GHI")
    @Builder.Default
    private Integer tongBanGhi = 0;

    @Column(name = "DA_XU_LY")
    @Builder.Default
    private Integer daXuLy = 0;

    @Column(name = "THANH_CONG")
    @Builder.Default
    private Integer thanhCong = 0;

    @Column(name = "THAT_BAI")
    @Builder.Default
    private Integer thatBai = 0;

    /** Phần trăm hoàn thành (0.00 - 100.00) - hiển thị trên dashboard */
    @Column(name = "PHAN_TRAM_HOAN_THANH", precision = 5)
    @Builder.Default
    private Double phanTramHoanThanh = 0.0;

    @Column(name = "MA_LOI", length = 50)
    private String maLoi;

    @Column(name = "MO_TA_LOI", columnDefinition = "TEXT")
    private String moTaLoi;

    @Column(name = "LAN_THU")
    @Builder.Default
    private Integer lanThu = 1;
}