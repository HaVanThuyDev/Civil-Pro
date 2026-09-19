package vn.civilpro.pay.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.civilpro.pay.model.enums.OrderStatus;
import vn.civilpro.pay.model.enums.TaxCategory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOrderResponse {

    private Long id;
    private String orderCode;
    private TaxCategory taxCategory;
    private String taxCategoryDescription;
    private String taxpayerNationalId;
    private String taxpayerName;
    private String taxCode;
    private BigDecimal amount;
    private String currency;
    private String fiscalPeriod;
    private OrderStatus status;
    private String idempotencyKey;
    private String receiptNumber;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}
