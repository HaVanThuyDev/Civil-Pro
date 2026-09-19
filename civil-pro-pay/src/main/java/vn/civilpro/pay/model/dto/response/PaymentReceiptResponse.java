package vn.civilpro.pay.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentReceiptResponse {

    private String receiptNumber;
    private PaymentOrderResponse order;
    private PaymentTransactionResponse transaction;
    private List<LedgerEntryResponse> ledgerEntries;
    private String digitalSignature;
    private String qrCodePayload;
    private LocalDateTime issuedAt;
    private String treasuryConfirmation;
}
