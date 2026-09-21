package vn.civilpro.household.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdDetailResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String householdCode;
    private String householdBookNumber;
    private Long headCitizenId;
    private String headFullName;
    private String areaCode;
    private String fullAddress;
    private Integer memberCount;
    private String householdType;
    private LocalDate registrationDate;
    private String status;
    private String notes;
    private List<HouseholdMemberResponse> members;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
