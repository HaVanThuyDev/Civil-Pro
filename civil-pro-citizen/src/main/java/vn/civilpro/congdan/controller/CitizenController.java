package vn.civilpro.congdan.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.civilpro.congdan.common.ApiResponse;
import vn.civilpro.congdan.model.dto.request.CreateCitizenRequest;
import vn.civilpro.congdan.model.dto.request.DeathRegistrationRequest;
import vn.civilpro.congdan.model.dto.request.SearchCitizenRequest;
import vn.civilpro.congdan.model.dto.request.UpdateCitizenRequest;
import vn.civilpro.congdan.model.dto.response.CitizenDetailResponse;
import vn.civilpro.congdan.model.dto.response.CitizenSummaryResponse;
import vn.civilpro.congdan.model.dto.response.PagedResult;
import vn.civilpro.congdan.service.CitizenService;

@RestController
@RequestMapping("/api/citizen")
@RequiredArgsConstructor
public class CitizenController {

    private final CitizenService citizenService;

    @GetMapping
    public ResponseEntity<PagedResult<CitizenSummaryResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (size > 100) {
            size = 100;
        }

        return ResponseEntity.ok(citizenService.getAll(page, size));
    }

    @PostMapping({"", "/create"})
//    @PreAuthorize("hasAuthority('CITIZEN:CREATE')")
    public ResponseEntity<?> create(@Valid @RequestBody CreateCitizenRequest request) {
        citizenService.create(request);
        return ResponseEntity.ok("create success");
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasAuthority('CITIZEN:UPDATE')")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody UpdateCitizenRequest request) {
        citizenService.update(id, request);
        return ResponseEntity.ok("create success");
    }

    @GetMapping({"/details{id}", "/details/{id}", "/{id}"})
//    @PreAuthorize("hasAuthority('CITIZEN:READ')")
    public ResponseEntity<ApiResponse<CitizenDetailResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(citizenService.getById(id), "Get citizen details successfully"));
    }

    @GetMapping("/id-card/{idCardNumber}")
//    @PreAuthorize("hasAuthority('CITIZEN:READ')")
    public ResponseEntity<ApiResponse<CitizenDetailResponse>> getByIdCardNumber(
            @PathVariable String idCardNumber) {
        return ResponseEntity.ok(ApiResponse.ok(citizenService.getByIdCardNumber(idCardNumber)));
    }

    @GetMapping("/search")
//    @PreAuthorize("hasAuthority('CITIZEN:READ')")
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
//    @PreAuthorize("hasAuthority('CITIZEN:UPDATE')")
    public ResponseEntity<ApiResponse<Void>> markAsDeceased(@PathVariable Long id, @Valid @RequestBody DeathRegistrationRequest request) {
        citizenService.markAsDeceased(id, request.getReason());
        return ResponseEntity.ok(ApiResponse.ok(null, "Deceased status updated successfully"));
    }
}