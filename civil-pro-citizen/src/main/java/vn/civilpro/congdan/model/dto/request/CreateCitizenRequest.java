package vn.civilpro.congdan.dto.request;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

/**
 * REQUEST DTO: CREATE NEW CITIZEN
 * Input validation layer before reaching the Service layer
 */
@Data
public class CreateCitizenRequest {

    @NotBlank(message = "Full name is required")
    @Size(max = 255, message = "Full name must not exceed 255 characters")
    private String fullName;

    @NotNull(message = "Gender is required")
    @Min(value = 1, message = "Invalid gender value")
    @Max(value = 3, message = "Invalid gender value")
    private Integer gender;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be a past date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @Size(max = 500, message = "Place of birth must not exceed 500 characters")
    private String placeOfBirth;

    @Size(max = 50, message = "Ethnicity must not exceed 50 characters")
    private String ethnicity;

    @Size(max = 50, message = "Religion must not exceed 50 characters")
    private String religion;

    @Pattern(regexp = "^[0-9]{12}$", message = "ID card number must be exactly 12 digits")
    private String idCardNumber;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate idCardIssuedDate;

    @Size(max = 255, message = "ID card issued place must not exceed 255 characters")
    private String idCardIssuedPlace;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate idCardExpiryDate;

    @Pattern(regexp = "^(0|\\+84)[0-9]{8,10}$", message = "Invalid phone number format")
    private String phoneNumber;

    @Email(message = "Invalid email address format")
    private String email;

    @NotBlank(message = "Permanent area code is required")
    private String permanentAreaCode;

    @NotBlank(message = "Permanent address is required")
    @Size(max = 500, message = "Permanent address must not exceed 500 characters")
    private String permanentAddress;

    @Size(max = 100, message = "Occupation must not exceed 100 characters")
    private String occupation;

    @Size(max = 50, message = "Education level must not exceed 50 characters")
    private String educationLevel;

    @Size(max = 255, message = "Workplace must not exceed 255 characters")
    private String workplace;

    private String citizenType;
}