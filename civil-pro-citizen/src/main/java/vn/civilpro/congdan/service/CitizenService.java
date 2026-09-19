package vn.civilpro.congdan.service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.civilpro.congdan.model.dto.request.CreateCitizenRequest;
import vn.civilpro.congdan.model.dto.request.SearchCitizenRequest;
import vn.civilpro.congdan.model.dto.request.UpdateCitizenRequest;
import vn.civilpro.congdan.model.dto.response.CitizenDetailResponse;
import vn.civilpro.congdan.model.dto.response.CitizenSummaryResponse;
import vn.civilpro.congdan.model.dto.response.PagedResult;

public interface CitizenService {

    CitizenDetailResponse create(CreateCitizenRequest request);

    CitizenDetailResponse update(Long id, UpdateCitizenRequest request);

    CitizenDetailResponse getById(Long id);

    CitizenDetailResponse getByIdCardNumber(String idCardNumber);

    Page<CitizenSummaryResponse> search(SearchCitizenRequest request, Pageable pageable);
    PagedResult<CitizenSummaryResponse> getAll(int page, int size);

    void markAsDeceased(Long id, String reason);
}