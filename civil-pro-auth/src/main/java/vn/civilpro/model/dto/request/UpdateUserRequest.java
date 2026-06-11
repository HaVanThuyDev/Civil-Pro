package vn.civilpro.model.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    @Size(max = 100)
    private String fullName;

    @Email(message = "Email không hợp lệ")
    private String email;

    private String  phoneNumber;
    private String  administrativeUnitCode;
    private String  avatarUrl;
    private Integer status;
    private Set<Long> roleIds;
}