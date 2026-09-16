package vn.civilpro.congdan.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
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
        name = "citizen_change_logs",
        indexes = {
                @Index(name = "idx_citizen_id", columnList = "CITIZEN_ID"),
                @Index(name = "idx_changed_at", columnList = "CHANGED_AT")
        }
)
public class CitizenChangeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "CITIZEN_ID")
    private Long citizenId;

    @Column(name = "CHANGED_FIELD", length = 100)
    private String changedField;

    @Lob
    @Column(name = "OLD_VALUE", columnDefinition = "TEXT")
    private String oldValue;

    @Lob
    @Column(name = "NEW_VALUE", columnDefinition = "TEXT")
    private String newValue;

    @CreatedBy
    @Column(name = "CHANGED_BY", length = 100, updatable = false)
    private String changedBy;

    @CreatedDate
    @Column(name = "CHANGED_AT", nullable = false, updatable = false)
    private LocalDateTime changedAt;

    @Column(name = "IP_ADDRESS", length = 45)
    private String ipAddress;
}
