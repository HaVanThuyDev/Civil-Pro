package vn.civilpro.hokhau.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.civilpro.hokhau.entity.HoKhau;

import java.util.Optional;

@Repository
public interface HoKhauRepository extends JpaRepository<HoKhau, Long> {

    Optional<HoKhau> findByMaHoKhau(String maHoKhau);

    boolean existsByMaHoKhau(String maHoKhau);

    Page<HoKhau> findByMaDvhcAndTrangThai(String maDvhc, String trangThai, Pageable pageable);

    /** Tìm hộ khẩu của một công dân (đang là thành viên tích cực) */
    @Query("""
        SELECT hk FROM HoKhau hk
        JOIN hk.thanhViens tv
        WHERE tv.idCongDan = :idCongDan
        AND tv.trangThai = 1
        AND hk.trangThai = 'HOAT_DONG'
        """)
    Optional<HoKhau> findHoKhauCuaCongDan(@Param("idCongDan") Long idCongDan);

    /** Đếm số hộ theo ĐVHC (dashboard) */
    @Query("SELECT COUNT(h) FROM HoKhau h WHERE h.maDvhc = :maDvhc AND h.trangThai = 'HOAT_DONG'")
    long countByDvhc(@Param("maDvhc") String maDvhc);

    /** Tìm kiếm đa tiêu chí */
    @Query("""
        SELECT h FROM HoKhau h
        WHERE (:maDvhc IS NULL OR h.maDvhc = :maDvhc)
        AND (:tenChuHo IS NULL OR LOWER(h.tenChuHo) LIKE LOWER(CONCAT('%',:tenChuHo,'%')))
        AND (:trangThai IS NULL OR h.trangThai = :trangThai)
        AND (:loaiHo IS NULL OR h.loaiHo = :loaiHo)
        """)
    Page<HoKhau> search(
            @Param("maDvhc") String maDvhc,
            @Param("tenChuHo") String tenChuHo,
            @Param("trangThai") String trangThai,
            @Param("loaiHo") String loaiHo,
            Pageable pageable
    );
}