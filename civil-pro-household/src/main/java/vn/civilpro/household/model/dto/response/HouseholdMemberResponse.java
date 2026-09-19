package vn.civilpro.household.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdMemberResponse {

    private Long id;
    private Long citizenId;
    private String fullName;
    private String relationshipWithHead;
    private LocalDate joinDate;
    private LocalDate leaveDate;
    private String leaveReason;
    private Integer status;
}
