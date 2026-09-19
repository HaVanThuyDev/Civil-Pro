package vn.civilpro.pay.mapper;

import org.springframework.stereotype.Component;
import vn.civil.grpc.payment.PaymentOrderInfo;
import vn.civil.grpc.payment.PaymentTransactionInfo;
import vn.civil.grpc.payment.TaxObligationInfo;
import vn.civilpro.pay.model.dto.response.LedgerEntryResponse;
import vn.civilpro.pay.model.dto.response.PaymentOrderResponse;
import vn.civilpro.pay.model.dto.response.PaymentTransactionResponse;
import vn.civilpro.pay.model.dto.response.TaxObligationResponse;
import vn.civilpro.pay.model.entity.LedgerEntry;
import vn.civilpro.pay.model.entity.PaymentOrder;
import vn.civilpro.pay.model.entity.PaymentTransaction;
import vn.civilpro.pay.model.enums.OrderStatus;
import vn.civilpro.pay.model.enums.TaxCategory;
import vn.civilpro.pay.model.enums.TransactionStatus;

import java.util.Collections;
import java.util.List;

@Component
public class PaymentMapper {

    public PaymentOrderResponse toOrderResponse(PaymentOrder order) {
        if (order == null) return null;
        return PaymentOrderResponse.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .taxCategory(order.getTaxCategory())
                .taxCategoryDescription(order.getTaxCategory() != null ? order.getTaxCategory().getDescription() : null)
                .taxpayerNationalId(order.getTaxpayerNationalId())
                .taxpayerName(order.getTaxpayerName())
                .taxCode(order.getTaxCode())
                .amount(order.getAmount())
                .currency(order.getCurrency())
                .fiscalPeriod(order.getFiscalPeriod())
                .status(order.getStatus())
                .idempotencyKey(order.getIdempotencyKey())
                .receiptNumber(order.getReceiptNumber())
                .notes(order.getNotes())
                .createdAt(order.getCreatedAt())
                .completedAt(order.getCompletedAt())
                .build();
    }

    public PaymentTransactionResponse toTransactionResponse(PaymentTransaction txn) {
        if (txn == null) return null;
        return PaymentTransactionResponse.builder()
                .id(txn.getId())
                .txnReference(txn.getTxnReference())
                .orderId(txn.getOrder() != null ? txn.getOrder().getId() : null)
                .orderCode(txn.getOrder() != null ? txn.getOrder().getOrderCode() : null)
                .paymentMethod(txn.getPaymentMethod())
                .debitAccount(txn.getDebitAccount())
                .creditAccount(txn.getCreditAccount())
                .amount(txn.getAmount())
                .fee(txn.getFee())
                .checksum(txn.getChecksum())
                .status(txn.getStatus())
                .gatewayTxnId(txn.getGatewayTxnId())
                .failureReason(txn.getFailureReason())
                .createdAt(txn.getCreatedAt())
                .completedAt(txn.getCompletedAt())
                .build();
    }

    public LedgerEntryResponse toLedgerEntryResponse(LedgerEntry entry) {
        if (entry == null) return null;
        return LedgerEntryResponse.builder()
                .id(entry.getId())
                .transactionId(entry.getTransactionId())
                .entryType(entry.getEntryType())
                .accountNumber(entry.getAccountNumber())
                .accountName(entry.getAccountName())
                .amount(entry.getAmount())
                .balanceAfter(entry.getBalanceAfter())
                .description(entry.getDescription())
                .postedAt(entry.getPostedAt())
                .build();
    }

    public List<LedgerEntryResponse> toLedgerEntryResponseList(List<LedgerEntry> entries) {
        if (entries == null) return Collections.emptyList();
        return entries.stream().map(this::toLedgerEntryResponse).toList();
    }

    public PaymentOrderInfo toProtoOrderInfo(PaymentOrder order) {
        if (order == null) return PaymentOrderInfo.getDefaultInstance();
        PaymentOrderInfo.Builder builder = PaymentOrderInfo.newBuilder()
                .setId(order.getId() != null ? order.getId() : 0L)
                .setOrderCode(order.getOrderCode() != null ? order.getOrderCode() : "")
                .setTaxCategory(toProtoTaxCategory(order.getTaxCategory()))
                .setTaxpayerNationalId(order.getTaxpayerNationalId() != null ? order.getTaxpayerNationalId() : "")
                .setTaxpayerName(order.getTaxpayerName() != null ? order.getTaxpayerName() : "")
                .setTaxCode(order.getTaxCode() != null ? order.getTaxCode() : "")
                .setAmount(order.getAmount() != null ? order.getAmount().doubleValue() : 0.0)
                .setCurrency(order.getCurrency() != null ? order.getCurrency() : "VND")
                .setFiscalPeriod(order.getFiscalPeriod() != null ? order.getFiscalPeriod() : "")
                .setStatus(toProtoOrderStatus(order.getStatus()))
                .setIdempotencyKey(order.getIdempotencyKey() != null ? order.getIdempotencyKey() : "")
                .setReceiptNumber(order.getReceiptNumber() != null ? order.getReceiptNumber() : "")
                .setCreatedAt(order.getCreatedAt() != null ? order.getCreatedAt().toString() : "");

        if (order.getCompletedAt() != null) {
            builder.setCompletedAt(order.getCompletedAt().toString());
        }
        return builder.build();
    }

