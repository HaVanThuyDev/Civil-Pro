package vn.civilpro.hokhau.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * ENTITY: THÀNH VIÊN HỘ KHẨU
 * Bảng liên kết HO_KHAU <-> CONG_DAN (ghi lịch sử nhập/tách)
 */
@Entity
@Table(name = "THANH_VIEN_HO_KHAU", indexes = {
        @Index(name = "IDX_TV_CD",      columnList = "ID_CONG_DAN"),
        @Index(name = "IDX_TV_HK",      columnList = "HO_KHAU_ID"),
        @Index(name = "IDX_TV_TRANG_THAI", columnList = "TRANG_THAI")
})
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ThanhVienHoKhau {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HO_KHAU_ID", nullable = false)
    private HoKhau hoKhau;

    /** ID công dân - tham chiếu sang DB_CONG_DAN, không FK thật */
    @Column(name = "ID_CONG_DAN", nullable = false)
    private Long idCongDan;

    /** Denormalized để hiển thị nhanh */
    @Column(name = "HO_TEN", length = 255)
    private String hoTen;

    @Column(name = "QUAN_HE_CHU_HO", nullable = false, length = 100)
    private String quanHeChuHo;         // CON/VO/CHONG/ANH/CHI/EM/...

    @Column(name = "NGAY_NHAP_HO", nullable = false)
    private LocalDate ngayNhapHo;

    @Column(name = "NGAY_TACH_HO")
    private LocalDate ngayTachHo;       // NULL = đang thuộc hộ

    @Column(name = "LY_DO_TACH", length = 500)
    private String lyDoTach;

    /** 1=Đang ở, 0=Đã tách */
    @Column(name = "TRANG_THAI")
    @Builder.Default
    private Integer trangThai = 1;

    @CreatedDate
    @Column(name = "NGAY_TAO", nullable = false, updatable = false)
    private LocalDateTime ngayTao;
}