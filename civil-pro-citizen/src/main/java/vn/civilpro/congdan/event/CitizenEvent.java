package vn.civilpro.congdan.event;


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
public class CitizenEvent {

    private String eventType;
    private Long citizenId;
    private String citizenCode;
    private String fullName;
    private String areaCode;
    private Integer status;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant occurredAt;
}