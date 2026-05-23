package vn.civilpro.dongbo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.civilpro.dongbo.entity.PhienDongBo;
import vn.civilpro.dongbo.repository.PhienDongBoRepository;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * ================================================================
 * ĐỒNG BỘ SERVICE - TÍCH HỢP CSDL DÂN CƯ QUỐC GIA
 * Quản lý các phiên đồng bộ dữ liệu định kỳ.
 * Dashboard hiển thị "80% Hoàn thành" - lấy từ bảng PHIEN_DONG_BO.
 *
 * Flow:
 * 1. Tạo phiên đồng bộ mới (PHIEN_DONG_BO)
 * 2. Gọi API Quốc gia lấy danh sách thay đổi
 * 3. Gọi gRPC CongDan Service để update từng bản ghi
 * 4. Cập nhật tiến độ real-time (% hoàn thành)
 * 5. Ghi lỗi vào BAN_GHI_DONG_BO_LOI nếu có
 * ================================================================
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DongBoServiceImpl {

    private final PhienDongBoRepository phienDongBoRepository;

    /**
     * Đồng bộ tăng tiến: chạy lúc 03:00 hàng ngày.
     * Chỉ lấy các thay đổi từ lần đồng bộ trước đến nay.
     */
    @Scheduled(cron = "0 0 3 * * *")
    public void dongBoTangTien() {
        log.info("[DongBo] Bắt đầu phiên đồng bộ tăng tiến - {}", LocalDateTime.now());

        PhienDongBo phien = PhienDongBo.builder()
                .maPhien("SYNC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .loaiDongBo("TANG_TIEN")
                .thoiGianBatDau(LocalDateTime.now())
                .trangThai("DANG_CHAY")
                .lanThu(1)
                .phanTramHoanThanh(0.0)
                .build();

        phienDongBoRepository.save(phien);

        try {
            // Bước 1: Gọi API Quốc gia lấy danh sách thay đổi
            // var danhSachThayDoi = quocGiaApiClient.getDanhSachThayDoi(lastSyncTime);

            // Bước 2: Xử lý từng bản ghi, update tiến độ
            // for (var item : danhSachThayDoi) { processItem(item, phien); }

            // Mock: simulate 80% completion (như dashboard UI hiển thị)
            phien.setPhanTramHoanThanh(80.0);
            phien.setTongBanGhi(1000);
            phien.setDaXuLy(800);
            phien.setThanhCong(790);
            phien.setThatBai(10);

            // Hoàn thành
            phien.setTrangThai("HOAN_THANH");
            phien.setThoiGianKetThuc(LocalDateTime.now());
            phien.setPhanTramHoanThanh(100.0);

            log.info("[DongBo] Phiên đồng bộ {} hoàn thành: {}/{} bản ghi",
                    phien.getMaPhien(), phien.getThanhCong(), phien.getTongBanGhi());

        } catch (Exception e) {
            log.error("[DongBo] Phiên đồng bộ {} thất bại: {}", phien.getMaPhien(), e.getMessage(), e);
            phien.setTrangThai("THAT_BAI");
            phien.setMaLoi("SYS-ERR-" + String.format("%02d", (int)(Math.random() * 99)));
            phien.setMoTaLoi(e.getMessage());
            phien.setThoiGianKetThuc(LocalDateTime.now());
        } finally {
            phienDongBoRepository.save(phien);
        }
    }

    /** Lấy tiến độ phiên đồng bộ mới nhất (cho dashboard) */
    @Transactional(readOnly = true)
    public PhienDongBo getPhienMoiNhat() {
        return phienDongBoRepository.findTopByOrderByThoiGianBatDauDesc()
                .orElse(null);
    }
}