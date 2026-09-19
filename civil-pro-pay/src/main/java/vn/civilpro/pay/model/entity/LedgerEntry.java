package vn.civilpro.pay.model.entity;

import jakarta.persistence.*;
import lombok.*;
import vn.civilpro.pay.model.enums.LedgerEntryType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "PAYMENT_LEDGER_ENTRY", indexes = {
        @Index(name = "IDX_LE_TXN_ID", columnList = "TRANSACTION_ID"),
        @Index(name = "IDX_LE_ACC", columnList = "ACCOUNT_NUMBER")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LedgerEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TRANSACTION_ID", nullable = false)
    private Long transactionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "ENTRY_TYPE", nullable = false, length = 20)
    private LedgerEntryType entryType; // DEBIT or CREDIT

    @Column(name = "ACCOUNT_NUMBER", nullable = false, length = 64)
    private String accountNumber;

    @Column(name = "ACCOUNT_NAME", nullable = false, length = 255)
    private String accountName;

    @Column(name = "AMOUNT", nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "BALANCE_AFTER", precision = 18, scale = 2)
    private BigDecimal balanceAfter;

    @Column(name = "DESCRIPTION", length = 500)
    private String description;

    @Column(name = "POSTED_AT", nullable = false)
    private LocalDateTime postedAt;

    @PrePersist
    public void prePersist() {
        if (postedAt == null) {
            postedAt = LocalDateTime.now();
        }
    }
}
