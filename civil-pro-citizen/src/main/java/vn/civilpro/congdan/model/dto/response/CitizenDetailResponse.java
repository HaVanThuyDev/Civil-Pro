package vn.civilpro.congdan.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class CitizenDetailResponse {

    private Long id;
    private String citizenCode;
    private String fullName;
    private String fullNameAscii;
    private String genderLabel;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    private Integer age;
    private String placeOfBirth;
    private String ethnicity;
    private String religion;
    private String nationality;


    private String idCardNumber;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate idCardIssuedDate;

    private String idCardIssuedPlace;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate idCardExpiryDate;

    private boolean idCardExpiringSoon;

    private String passportNumber;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate passportExpiryDate;

    // Contact
    private String phoneNumber;
    private String email;

    // Residency
    private String permanentAreaCode;
    private String permanentAddress;
    private String temporaryAreaCode;
    private String temporaryAddress;

    // Occupation & Education
    private String occupation;
    private String educationLevel;
    private String workplace;

    // Classification & Household
    private String citizenType;
    private Boolean isHouseholdHead;
    private Long householdId;

    // Status
    private Integer status;
    private String statusLabel;             // "Active" / "Deceased" / "Emigrated"

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deathDate;

    private String statusReason;

    // Audit
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private String createdBy;
    private String updatedBy;
}