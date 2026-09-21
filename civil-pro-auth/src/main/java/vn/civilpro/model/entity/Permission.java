package vn.civilpro.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(
        name = "PERMISSIONS",
        indexes = {
                @Index(name = "IDX_PERMISSION_GROUP", columnList = "PERMISSION_GROUP")
        }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Permission implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "PERMISSION_CODE", nullable = false, unique = true, length = 100)
    private String permissionCode;

    @Column(name = "PERMISSION_NAME", nullable = false, length = 255)
    private String permissionName;

    @Column(name = "PERMISSION_GROUP", length = 100)
    private String permissionGroup;

    @Column(name = "DESCRIPTION", columnDefinition = "TEXT")
    private String description;
}