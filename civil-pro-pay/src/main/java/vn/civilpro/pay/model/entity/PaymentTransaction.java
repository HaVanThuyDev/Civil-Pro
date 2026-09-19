package vn.civilpro.pay.model.entity;

import jakarta.persistence.*;
import lombok.*;
import vn.civilpro.pay.model.enums.PaymentMethod;
import vn.civilpro.pay.model.enums.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "PAYMENT_TRANSACTION", indexes = {
        @Index(name = "IDX_PT_REF", columnList = "TXN_REFERENCE", unique = true),
        @Index(name = "IDX_PT_ORDER_ID", columnList = "ORDER_ID"),
        @Index(name = "IDX_PT_STATUS", columnList = "STATUS")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TXN_REFERENCE", nullable = false, unique = true, length = 64)
    private String txnReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID", nullable = false)
    private PaymentOrder order;

    @Enumerated(EnumType.STRING)
    @Column(name = "PAYMENT_METHOD", nullable = false, length = 50)
    private PaymentMethod paymentMethod;

    @Convert(converter = vn.civilpro.pay.security.Aes256EncryptConverter.class)
    @Column(name = "DEBIT_ACCOUNT", nullable = false, length = 100)
    private String debitAccount;

    @Column(name = "CREDIT_ACCOUNT", nullable = false, length = 64)
    @Builder.Default
    private String creditAccount = "VN-STATE-TREASURY-8888";

    @Column(name = "AMOUNT", nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "FEE", precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal fee = BigDecimal.ZERO;

    @Column(name = "CHECKSUM", length = 128)
    private String checksum;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 30)
    @Builder.Default
    private TransactionStatus status = TransactionStatus.PENDING;

    @Column(name = "GATEWAY_TXN_ID", length = 100)
    private String gatewayTxnId;

    @Column(name = "FAILURE_REASON", columnDefinition = "TEXT")
    private String failureReason;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "COMPLETED_AT")
    private LocalDateTime completedAt;

    @Version
    @Builder.Default
    private Integer version = 0;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
