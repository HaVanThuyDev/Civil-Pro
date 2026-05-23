package vn.civilpro.congdan.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.civilpro.congdan.dto.request.CreateCongDanRequest;
import vn.civilpro.congdan.dto.request.SearchCongDanRequest;
import vn.civilpro.congdan.dto.request.UpdateCongDanRequest;
import vn.civilpro.congdan.dto.response.CongDanDetailResponse;
import vn.civilpro.congdan.dto.response.CongDanSummaryResponse;

/**
 * CÔNG DÂN SERVICE INTERFACE
 * Define contract - implementation ở CongDanServiceImpl
 */
public interface CongDanService {

    /** Thêm công dân mới vào hệ thống */
    CongDanDetailResponse create(CreateCongDanRequest request);

    /** Cập nhật thông tin công dân */
    CongDanDetailResponse update(Long id, UpdateCongDanRequest request);

    /** Lấy thông tin chi tiết theo ID */
    CongDanDetailResponse getById(Long id);

    /** Lấy thông tin theo số CCCD */
    CongDanDetailResponse getByCccd(String soCccd);

    /** Tìm kiếm công dân với nhiều tiêu chí */
    Page<CongDanSummaryResponse> search(SearchCongDanRequest request, Pageable pageable);

    /** Khai tử: đổi trạng thái sang DA_CHET */
    void khaiTu(Long id, String lyDo);
}