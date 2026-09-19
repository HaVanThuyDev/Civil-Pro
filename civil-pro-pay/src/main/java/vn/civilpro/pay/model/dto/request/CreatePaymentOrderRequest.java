package vn.civilpro.pay.model.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.civilpro.pay.model.enums.TaxCategory;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentOrderRequest {

    @NotNull(message = "Tax category is required")
    private TaxCategory taxCategory;

    @NotBlank(message = "Taxpayer national ID is required")
    private String taxpayerNationalId;

    @NotBlank(message = "Taxpayer name is required")
    private String taxpayerName;

    private String taxCode;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1000.00", message = "Minimum payment amount is 1,000 VND")
    private BigDecimal amount;

    private String fiscalPeriod;

    private String notes;
}
