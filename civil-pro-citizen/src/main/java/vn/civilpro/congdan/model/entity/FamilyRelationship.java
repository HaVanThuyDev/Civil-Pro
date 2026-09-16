package vn.civilpro.congdan.entity;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
        name = "family_relationships",
        indexes = {
                @Index(name = "idx_citizen_id", columnList = "CITIZEN_ID"),
                @Index(name = "idx_related_citizen_id", columnList = "RELATED_CITIZEN_ID")
        }
)
public class FamilyRelationship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "CITIZEN_ID", nullable = false)
    private Long citizenId;

    @Column(name = "RELATED_CITIZEN_ID", nullable = false)
    private Long relatedCitizenId;

    @Column(name = "RELATIONSHIP_TYPE", length = 50, nullable = false)
    private String relationshipType;

    @CreatedDate
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}