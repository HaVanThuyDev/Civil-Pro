package vn.civilpro.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "LOGIN_ATTEMPTS",
        indexes = {
                @Index(name = "IDX_LOGIN_USERNAME", columnList = "USERNAME"),
                @Index(name = "IDX_LOGIN_LAST_ATTEMPT", columnList = "LAST_ATTEMPT_AT")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "USERNAME", nullable = false, length = 100, unique = true)
    private String username;

    @Column(name = "ATTEMPT_COUNT", nullable = false)
    @Builder.Default
    private Integer attemptCount = 0;

    @Column(name = "IP_ADDRESS", length = 45)
    private String ipAddress;

    @Column(name = "LAST_ATTEMPT_AT", nullable = false)
    @Builder.Default
    private LocalDateTime lastAttemptAt = LocalDateTime.now();

    @Column(name = "LOCKED_UNTIL")
    private LocalDateTime lockedUntil;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Kiểm tra có đang bị khóa không
    public boolean isLocked() {
        return lockedUntil != null && LocalDateTime.now().isBefore(lockedUntil);
    }

    // Tăng số lần thất bại
    public void incrementAttempt() {
        this.attemptCount = (this.attemptCount == null ? 0 : this.attemptCount) + 1;
        this.lastAttemptAt = LocalDateTime.now();
    }

    // Reset về 0
    public void reset() {
        this.attemptCount = 0;
        this.lockedUntil = null;
        this.lastAttemptAt = LocalDateTime.now();
    }

    // Khóa tài khoản trong X phút
    public void lock(int minutes) {
        this.lockedUntil = LocalDateTime.now().plusMinutes(minutes);
    }
}