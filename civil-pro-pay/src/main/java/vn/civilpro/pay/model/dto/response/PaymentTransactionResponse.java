package vn.civilpro.pay.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.civilpro.pay.model.enums.PaymentMethod;
import vn.civilpro.pay.model.enums.TransactionStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransactionResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String txnReference;
    private Long orderId;
    private String orderCode;
    private PaymentMethod paymentMethod;
    private String debitAccount;
    private String creditAccount;
    private BigDecimal amount;
    private BigDecimal fee;
    private String checksum;
    private TransactionStatus status;
    private String gatewayTxnId;
    private String failureReason;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}
