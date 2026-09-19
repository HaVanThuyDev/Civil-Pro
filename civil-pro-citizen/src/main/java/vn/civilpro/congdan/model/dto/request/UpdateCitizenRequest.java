package vn.civilpro.congdan.model.dto.request;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateCitizenRequest {

    @Size(max = 255)
    private String fullName;

    @Min(1) @Max(3)
    private Integer gender;

    @Past
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @Size(max = 500)
    private String placeOfBirth;

    @Size(max = 50)
    private String ethnicity;

    @Size(max = 50)
    private String religion;

    @Pattern(regexp = "^[0-9]{12}$", message = "ID card number must be exactly 12 digits")
    private String idCardNumber;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate idCardIssuedDate;

    @Size(max = 255)
    private String idCardIssuedPlace;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate idCardExpiryDate;

    @Pattern(regexp = "^(0|\\+84)[0-9]{8,10}$", message = "Invalid phone number format")
    private String phoneNumber;

    @Email(message = "Invalid email address format")
    private String email;

    private String permanentAreaCode;

    @Size(max = 500)
    private String permanentAddress;

    @Size(max = 100)
    private String occupation;

    @Size(max = 50)
    private String educationLevel;

    @Size(max = 255)
    private String workplace;

    private String citizenType;
}
