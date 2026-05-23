package vn.civilpro.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "NOTIFICATIONS",
        indexes = {
                @Index(name = "IDX_NOTIF_RECIPIENT", columnList = "RECIPIENT_ID"),
                @Index(name = "IDX_NOTIF_IS_READ",    columnList = "IS_READ"),
                @Index(name = "IDX_NOTIF_CREATED_AT", columnList = "CREATED_AT"),
                @Index(name = "IDX_NOTIF_TYPE",       columnList = "TYPE")
        }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "RECIPIENT_ID",
            foreignKey = @ForeignKey(name = "FK_NOTIF_USER")
    )
    private User recipient;

    @Column(name = "TITLE", nullable = false, length = 500)
    private String title;

    @Column(name = "CONTENT", columnDefinition = "TEXT")
    private String content;

    @Column(name = "TYPE", nullable = false, length = 50)
    private String type;

    @Column(name = "IS_READ", nullable = false)
    @Builder.Default
    private Boolean isRead = false;

    @Column(name = "READ_AT")
    private LocalDateTime readAt;

    @Column(name = "REDIRECT_URL", length = 500)
    private String redirectUrl;

    @CreatedDate
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Logic nghiệp vụ: Đánh dấu đã đọc
     */
    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }
}