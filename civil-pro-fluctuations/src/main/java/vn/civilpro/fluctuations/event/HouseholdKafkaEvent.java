package vn.civilpro.fluctuations.event;

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
public class HouseholdKafkaEvent {

    private String eventType;
    private Long householdId;
    private String householdCode;
    private Long citizenId;
    private String citizenFullName;
    private String areaCode;
    private String relationshipWithHead;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant occurredAt;
}
