package vn.civilpro.congdan.dto.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;


@Data
@Builder
public class CitizenSummaryResponse {

    private Long id;
    private String citizenCode;
    private String fullName;
    private String genderLabel;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    private Integer age;
    private String idCardNumber;
    private String permanentAddress;
    private String occupation;
    private String citizenType;
    private Integer status;
    private String statusLabel;
}