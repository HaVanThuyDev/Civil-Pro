package vn.civilpro.pay.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.civilpro.pay.model.enums.LedgerEntryType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LedgerEntryResponse {

    private Long id;
    private Long transactionId;
    private LedgerEntryType entryType;
    private String accountNumber;
    private String accountName;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private String description;
    private LocalDateTime postedAt;
}
