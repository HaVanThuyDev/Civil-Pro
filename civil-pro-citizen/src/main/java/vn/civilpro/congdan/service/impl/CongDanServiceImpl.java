package vn.civilpro.congdan.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.civilpro.common.exception.DuplicateResourceException;
import vn.civilpro.common.exception.ResourceNotFoundException;
import vn.civilpro.common.enums.ErrorCode;
import vn.civilpro.congdan.dto.request.CreateCongDanRequest;
import vn.civilpro.congdan.dto.request.SearchCongDanRequest;
import vn.civilpro.congdan.dto.request.UpdateCongDanRequest;
import vn.civilpro.congdan.dto.response.CongDanDetailResponse;
import vn.civilpro.congdan.dto.response.CongDanSummaryResponse;
import vn.civilpro.congdan.entity.CongDan;
import vn.civilpro.congdan.event.CongDanEventPublisher;
import vn.civilpro.congdan.mapper.CongDanMapper;
import vn.civilpro.congdan.repository.CongDanRepository;
import vn.civilpro.congdan.service.CongDanService;
import vn.civilpro.congdan.util.CongDanCodeGenerator;
import vn.civilpro.congdan.util.VietNameseUtils;

import java.time.LocalDate;

/**
 * ================================================================
 * CÔNG DÂN SERVICE IMPLEMENTATION
 * Xử lý business logic chính:
 * - CRUD công dân
 * - Cache với Redis (@Cacheable/@CacheEvict)
 * - Publish Kafka events khi có thay đổi
 * ================================================================
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // Default read-only, chỉ override ở write methods
public class CongDanServiceImpl implements CongDanService {

    private final CongDanRepository congDanRepository;
    private final CongDanMapper congDanMapper;
    private final CongDanEventPublisher eventPublisher;

    // ---- CREATE ----

    @Override
    @Transactional
    public CongDanDetailResponse create(CreateCongDanRequest request) {
        log.info("[CongDanService] Tạo công dân mới, CCCD: {}", request.getSoCccd());

        // Kiểm tra CCCD trùng
        if (request.getSoCccd() != null &&
                congDanRepository.existsBySoCccd(request.getSoCccd())) {
            throw new DuplicateResourceException(ErrorCode.CD_CCCD_EXISTS, request.getSoCccd());
        }

        CongDan entity = congDanMapper.toEntity(request);

        // Tự động sinh mã công dân nội bộ (CD-XXXXXX)
        entity.setMaCongDan(CongDanCodeGenerator.generate());

        // Chuẩn hóa tên không dấu để tìm kiếm nhanh hơn
        entity.setHoTenKhongDau(VietNameseUtils.removeAccent(request.getHoTen()));

        CongDan saved = congDanRepository.save(entity);
        log.info("[CongDanService] Tạo xong, ID: {}, Mã: {}", saved.getId(), saved.getMaCongDan());

        // Publish event cho các service khác subscribe (Kafka)
        eventPublisher.publishCongDanCreated(saved);

        return congDanMapper.toDetailResponse(saved);
    }

    // ---- UPDATE ----

    @Override
    @Transactional
    @CacheEvict(value = {"congDan", "cccdLookup"}, key = "#id")
    public CongDanDetailResponse update(Long id, UpdateCongDanRequest request) {
        log.info("[CongDanService] Cập nhật công dân ID: {}", id);

        CongDan existing = findByIdOrThrow(id);

        // Kiểm tra CCCD nếu thay đổi
        if (request.getSoCccd() != null &&
                !request.getSoCccd().equals(existing.getSoCccd()) &&
                congDanRepository.existsBySoCccdAndIdNot(request.getSoCccd(), id)) {
            throw new DuplicateResourceException(ErrorCode.CD_CCCD_EXISTS, request.getSoCccd());
        }

        congDanMapper.updateEntityFromRequest(request, existing);

        // Cập nhật lại tên không dấu nếu tên thay đổi
        if (request.getHoTen() != null) {
            existing.setHoTenKhongDau(VietNameseUtils.removeAccent(request.getHoTen()));
        }

        CongDan updated = congDanRepository.save(existing);

        // Publish event cập nhật
        eventPublisher.publishCongDanUpdated(updated);

        return congDanMapper.toDetailResponse(updated);
    }

    // ---- READ ----

    @Override
    @Cacheable(value = "congDan", key = "#id", unless = "#result == null")
    public CongDanDetailResponse getById(Long id) {
        return congDanMapper.toDetailResponse(findByIdOrThrow(id));
    }

    @Override
    @Cacheable(value = "cccdLookup", key = "#soCccd", unless = "#result == null")
    public CongDanDetailResponse getByCccd(String soCccd) {
        CongDan congDan = congDanRepository.findBySoCccd(soCccd)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CD_NOT_FOUND, soCccd));
        return congDanMapper.toDetailResponse(congDan);
    }

    @Override
    public Page<CongDanSummaryResponse> search(SearchCongDanRequest request, Pageable pageable) {
        return congDanRepository.searchCongDan(
                request.getHoTen(),
                request.getMaDvhc(),
                request.getLoaiDoiTuong(),
                request.getTrangThai(),
                pageable
        ).map(congDanMapper::toSummaryResponse);
    }

    // ---- KHAI TỬ ----

    @Override
    @Transactional
    @CacheEvict(value = {"congDan", "cccdLookup"}, key = "#id")
    public void khaiTu(Long id, String lyDo) {
        log.info("[CongDanService] Khai tử công dân ID: {}", id);

        CongDan congDan = findByIdOrThrow(id);
        congDan.setTrangThai("DA_CHET");
        congDan.setNgayKhaiTu(LocalDate.now());
        congDan.setLyDoTrangThai(lyDo);

        congDanRepository.save(congDan);

        // Publish event khai tử (HoKhau Service cần biết để cập nhật thành viên)
        eventPublisher.publishCongDanKhaiTu(congDan);
    }

    // ---- HELPER ----

    private CongDan findByIdOrThrow(Long id) {
        return congDanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CD_NOT_FOUND, id));
    }
}