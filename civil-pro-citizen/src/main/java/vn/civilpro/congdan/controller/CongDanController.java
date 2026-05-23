package vn.civilpro.congdan.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.civilpro.common.response.ApiResponse;
import vn.civilpro.congdan.dto.request.CreateCongDanRequest;
import vn.civilpro.congdan.dto.request.KhaiTuRequest;
import vn.civilpro.congdan.dto.request.SearchCongDanRequest;
import vn.civilpro.congdan.dto.request.UpdateCongDanRequest;
import vn.civilpro.congdan.dto.response.CongDanDetailResponse;
import vn.civilpro.congdan.dto.response.CongDanSummaryResponse;
import vn.civilpro.congdan.service.CongDanService;

/**
 * ================================================================
 * REST CONTROLLER: CÔNG DÂN
 * Base path: /api/cong-dan
 * ================================================================
 */
@RestController
@RequestMapping("/cong-dan")
@RequiredArgsConstructor
public class CongDanController {

    private final CongDanService congDanService;

    /**
     * POST /api/cong-dan
     * Thêm công dân mới vào hệ thống
     * Quyền: CONG_DAN:CREATE
     */
    @PostMapping
    @PreAuthorize("hasAuthority('CONG_DAN:CREATE')")
    public ResponseEntity<ApiResponse<CongDanDetailResponse>> create(
            @Valid @RequestBody CreateCongDanRequest request) {

        CongDanDetailResponse result = congDanService.create(request);
        return ResponseEntity.status(201).body(ApiResponse.created(result));
    }

    /**
     * PUT /api/cong-dan/{id}
     * Cập nhật thông tin công dân
     * Quyền: CONG_DAN:UPDATE
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CONG_DAN:UPDATE')")
    public ResponseEntity<ApiResponse<CongDanDetailResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCongDanRequest request) {

        return ResponseEntity.ok(ApiResponse.ok(congDanService.update(id, request)));
    }

    /**
     * GET /api/cong-dan/{id}
     * Lấy thông tin chi tiết một công dân
     * Quyền: CONG_DAN:READ
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CONG_DAN:READ')")
    public ResponseEntity<ApiResponse<CongDanDetailResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(congDanService.getById(id)));
    }

    /**
     * GET /api/cong-dan/cccd/{soCccd}
     * Tìm công dân theo số CCCD
     * Quyền: CONG_DAN:READ
     */
    @GetMapping("/cccd/{soCccd}")
    @PreAuthorize("hasAuthority('CONG_DAN:READ')")
    public ResponseEntity<ApiResponse<CongDanDetailResponse>> getByCccd(
            @PathVariable String soCccd) {
        return ResponseEntity.ok(ApiResponse.ok(congDanService.getByCccd(soCccd)));
    }

    /**
     * GET /api/cong-dan/search?hoTen=&maDvhc=&page=0&size=10
     * Tìm kiếm công dân với nhiều tiêu chí + phân trang
     * Quyền: CONG_DAN:READ
     */
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('CONG_DAN:READ')")
    public ResponseEntity<ApiResponse<Page<CongDanSummaryResponse>>> search(
            @ModelAttribute SearchCongDanRequest request,
            @PageableDefault(size = 10, sort = "hoTen") Pageable pageable) {

        Page<CongDanSummaryResponse> result = congDanService.search(request, pageable);

        return ResponseEntity.ok(ApiResponse.paged(
                result,
                result.getTotalElements(),
                result.getTotalPages(),
                result.getNumber(),
                result.getSize()
        ));
    }

    /**
     * PATCH /api/cong-dan/{id}/khai-tu
     * Khai tử công dân (đổi trạng thái DA_CHET)
     * Quyền: CONG_DAN:UPDATE
     */
    @PatchMapping("/{id}/khai-tu")
    @PreAuthorize("hasAuthority('CONG_DAN:UPDATE')")
    public ResponseEntity<ApiResponse<Void>> khaiTu(
            @PathVariable Long id,
            @Valid @RequestBody KhaiTuRequest request) {

        congDanService.khaiTu(id, request.getLyDo());
        return ResponseEntity.ok(ApiResponse.ok(null, "Khai tử thành công"));
    }
}