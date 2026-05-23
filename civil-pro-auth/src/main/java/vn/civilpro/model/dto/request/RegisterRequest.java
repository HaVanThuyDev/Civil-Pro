package vn.civilpro.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class RegisterRequest {

    @NotBlank(message = "Username cannot be empty")
    @Size(min = 4, max = 50)
    private String username;

    @NotBlank(message = "Email cannot be empty")
    @Size(min = 9, max = 100)
    private String email;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, max = 100)
    private String password;

    @NotBlank(message = "Full name cannot be empty")
    private String fullName;

    private String administrativeUnitCode;

    private Set<String> roleCodes;
}
