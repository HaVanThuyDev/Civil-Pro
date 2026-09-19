package vn.civilpro.household.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "HOUSEHOLD", indexes = {
        @Index(name = "IDX_HH_AREA_CODE", columnList = "AREA_CODE"),
        @Index(name = "IDX_HH_STATUS", columnList = "STATUS"),
        @Index(name = "IDX_HH_HEAD_ID", columnList = "HEAD_CITIZEN_ID")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Household {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "HOUSEHOLD_CODE", nullable = false, unique = true, length = 30)
    private String householdCode;

    @Column(name = "HOUSEHOLD_BOOK_NUMBER", length = 50)
    private String householdBookNumber;

    @Column(name = "HEAD_CITIZEN_ID", nullable = false)
    private Long headCitizenId;

    @Column(name = "HEAD_FULL_NAME", nullable = false, length = 255)
    private String headFullName;

    @Column(name = "AREA_CODE", nullable = false, length = 20)
    private String areaCode;

    @Column(name = "FULL_ADDRESS", nullable = false, length = 500)
    private String fullAddress;

    @Column(name = "MEMBER_COUNT")
    @Builder.Default
    private Integer memberCount = 0;

    @Column(name = "HOUSEHOLD_TYPE", length = 50)
    @Builder.Default
    private String householdType = "NORMAL";

    @Column(name = "REGISTRATION_DATE", nullable = false)
    private LocalDate registrationDate;

    @Column(name = "STATUS", nullable = false, length = 20)
    @Builder.Default
    private String status = "ACTIVE";

    @Column(name = "NOTES", columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "household", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<HouseholdMember> members = new ArrayList<>();

    @CreatedDate
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(name = "CREATED_BY", length = 100, updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "UPDATED_BY", length = 100)
    private String updatedBy;

    @Version
    @Builder.Default
    private Integer version = 0;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
