package vn.civilpro.pay.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.civilpro.pay.model.enums.PaymentMethod;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessPaymentRequest {

    @NotBlank(message = "Order code is required")
    private String orderCode;

    @NotBlank(message = "Idempotency key is required to prevent double charging")
    private String idempotencyKey;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    @NotBlank(message = "Debit account is required")
    private String debitAccount;

    private String authToken;
}
