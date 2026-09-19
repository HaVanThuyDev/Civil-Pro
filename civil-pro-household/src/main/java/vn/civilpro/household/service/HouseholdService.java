package vn.civilpro.household.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.civilpro.household.model.dto.request.AddMemberRequest;
import vn.civilpro.household.model.dto.request.CreateHouseholdRequest;
import vn.civilpro.household.model.dto.request.UpdateHouseholdRequest;
import vn.civilpro.household.model.dto.response.HouseholdDetailResponse;
import vn.civilpro.household.model.dto.response.HouseholdMemberResponse;

import java.util.List;

public interface HouseholdService {

    HouseholdDetailResponse create(CreateHouseholdRequest request);

    HouseholdDetailResponse update(Long id, UpdateHouseholdRequest request);

    void addMember(Long householdId, AddMemberRequest request);

    void removeMember(Long householdId, Long citizenId, String reason);

    HouseholdDetailResponse getById(Long id);

    HouseholdDetailResponse getByHouseholdCode(String householdCode);

    HouseholdDetailResponse getByCitizenId(Long citizenId);

    List<HouseholdMemberResponse> getMembers(Long householdId, boolean activeOnly);

    long countHouseholdsByArea(String areaCode);

    Page<HouseholdDetailResponse> search(String areaCode, String headFullName, String status, String householdType, Pageable pageable);
}