    public PaymentTransactionInfo toProtoTransactionInfo(PaymentTransaction txn) {
        if (txn == null) return PaymentTransactionInfo.getDefaultInstance();
        PaymentTransactionInfo.Builder builder = PaymentTransactionInfo.newBuilder()
                .setId(txn.getId() != null ? txn.getId() : 0L)
                .setTxnReference(txn.getTxnReference() != null ? txn.getTxnReference() : "")
                .setOrderId(txn.getOrder() != null && txn.getOrder().getId() != null ? txn.getOrder().getId() : 0L)
                .setOrderCode(txn.getOrder() != null && txn.getOrder().getOrderCode() != null ? txn.getOrder().getOrderCode() : "")
                .setPaymentMethod(txn.getPaymentMethod() != null ? txn.getPaymentMethod().name() : "")
                .setDebitAccount(txn.getDebitAccount() != null ? txn.getDebitAccount() : "")
                .setCreditAccount(txn.getCreditAccount() != null ? txn.getCreditAccount() : "")
                .setAmount(txn.getAmount() != null ? txn.getAmount().doubleValue() : 0.0)
                .setFee(txn.getFee() != null ? txn.getFee().doubleValue() : 0.0)
                .setChecksum(txn.getChecksum() != null ? txn.getChecksum() : "")
                .setStatus(toProtoTxnStatus(txn.getStatus()))
                .setGatewayTxnId(txn.getGatewayTxnId() != null ? txn.getGatewayTxnId() : "");

        if (txn.getCompletedAt() != null) {
            builder.setCompletedAt(txn.getCompletedAt().toString());
        }
        return builder.build();
    }

    public TaxObligationInfo toProtoTaxObligationInfo(TaxObligationResponse dto) {
        if (dto == null) return TaxObligationInfo.getDefaultInstance();
        return TaxObligationInfo.newBuilder()
                .setObligationId(dto.getObligationId() != null ? dto.getObligationId() : "")
                .setTaxCategory(toProtoTaxCategory(dto.getTaxCategory()))
                .setDescription(dto.getDescription() != null ? dto.getDescription() : "")
                .setAmountDue(dto.getAmountDue() != null ? dto.getAmountDue().doubleValue() : 0.0)
                .setDueDate(dto.getDueDate() != null ? dto.getDueDate() : "")
                .setFiscalPeriod(dto.getFiscalPeriod() != null ? dto.getFiscalPeriod() : "")
                .setTaxCode(dto.getTaxCode() != null ? dto.getTaxCode() : "")
                .build();
    }

    public vn.civil.grpc.payment.TaxCategory toProtoTaxCategory(TaxCategory category) {
        if (category == null) return vn.civil.grpc.payment.TaxCategory.TAX_CATEGORY_UNSPECIFIED;
        try {
            return vn.civil.grpc.payment.TaxCategory.valueOf(category.name());
        } catch (IllegalArgumentException e) {
            return vn.civil.grpc.payment.TaxCategory.TAX_CATEGORY_UNSPECIFIED;
        }
    }

    public TaxCategory toDomainTaxCategory(vn.civil.grpc.payment.TaxCategory protoCategory) {
        if (protoCategory == null || protoCategory == vn.civil.grpc.payment.TaxCategory.TAX_CATEGORY_UNSPECIFIED) {
            return null;
        }
        try {
            return TaxCategory.valueOf(protoCategory.name());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public vn.civil.grpc.payment.OrderStatus toProtoOrderStatus(OrderStatus status) {
        if (status == null) return vn.civil.grpc.payment.OrderStatus.ORDER_STATUS_UNSPECIFIED;
        return switch (status) {
            case PENDING -> vn.civil.grpc.payment.OrderStatus.ORDER_PENDING;
            case PROCESSING -> vn.civil.grpc.payment.OrderStatus.ORDER_PROCESSING;
            case COMPLETED -> vn.civil.grpc.payment.OrderStatus.ORDER_COMPLETED;
            case FAILED -> vn.civil.grpc.payment.OrderStatus.ORDER_FAILED;
            case CANCELLED -> vn.civil.grpc.payment.OrderStatus.ORDER_CANCELLED;
        };
    }

    public vn.civil.grpc.payment.TransactionStatus toProtoTxnStatus(TransactionStatus status) {
        if (status == null) return vn.civil.grpc.payment.TransactionStatus.TXN_STATUS_UNSPECIFIED;
        return switch (status) {
            case PENDING -> vn.civil.grpc.payment.TransactionStatus.TXN_PENDING;
            case SUCCESS -> vn.civil.grpc.payment.TransactionStatus.TXN_SUCCESS;
            case FAILED -> vn.civil.grpc.payment.TransactionStatus.TXN_FAILED;
            case REVERSED -> vn.civil.grpc.payment.TransactionStatus.TXN_REVERSED;
        };
    }
}
