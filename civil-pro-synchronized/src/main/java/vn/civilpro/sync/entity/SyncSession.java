package vn.civilpro.sync.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "SYNC_SESSION", indexes = {
        @Index(name = "IDX_SS_STATUS", columnList = "STATUS"),
        @Index(name = "IDX_SS_START_TIME", columnList = "START_TIME")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyncSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "SESSION_CODE", nullable = false, unique = true, length = 50)
    private String sessionCode;

    @Column(name = "SYNC_TYPE", nullable = false, length = 50)
    private String syncType; // FULL, INCREMENTAL, RESTORE

    @Column(name = "START_TIME", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "END_TIME")
    private LocalDateTime endTime;

    @Column(name = "STATUS", nullable = false, length = 20)
    @Builder.Default
    private String status = "RUNNING";

    @Column(name = "TOTAL_RECORDS")
    @Builder.Default
    private Integer totalRecords = 0;

    @Column(name = "PROCESSED_RECORDS")
    @Builder.Default
    private Integer processedRecords = 0;

    @Column(name = "SUCCESS_RECORDS")
    @Builder.Default
    private Integer successRecords = 0;

    @Column(name = "FAILED_RECORDS")
    @Builder.Default
    private Integer failedRecords = 0;

    @Column(name = "COMPLETION_PERCENTAGE", precision = 5)
    @Builder.Default
    private Double completionPercentage = 0.0;

    @Column(name = "ERROR_CODE", length = 50)
    private String errorCode;

    @Column(name = "ERROR_MESSAGE", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "RETRY_COUNT")
    @Builder.Default
    private Integer retryCount = 0;

    @PrePersist
    public void prePersist() {
        if (startTime == null) {
            startTime = LocalDateTime.now();
        }
    }
}
