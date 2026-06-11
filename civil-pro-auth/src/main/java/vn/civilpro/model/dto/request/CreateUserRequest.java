package vn.civilpro.model.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {

    @NotBlank(message = "Username không được để trống")
    @Size(min = 4, max = 50)
    private String username;

    @NotBlank(message = "Password không được để trống")
    @Size(min = 6)
    private String password;

    @NotBlank(message = "Full name không được để trống")
    private String fullName;

    @Email(message = "Email không hợp lệ")
    private String email;

    private String phoneNumber;
    private String administrativeUnitCode;
    private Set<Long> roleIds;
    private String createdBy;
}