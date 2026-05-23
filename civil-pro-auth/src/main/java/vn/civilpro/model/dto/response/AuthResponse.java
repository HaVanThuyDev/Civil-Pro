package vn.civilpro.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import java.util.Set;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long   expiresIn;

    private Long   userId;
    private String username;
    private String fullName;
    private String administrativeUnitCode;

    private Set<String> authorities;
    private Set<String> roles;
}
