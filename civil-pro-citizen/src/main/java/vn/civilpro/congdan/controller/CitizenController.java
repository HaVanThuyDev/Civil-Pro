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
import vn.civilpro.congdan.dto.request.CreateCitizenRequest;
import vn.civilpro.congdan.dto.request.DeathRegistrationRequest;
import vn.civilpro.congdan.dto.request.SearchCitizenRequest;
import vn.civilpro.congdan.dto.request.UpdateCitizenRequest;
import vn.civilpro.congdan.dto.response.CitizenDetailResponse;
import vn.civilpro.congdan.dto.response.CitizenSummaryResponse;
import vn.civilpro.congdan.service.CitizenService;

@RestController
@RequestMapping("/citizens")
@RequiredArgsConstructor
public class CitizenController {

    private final CitizenService citizenService;

    @PostMapping
    @PreAuthorize("hasAuthority('CITIZEN:CREATE')")
    public ResponseEntity<ApiResponse<CitizenDetailResponse>> create(@Valid @RequestBody CreateCitizenRequest request) {
        CitizenDetailResponse result = citizenService.create(request);
        return ResponseEntity.status(201).body(ApiResponse.created(result, "Citizen created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CITIZEN:UPDATE')")
    public ResponseEntity<ApiResponse<CitizenDetailResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCitizenRequest request) {

        return ResponseEntity.ok(ApiResponse.ok(citizenService.update(id, request)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CITIZEN:READ')")
    public ResponseEntity<ApiResponse<CitizenDetailResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(citizenService.getById(id)));
    }

    @GetMapping("/id-card/{idCardNumber}")
    @PreAuthorize("hasAuthority('CITIZEN:READ')")
    public ResponseEntity<ApiResponse<CitizenDetailResponse>> getByIdCardNumber(
            @PathVariable String idCardNumber) {
        return ResponseEntity.ok(ApiResponse.ok(citizenService.getByIdCardNumber(idCardNumber)));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('CITIZEN:READ')")
    public ResponseEntity<ApiResponse<Page<CitizenSummaryResponse>>> search(
            @ModelAttribute SearchCitizenRequest request,
            @PageableDefault(size = 10, sort = "fullName") Pageable pageable) {

        Page<CitizenSummaryResponse> result = citizenService.search(request, pageable);

        return ResponseEntity.ok(ApiResponse.paged(
                result,
                result.getTotalElements(),
                result.getTotalPages(),
                result.getNumber(),
                result.getSize()
        ));
    }

    @PatchMapping("/{id}/mark-deceased")
    @PreAuthorize("hasAuthority('CITIZEN:UPDATE')")
    public ResponseEntity<ApiResponse<Void>> markAsDeceased(
            @PathVariable Long id,
            @Valid @RequestBody DeceasedRequest request) {

        citizenService.markAsDeceased(id, request.getReason());
        return ResponseEntity.ok(ApiResponse.ok(null, "Deceased status updated successfully"));
    }
}