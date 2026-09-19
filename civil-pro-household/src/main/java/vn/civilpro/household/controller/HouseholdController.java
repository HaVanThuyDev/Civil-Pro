package vn.civilpro.household.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.civilpro.household.model.dto.request.AddMemberRequest;
import vn.civilpro.household.model.dto.request.CreateHouseholdRequest;
import vn.civilpro.household.model.dto.request.UpdateHouseholdRequest;
import vn.civilpro.household.model.dto.response.ApiResponse;
import vn.civilpro.household.model.dto.response.HouseholdDetailResponse;
import vn.civilpro.household.model.dto.response.HouseholdMemberResponse;
import vn.civilpro.household.service.HouseholdService;

import java.util.List;

@RestController
@RequestMapping("/api/household")
@RequiredArgsConstructor
public class HouseholdController {

    private final HouseholdService householdService;

    @PostMapping
    public ResponseEntity<ApiResponse<HouseholdDetailResponse>> create(@Valid @RequestBody CreateHouseholdRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(householdService.create(request), "Household created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HouseholdDetailResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(householdService.getById(id)));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<HouseholdDetailResponse>> getByCode(@PathVariable String code) {
        return ResponseEntity.ok(ApiResponse.ok(householdService.getByHouseholdCode(code)));
    }

    @GetMapping("/citizen/{citizenId}")
    public ResponseEntity<ApiResponse<HouseholdDetailResponse>> getByCitizenId(@PathVariable Long citizenId) {
        return ResponseEntity.ok(ApiResponse.ok(householdService.getByCitizenId(citizenId)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<HouseholdDetailResponse>> update(
            @PathVariable Long id,
            @RequestBody UpdateHouseholdRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(householdService.update(id, request)));
    }

    @PostMapping("/{id}/members")
    public ResponseEntity<ApiResponse<Void>> addMember(
            @PathVariable Long id,
            @Valid @RequestBody AddMemberRequest request) {
        householdService.addMember(id, request);
        return ResponseEntity.ok(ApiResponse.ok(null, "Member added successfully"));
    }

    @DeleteMapping("/{id}/members/{citizenId}")
    public ResponseEntity<ApiResponse<Void>> removeMember(
            @PathVariable Long id,
            @PathVariable Long citizenId,
            @RequestParam(defaultValue = "Separated from household") String reason) {
        householdService.removeMember(id, citizenId, reason);
        return ResponseEntity.ok(ApiResponse.ok(null, "Member removed successfully"));
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<ApiResponse<List<HouseholdMemberResponse>>> getMembers(
            @PathVariable Long id,
            @RequestParam(defaultValue = "true") boolean activeOnly) {
        return ResponseEntity.ok(ApiResponse.ok(householdService.getMembers(id, activeOnly)));
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> countByArea(@RequestParam String areaCode) {
        return ResponseEntity.ok(ApiResponse.ok(householdService.countHouseholdsByArea(areaCode)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<HouseholdDetailResponse>>> search(
            @RequestParam(required = false) String areaCode,
            @RequestParam(required = false) String headFullName,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String householdType,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(householdService.search(areaCode, headFullName, status, householdType, pageable)));
    }
}
