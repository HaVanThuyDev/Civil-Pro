package vn.civilpro.hokhau.service.impl;

import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.civilpro.common.enums.ErrorCode;
import vn.civilpro.common.exception.BusinessException;
import vn.civilpro.common.exception.GrpcServiceException;
import vn.civilpro.common.exception.ResourceNotFoundException;
import vn.civilpro.grpc.congdan.CongDanGrpcServiceGrpc;
import vn.civilpro.grpc.congdan.GetCongDanByIdRequest;
import vn.civilpro.grpc.congdan.GetCongDanResponse;
import vn.civilpro.hokhau.dto.request.CreateHoKhauRequest;
import vn.civilpro.hokhau.dto.request.ThemThanhVienRequest;
import vn.civilpro.hokhau.dto.response.HoKhauDetailResponse;
import vn.civilpro.hokhau.entity.HoKhau;
import vn.civilpro.hokhau.entity.ThanhVienHoKhau;
import vn.civilpro.hokhau.event.HoKhauEventPublisher;
import vn.civilpro.hokhau.mapper.HoKhauMapper;
import vn.civilpro.hokhau.repository.HoKhauRepository;
import vn.civilpro.hokhau.repository.ThanhVienHoKhauRepository;
import vn.civilpro.hokhau.util.HoKhauCodeGenerator;

import java.time.LocalDate;

/**
 * ================================================================
 * HỘ KHẨU SERVICE IMPLEMENTATION
 * Điểm quan trọng: gọi gRPC đến CongDan Service để validate
 * thông tin công dân trước khi tạo/thêm vào hộ khẩu.
 * ================================================================
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HoKhauServiceImpl {

    private final HoKhauRepository hoKhauRepository;
    private final ThanhVienHoKhauRepository thanhVienRepository;
    private final HoKhauMapper hoKhauMapper;
    private final HoKhauEventPublisher eventPublisher;

    /**
     * gRPC stub kết nối tới CongDan Service.
     * "cong-dan-service" phải khớp với key trong grpc.client.* trong application.yml.
     * Eureka tự resolve địa chỉ, load balance round-robin.
     */
    @GrpcClient("cong-dan-service")
    private CongDanGrpcServiceGrpc.CongDanGrpcServiceBlockingStub congDanGrpcStub;

    // ---- TẠO HỘ KHẨU MỚI ----

    @Transactional
    public HoKhauDetailResponse create(CreateHoKhauRequest request) {
        log.info("[HoKhauService] Tạo hộ khẩu mới, chủ hộ ID: {}", request.getIdChuHo());

        // Gọi gRPC sang CongDan Service để validate chủ hộ
        var congDanInfo = getCongDanViaGrpc(request.getIdChuHo());

        if (!"HOAT_DONG".equals(congDanInfo.getTrangThai())) {
            throw new BusinessException(ErrorCode.CD_DA_CHET);
        }

        HoKhau hoKhau = HoKhau.builder()
                .maHoKhau(HoKhauCodeGenerator.generate())
                .idChuHo(request.getIdChuHo())
                .tenChuHo(congDanInfo.getHoTen())   // Denormalized từ gRPC response
                .maDvhc(request.getMaDvhc())
                .diaChiDayDu(request.getDiaChiDayDu())
                .ngayDangKy(LocalDate.now())
                .loaiHo(request.getLoaiHo())
                .build();

        HoKhau saved = hoKhauRepository.save(hoKhau);

        // Thêm chủ hộ vào danh sách thành viên
        ThanhVienHoKhau chuHo = ThanhVienHoKhau.builder()
                .hoKhau(saved)
                .idCongDan(request.getIdChuHo())
                .hoTen(congDanInfo.getHoTen())
                .quanHeChuHo("CHU_HO")
                .ngayNhapHo(LocalDate.now())
                .trangThai(1)
                .build();

        thanhVienRepository.save(chuHo);
        saved.setThanhViens(java.util.List.of(chuHo));
        saved.setSoThanhVien(1);
        hoKhauRepository.save(saved);

        log.info("[HoKhauService] Tạo xong HK: {}", saved.getMaHoKhau());
        eventPublisher.publishHoKhauCreated(saved);

        return hoKhauMapper.toDetailResponse(saved);
    }

    // ---- THÊM THÀNH VIÊN VÀO HỘ ----

    @Transactional
    @CacheEvict(value = "hoKhau", key = "#hoKhauId")
    public void themThanhVien(Long hoKhauId, ThemThanhVienRequest request) {
        HoKhau hoKhau = findByIdOrThrow(hoKhauId);

        // Kiểm tra đã là thành viên chưa
        boolean daCoTrongHo = thanhVienRepository
                .existsByHoKhauIdAndIdCongDanAndTrangThai(hoKhauId, request.getIdCongDan(), 1);
        if (daCoTrongHo) {
            throw new BusinessException(ErrorCode.HK_THANH_VIEN_TON_TAI);
        }

        // Validate công dân qua gRPC
        var congDanInfo = getCongDanViaGrpc(request.getIdCongDan());

        ThanhVienHoKhau thanhVien = ThanhVienHoKhau.builder()
                .hoKhau(hoKhau)
                .idCongDan(request.getIdCongDan())
                .hoTen(congDanInfo.getHoTen())
                .quanHeChuHo(request.getQuanHeChuHo())
                .ngayNhapHo(LocalDate.now())
                .trangThai(1)
                .build();

        thanhVienRepository.save(thanhVien);

        // Cập nhật số thành viên
        hoKhau.setSoThanhVien(hoKhau.getSoThanhVien() + 1);
        hoKhauRepository.save(hoKhau);

        log.info("[HoKhauService] Thêm thành viên {} vào HK {}", request.getIdCongDan(), hoKhauId);
    }

    // ---- READ ----

    @Cacheable(value = "hoKhau", key = "#id")
    public HoKhauDetailResponse getById(Long id) {
        return hoKhauMapper.toDetailResponse(findByIdOrThrow(id));
    }

    public Page<HoKhauDetailResponse> search(String maDvhc, String tenChuHo,
                                             String trangThai, String loaiHo,
                                             Pageable pageable) {
        return hoKhauRepository.search(maDvhc, tenChuHo, trangThai, loaiHo, pageable)
                .map(hoKhauMapper::toDetailResponse);
    }

    // ---- gRPC HELPER ----

    /**
     * Gọi CongDan Service qua gRPC để lấy thông tin công dân.
     * Nếu gRPC lỗi → ném GrpcServiceException (HTTP 503).
     */
    private vn.civilpro.grpc.congdan.CongDanInfo getCongDanViaGrpc(Long congDanId) {
        try {
            GetCongDanResponse response = congDanGrpcStub.getById(
                    GetCongDanByIdRequest.newBuilder().setId(congDanId).build()
            );

            if (!response.getMeta().getSuccess()) {
                throw new ResourceNotFoundException(ErrorCode.CD_NOT_FOUND, congDanId);
            }

            return response.getData();

        } catch (StatusRuntimeException e) {
            log.error("[gRPC] Lỗi gọi CongDan Service: {}", e.getStatus());
            throw new GrpcServiceException(ErrorCode.GRPC_CONG_DAN_SERVICE_ERROR, e, congDanId);
        }
    }

    // ---- PRIVATE HELPER ----

    private HoKhau findByIdOrThrow(Long id) {
        return hoKhauRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.HK_NOT_FOUND, id));
    }
}