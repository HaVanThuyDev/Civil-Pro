package vn.civilpro.congdan.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeathRegistrationRequest {

    @NotBlank(message = "Reason for death registration is required")
    private String reason;
}