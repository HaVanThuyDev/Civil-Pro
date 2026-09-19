package vn.civilpro.pay.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.civilpro.pay.model.enums.PaymentMethod;
import vn.civilpro.pay.model.enums.TaxCategory;
import vn.civilpro.pay.model.enums.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEvent {

    private String eventType;
    private String orderCode;
    private String txnReference;
    private TaxCategory taxCategory;
    private String taxpayerNationalId;
    private String taxpayerName;
    private String taxCode;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private TransactionStatus status;
    private String receiptNumber;
    private LocalDateTime timestamp;
}
