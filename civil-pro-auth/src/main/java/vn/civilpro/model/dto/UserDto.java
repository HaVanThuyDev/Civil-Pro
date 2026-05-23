package vn.civilpro.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {

    private Long    id;
    private String  username;
    private String  fullName;
    private String  email;
    private String  phoneNumber;
    private String  administrativeUnitCode;
    private String  avatarUrl;
    private Integer status;
    private String  statusLabel;

    private Set<String> roles;
    private Set<String> authorities;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginAt;
}
