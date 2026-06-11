package vn.civilpro.model.projection;

import java.time.LocalDateTime;

public interface UserView {
    Long getId();
    String getUsername();
    String getFullName();
    String getEmail();
    String getPhoneNumber();
    String getAdministrativeUnitCode();
    String getAvatarUrl();
    Integer getStatus();
    LocalDateTime getCreatedAt();
    LocalDateTime getLastLoginAt();
}