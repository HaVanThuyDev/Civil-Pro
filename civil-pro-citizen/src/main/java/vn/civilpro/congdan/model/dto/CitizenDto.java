package vn.civilpro.congdan.model.dto;


import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CitizenDto {

    private Long id;

    private String citizenCode;

    private String fullName;

    private String fullNameAscii;

    private Integer gender; // 1=Male, 2=Female

    private LocalDate dateOfBirth;

    private String placeOfBirth;

    private String ethnicity;

    private String religion;

    private String nationality;

    // ---- Identification Documents ----
    private String idCardNumber;

    private LocalDate idCardIssuedDate;

    private String idCardIssuedPlace;

    private LocalDate idCardExpiryDate;

    private String passportNumber;

    private LocalDate passportExpiryDate;

    // ---- Contact Details ----
    private String phoneNumber;

    private String email;

    // ---- Residency ----
    private String permanentAreaCode;

    private String permanentAddress;

    private String temporaryAreaCode;

    private String temporaryAddress;

    // ---- Occupation & Education ----
    private String occupation;

    private String educationLevel;

    private String workplace;

    // ---- Classification & Household ----
    private String citizenType;

    private Integer isHouseholdHead;

    private Long householdId;

    // ---- Status ----
    private Integer status;

    private LocalDate deathDate;

    private String statusReason;
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdBy;

    private String updatedBy;

}