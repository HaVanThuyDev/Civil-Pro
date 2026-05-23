package vn.civilpro.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ROLES") // Tên bảng HOA
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "ROLE_CODE", nullable = false, unique = true, length = 50)
    private String roleCode;

    @Column(name = "ROLE_NAME", nullable = false, length = 255)
    private String roleName;

    @Column(name = "DESCRIPTION", columnDefinition = "TEXT")
    private String description;

    @Column(name = "MANAGEMENT_LEVEL")
    private Integer managementLevel;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "ROLE_PERMISSIONS", // Bảng trung gian viết HOA
            joinColumns = @JoinColumn(
                    name = "ROLE_ID", // Khóa ngoại trỏ tới bảng ROLES
                    foreignKey = @ForeignKey(name = "FK_RP_ROLE")
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "PERMISSION_ID", // Khóa ngoại trỏ tới bảng PERMISSIONS
                    foreignKey = @ForeignKey(name = "FK_RP_PERMISSION")
            )
    )
    @Builder.Default
    private Set<Permission> permissions = new HashSet<>();
}