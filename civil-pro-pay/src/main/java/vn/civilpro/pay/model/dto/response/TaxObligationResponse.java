package vn.civilpro.pay.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.civilpro.pay.model.enums.TaxCategory;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxObligationResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private String obligationId;
    private TaxCategory taxCategory;
    private String categoryDescription;
    private String description;
    private BigDecimal amountDue;
    private String dueDate;
    private String fiscalPeriod;
    private String taxCode;
}
