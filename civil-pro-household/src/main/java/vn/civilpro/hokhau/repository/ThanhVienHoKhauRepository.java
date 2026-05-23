package vn.civilpro.hokhau.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.civilpro.hokhau.entity.ThanhVienHoKhau;

import java.util.List;

@Repository
public interface ThanhVienHoKhauRepository extends JpaRepository<ThanhVienHoKhau, Long> {

    List<ThanhVienHoKhau> findByHoKhauIdAndTrangThai(Long hoKhauId, Integer trangThai);

    boolean existsByHoKhauIdAndIdCongDanAndTrangThai(Long hoKhauId, Long idCongDan, Integer trangThai);
}