package vn.civilpro.pay.model.entity;

import jakarta.persistence.*;
import lombok.*;
import vn.civilpro.pay.model.enums.OrderStatus;
import vn.civilpro.pay.model.enums.TaxCategory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "PAYMENT_ORDER", indexes = {
        @Index(name = "IDX_PO_CODE", columnList = "ORDER_CODE", unique = true),
        @Index(name = "IDX_PO_IDEMPOTENCY", columnList = "IDEMPOTENCY_KEY", unique = true),
        @Index(name = "IDX_PO_TAXPAYER", columnList = "TAXPAYER_NATIONAL_ID"),
        @Index(name = "IDX_PO_STATUS", columnList = "STATUS"),
        @Index(name = "IDX_PO_TAX_CAT", columnList = "TAX_CATEGORY"),
        @Index(name = "IDX_PO_CREATED", columnList = "CREATED_AT")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ORDER_CODE", nullable = false, unique = true, length = 50)
    private String orderCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "TAX_CATEGORY", nullable = false, length = 50)
    private TaxCategory taxCategory;

    @Convert(converter = vn.civilpro.pay.security.Aes256EncryptConverter.class)
    @Column(name = "TAXPAYER_NATIONAL_ID", nullable = false, length = 100)
    private String taxpayerNationalId;

    @Column(name = "TAXPAYER_NAME", nullable = false, length = 255)
    private String taxpayerName;

    @Column(name = "TAX_CODE", length = 30)
    private String taxCode;

    @Column(name = "AMOUNT", nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "CURRENCY", nullable = false, length = 10)
    @Builder.Default
    private String currency = "VND";

    @Column(name = "FISCAL_PERIOD", length = 30)
    private String fiscalPeriod;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 30)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    @Column(name = "IDEMPOTENCY_KEY", unique = true, length = 100)
    private String idempotencyKey;

    @Column(name = "RECEIPT_NUMBER", length = 100)
    private String receiptNumber;

    @Column(name = "NOTES", columnDefinition = "TEXT")
    private String notes;

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
        if (currency == null) {
            currency = "VND";
        }
    }
}
