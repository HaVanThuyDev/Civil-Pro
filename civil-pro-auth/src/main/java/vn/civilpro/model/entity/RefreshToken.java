package vn.civilpro.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "REFRESH_TOKENS",
        indexes = {
                @Index(name = "IDX_RT_USER",        columnList = "USER_ID"),
                @Index(name = "IDX_RT_HASH",        columnList = "TOKEN_HASH"),
                @Index(name = "IDX_RT_EXPIRY_AT",   columnList = "EXPIRY_AT")
        }
)
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "USER_ID", // Mapping HOA
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_RT_USER")
    )
    private User user;

    @Column(name = "TOKEN_HASH", nullable = false, length = 255)
    private String tokenHash;

    @Column(name = "EXPIRY_AT", nullable = false) // Đổi từ DATE sang AT cho chuẩn thời điểm
    private LocalDateTime expiryAt;

    @Column(name = "IP_ADDRESS", length = 45)
    private String ipAddress;

    @Column(name = "USER_AGENT", length = 500)
    private String userAgent;

    @Column(name = "IS_REVOKED", nullable = false)
    @Builder.Default
    private Boolean isRevoked = false;

    @CreatedDate
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Kiểm tra Token còn hiệu lực hay không
     */
    public boolean isValid() {
        return !Boolean.TRUE.equals(this.isRevoked)
                && this.expiryAt.isAfter(LocalDateTime.now());
    }

    /**
     * Thu hồi Token
     */
    public void revoke() {
        this.isRevoked = true;
    }
}