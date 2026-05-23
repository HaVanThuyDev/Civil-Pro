package vn.civilpro.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "USERS",
        indexes = {
                @Index(name = "IDX_USER_ADMIN_UNIT", columnList = "ADMIN_UNIT_CODE"),
                @Index(name = "IDX_USER_STATUS",     columnList = "STATUS")
        }
)
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "USERNAME", nullable = false, unique = true, length = 100)
    private String username;

    @Column(name = "PASSWORD_HASH", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "FULL_NAME", nullable = false, length = 255)
    private String fullName;

    @Column(name = "EMAIL", unique = true, length = 255)
    private String email;

    @Column(name = "PHONE_NUMBER", length = 15)
    private String phoneNumber;

    @Column(name = "ADMIN_UNIT_CODE", length = 20)
    private String administrativeUnitCode;

    @Column(name = "AVATAR_URL", length = 500)
    private String avatarUrl;

    @Column(name = "STATUS", nullable = false)
    @Builder.Default
    private Integer status = 1;

    @Column(name = "LAST_LOGIN_AT")
    private LocalDateTime lastLoginAt;

    @Column(name = "FAILED_LOGIN_COUNT")
    @Builder.Default
    private Integer failedLoginCount = 0;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "USER_ROLES",
            joinColumns = @JoinColumn(
                    name = "USER_ID",
                    foreignKey = @ForeignKey(name = "FK_UR_USER")
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "ROLE_ID",
                    foreignKey = @ForeignKey(name = "FK_UR_ROLE")
            )
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @CreatedDate
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;
}