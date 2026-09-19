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
public class AddMemberRequest {

    @NotNull(message = "Citizen ID is required")
    private Long citizenId;

    @NotBlank(message = "Relationship with head is required")
    private String relationshipWithHead;
}
