package vn.civilpro.congdan.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.civilpro.congdan.entity.CongDan;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * ================================================================
 * REPOSITORY: CÔNG DÂN
 * Extends JpaSpecificationExecutor để hỗ trợ dynamic filtering (Specification)
 * ================================================================
 */
@Repository
public interface CongDanRepository extends JpaRepository<CongDan, Long>,
        JpaSpecificationExecutor<CongDan> {

    // ---- Tìm kiếm cơ bản ----

    Optional<CongDan> findBySoCccd(String soCccd);

    Optional<CongDan> findByMaCongDan(String maCongDan);

    boolean existsBySoCccd(String soCccd);

    /** Kiểm tra CCCD tồn tại, bỏ qua bản ghi có ID cụ thể (dùng khi update) */
    boolean existsBySoCccdAndIdNot(String soCccd, Long excludeId);

    List<CongDan> findByIdIn(List<Long> ids);

    // ---- Tìm kiếm theo ĐVHC ----

    Page<CongDan> findByMaDvhcThuongTruAndTrangThai(String maDvhc, String trangThai, Pageable pageable);

    /** Đếm dân số theo ĐVHC (dùng cho thống kê dashboard) */
    @Query("SELECT COUNT(c) FROM CongDan c WHERE c.maDvhcThuongTru = :maDvhc AND c.trangThai = 'HOAT_DONG'")
    long countDanSoByDvhc(@Param("maDvhc") String maDvhc);

    // ---- Tìm kiếm full-text (có thể dùng LIKE hoặc MySQL FULLTEXT) ----

    @Query("""
        SELECT c FROM CongDan c
        WHERE (:hoTen IS NULL OR LOWER(c.hoTenKhongDau) LIKE LOWER(CONCAT('%', :hoTen, '%')))
        AND (:maDvhc IS NULL OR c.maDvhcThuongTru = :maDvhc)
        AND (:loaiDoiTuong IS NULL OR c.loaiDoiTuong = :loaiDoiTuong)
        AND (:trangThai IS NULL OR c.trangThai = :trangThai)
        """)
    Page<CongDan> searchCongDan(
            @Param("hoTen") String hoTen,
            @Param("maDvhc") String maDvhc,
            @Param("loaiDoiTuong") String loaiDoiTuong,
            @Param("trangThai") String trangThai,
            Pageable pageable
    );

    // ---- Thống kê cơ cấu độ tuổi ----

    @Query("""
        SELECT
            SUM(CASE WHEN YEAR(CURRENT_DATE) - YEAR(c.ngaySinh) <= 14 THEN 1 ELSE 0 END) AS tuoi0_14,
            SUM(CASE WHEN YEAR(CURRENT_DATE) - YEAR(c.ngaySinh) BETWEEN 15 AND 64 THEN 1 ELSE 0 END) AS tuoi15_64,
            SUM(CASE WHEN YEAR(CURRENT_DATE) - YEAR(c.ngaySinh) >= 65 THEN 1 ELSE 0 END) AS tuoiTren65
        FROM CongDan c
        WHERE c.maDvhcThuongTru LIKE CONCAT(:maDvhcPrefix, '%')
        AND c.trangThai = 'HOAT_DONG'
        """)
    Object[] thongKeCoCauDoTuoi(@Param("maDvhcPrefix") String maDvhcPrefix);

    // ---- Cảnh báo CCCD sắp hết hạn ----

    @Query("""
        SELECT c FROM CongDan c
        WHERE c.ngayHetHanCccd IS NOT NULL
        AND c.ngayHetHanCccd BETWEEN :tuNgay AND :denNgay
        AND c.trangThai = 'HOAT_DONG'
        """)
    List<CongDan> findCccdSapHetHan(@Param("tuNgay") LocalDate tuNgay,
                                    @Param("denNgay") LocalDate denNgay);

    /** Cập nhật ID hộ khẩu sau khi nhập hộ (bulk update) */
    @Modifying
    @Query("UPDATE CongDan c SET c.idHoKhau = :idHoKhau WHERE c.id IN :ids")
    int updateHoKhauForCongDanList(@Param("idHoKhau") Long idHoKhau,
                                   @Param("ids") List<Long> ids);
}