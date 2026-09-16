package vn.civilpro.congdan.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.civilpro.congdan.dto.request.CreateCitizenRequest;
import vn.civilpro.congdan.dto.request.SearchCitizenRequest;
import vn.civilpro.congdan.dto.request.UpdateCitizenRequest;
import vn.civilpro.congdan.dto.response.CitizenDetailResponse;
import vn.civilpro.congdan.dto.response.CitizenSummaryResponse;

public interface CitizenService {

    CitizenDetailResponse create(CreateCitizenRequest request);

    CitizenDetailResponse update(Long id, UpdateCitizenRequest request);

    CitizenDetailResponse getById(Long id);

    CitizenDetailResponse getByIdCardNumber(String idCardNumber);

    Page<CitizenSummaryResponse> search(SearchCitizenRequest request, Pageable pageable);

    void markAsDeceased(Long id, String reason);
}