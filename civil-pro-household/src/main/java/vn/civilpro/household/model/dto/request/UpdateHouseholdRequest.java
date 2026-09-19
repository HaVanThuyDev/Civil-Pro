package vn.civilpro.household.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateHouseholdRequest {

    private String householdBookNumber;
    private String fullAddress;
    private String householdType;
    private String status;
    private String notes;
}
