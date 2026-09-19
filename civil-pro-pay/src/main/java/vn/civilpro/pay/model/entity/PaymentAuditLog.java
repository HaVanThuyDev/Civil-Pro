package vn.civilpro.pay.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "PAYMENT_AUDIT_LOG", indexes = {
        @Index(name = "IDX_PAL_ENTITY", columnList = "ENTITY_TYPE, ENTITY_ID"),
        @Index(name = "IDX_PAL_TIME", columnList = "OCCURRED_AT")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ENTITY_TYPE", nullable = false, length = 50)
    private String entityType;

    @Column(name = "ENTITY_ID", nullable = false)
    private Long entityId;

    @Column(name = "ACTION", nullable = false, length = 50)
    private String action;

    @Column(name = "PREVIOUS_STATE", length = 50)
    private String previousState;

    @Column(name = "NEW_STATE", length = 50)
    private String newState;

    @Column(name = "PERFORMED_BY", length = 100)
    private String performedBy;

    @Column(name = "CLIENT_IP", length = 50)
    private String clientIp;

    @Column(name = "OCCURRED_AT", nullable = false)
    private LocalDateTime occurredAt;

    @PrePersist
    public void prePersist() {
        if (occurredAt == null) {
            occurredAt = LocalDateTime.now();
        }
    }
}
