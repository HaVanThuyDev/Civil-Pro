package vn.civilpro.sync.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "SYNC_ERROR_RECORD", indexes = {
        @Index(name = "IDX_SER_SESSION", columnList = "SESSION_ID"),
        @Index(name = "IDX_SER_PROCESSED", columnList = "PROCESSED")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyncErrorRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "SESSION_ID", nullable = false)
    private Long sessionId;

    @Column(name = "NATIONAL_ID", length = 30)
    private String nationalId;

    @Column(name = "ERROR_REASON", columnDefinition = "TEXT")
    private String errorReason;

    @Column(name = "ERROR_CODE", length = 50)
    private String errorCode;

    @Column(name = "PROCESSED", nullable = false)
    @Builder.Default
    private Boolean processed = false;

    @Column(name = "PROCESSED_BY", length = 100)
    private String processedBy;

    @Column(name = "NOTE", columnDefinition = "TEXT")
    private String note;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
