package vn.civilpro.household.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateHouseholdRequest {

    @NotNull(message = "Head citizen ID is required")
    private Long headCitizenId;

    @NotBlank(message = "Administrative area code is required")
    private String areaCode;

    @NotBlank(message = "Full address is required")
    private String fullAddress;

    private String householdBookNumber;

    @Builder.Default
    private String householdType = "NORMAL";

    private String notes;
}
