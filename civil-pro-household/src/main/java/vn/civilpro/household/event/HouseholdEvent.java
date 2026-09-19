package vn.civilpro.household.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdEvent {

    private String eventType; // HOUSEHOLD_CREATED, HOUSEHOLD_MEMBER_ADDED, HOUSEHOLD_SPLIT
    private Long householdId;
    private String householdCode;
    private Long citizenId;
    private String citizenFullName;
    private String areaCode;
    private String relationshipWithHead;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Builder.Default
    private Instant occurredAt = Instant.now();
}
