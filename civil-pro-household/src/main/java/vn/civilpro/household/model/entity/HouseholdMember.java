package vn.civilpro.household.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "HOUSEHOLD_MEMBER", indexes = {
        @Index(name = "IDX_HM_CITIZEN_ID", columnList = "CITIZEN_ID"),
        @Index(name = "IDX_HM_HH_ID", columnList = "HOUSEHOLD_ID"),
        @Index(name = "IDX_HM_STATUS", columnList = "STATUS")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HouseholdMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HOUSEHOLD_ID", nullable = false)
    private Household household;

    @Column(name = "CITIZEN_ID", nullable = false)
    private Long citizenId;

    @Column(name = "FULL_NAME", length = 255)
    private String fullName;

    @Column(name = "RELATIONSHIP_WITH_HEAD", nullable = false, length = 100)
    private String relationshipWithHead;

    @Column(name = "JOIN_DATE", nullable = false)
    private LocalDate joinDate;

    @Column(name = "LEAVE_DATE")
    private LocalDate leaveDate;

    @Column(name = "LEAVE_REASON", length = 500)
    private String leaveReason;

    @Column(name = "STATUS", nullable = false)
    @Builder.Default
    private Integer status = 1;

    @CreatedDate
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
