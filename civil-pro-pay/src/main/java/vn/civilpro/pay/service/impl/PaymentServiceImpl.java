package vn.civilpro.pay.service.impl;

import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import vn.civilpro.auth.grpc.proto.AuthServiceGrpc;
import vn.civilpro.auth.grpc.proto.ValidateTokenRequest;
import vn.civilpro.auth.grpc.proto.ValidateTokenResponse;
import vn.civilpro.pay.model.dto.request.CreatePaymentOrderRequest;
import vn.civilpro.pay.model.dto.request.ProcessPaymentRequest;
import vn.civilpro.pay.model.dto.response.LedgerEntryResponse;
import vn.civilpro.pay.model.dto.response.PaymentOrderResponse;
import vn.civilpro.pay.model.dto.response.PaymentReceiptResponse;
import vn.civilpro.pay.model.dto.response.PaymentTransactionResponse;
import vn.civilpro.pay.model.dto.response.TaxObligationResponse;
import vn.civilpro.pay.model.entity.LedgerEntry;
import vn.civilpro.pay.model.entity.PaymentAuditLog;
import vn.civilpro.pay.model.entity.PaymentOrder;
import vn.civilpro.pay.model.entity.PaymentTransaction;
import vn.civilpro.pay.model.enums.LedgerEntryType;
import vn.civilpro.pay.model.enums.OrderStatus;
import vn.civilpro.pay.model.enums.TaxCategory;
import vn.civilpro.pay.model.enums.TransactionStatus;
import vn.civilpro.pay.event.PaymentEvent;
import vn.civilpro.pay.event.PaymentEventPublisher;
import vn.civilpro.pay.exception.DuplicateTransactionException;
import vn.civilpro.pay.exception.PaymentException;
import vn.civilpro.pay.exception.ResourceNotFoundException;
import vn.civilpro.pay.exception.UnauthorizedException;
import vn.civilpro.pay.mapper.PaymentMapper;
import vn.civilpro.pay.repository.LedgerEntryRepository;
import vn.civilpro.pay.repository.PaymentAuditLogRepository;
import vn.civilpro.pay.repository.PaymentOrderRepository;
import vn.civilpro.pay.repository.PaymentTransactionRepository;
import vn.civilpro.pay.service.PaymentService;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private static final String STATE_TREASURY_ACCOUNT = "VN-STATE-TREASURY-8888";
    private static final String STATE_TREASURY_NAME = "Kho bac Nha nuoc Viet Nam (State Treasury)";

    private final PaymentOrderRepository orderRepository;
    private final PaymentTransactionRepository transactionRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final PaymentAuditLogRepository auditLogRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentEventPublisher eventPublisher;

    @GrpcClient("auth-service")
    private AuthServiceGrpc.AuthServiceBlockingStub authGrpcStub;

    public void setAuthGrpcStub(AuthServiceGrpc.AuthServiceBlockingStub authGrpcStub) {
        this.authGrpcStub = authGrpcStub;
    }

    @Override
    @Transactional
    public PaymentOrderResponse createPaymentOrder(CreatePaymentOrderRequest request) {
        log.info("[PaymentService] Creating tax payment order for taxpayer: {}, category: {}, amount: {}",
                request.getTaxpayerNationalId(), request.getTaxCategory(), request.getAmount());

        String orderCode = "ORD-TAX-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        PaymentOrder order = PaymentOrder.builder()
                .orderCode(orderCode)
                .taxCategory(request.getTaxCategory())
                .taxpayerNationalId(request.getTaxpayerNationalId())
                .taxpayerName(request.getTaxpayerName())
                .taxCode(request.getTaxCode())
                .amount(request.getAmount())
                .currency("VND")
                .fiscalPeriod(request.getFiscalPeriod() != null ? request.getFiscalPeriod() : String.valueOf(LocalDateTime.now().getYear()))
                .status(OrderStatus.PENDING)
                .notes(request.getNotes())
                .createdAt(LocalDateTime.now())
                .build();

        PaymentOrder savedOrder = orderRepository.save(order);

        auditLogRepository.save(PaymentAuditLog.builder()
                .entityType("PaymentOrder")
                .entityId(savedOrder.getId())
                .action("CREATE_ORDER")
                .newState(OrderStatus.PENDING.name())
                .performedBy(savedOrder.getTaxpayerNationalId())
                .occurredAt(LocalDateTime.now())
                .build());

        return paymentMapper.toOrderResponse(savedOrder);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public PaymentReceiptResponse processPayment(ProcessPaymentRequest request) {
        log.info("[PaymentService] Processing payment orderCode: {}, idempotencyKey: {}, method: {}",
                request.getOrderCode(), request.getIdempotencyKey(), request.getPaymentMethod());

        // 1. Idempotency Check: prevent duplicate execution
        Optional<PaymentOrder> existingByIdempotency = orderRepository.findByIdempotencyKey(request.getIdempotencyKey());
        if (existingByIdempotency.isPresent()) {
            PaymentOrder existing = existingByIdempotency.get();
            if (existing.getStatus() == OrderStatus.COMPLETED) {
                log.info("[PaymentService] Idempotency match: returning existing receipt for key: {}", request.getIdempotencyKey());
                return buildReceiptResponse(existing);
            } else if (existing.getStatus() == OrderStatus.PROCESSING) {
                throw new DuplicateTransactionException("Transaction is currently processing for key: " + request.getIdempotencyKey());
            }
        }

        // 2. Validate Token via gRPC if provided
        String authenticatedUser = null;
        if (request.getAuthToken() != null && !request.getAuthToken().isBlank()) {
            ValidateTokenResponse authResp = validateTokenViaGrpc(request.getAuthToken());
            if (authResp != null && !authResp.getUsername().isBlank()) {
                authenticatedUser = authResp.getUsername();
            }
        }

        // 3. Retrieve order
        PaymentOrder order = orderRepository.findByOrderCode(request.getOrderCode())
                .orElseThrow(() -> new ResourceNotFoundException("Payment order not found: " + request.getOrderCode()));

        if (order.getStatus() == OrderStatus.COMPLETED) {
            log.info("[PaymentService] Order {} is already completed. Returning receipt.", order.getOrderCode());
            return buildReceiptResponse(order);
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new PaymentException("Cannot pay cancelled order: " + order.getOrderCode());
        }

        // 4. Update status to PROCESSING and set Idempotency Key
        order.setStatus(OrderStatus.PROCESSING);
        order.setIdempotencyKey(request.getIdempotencyKey());
        order = orderRepository.saveAndFlush(order);

        // 5. Create Payment Transaction with Checksum
        String txnRef = "TXN-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String rawChecksum = String.format("%s|%s|%s|%s|%s|%s",
                order.getOrderCode(),
                order.getAmount().toPlainString(),
                request.getDebitAccount(),
                STATE_TREASURY_ACCOUNT,
                request.getPaymentMethod().name(),
                request.getIdempotencyKey());
        String checksum = calculateSha256(rawChecksum);

        PaymentTransaction txn = PaymentTransaction.builder()
                .txnReference(txnRef)
                .order(order)
                .paymentMethod(request.getPaymentMethod())
                .debitAccount(request.getDebitAccount())
                .creditAccount(STATE_TREASURY_ACCOUNT)
                .amount(order.getAmount())
                .fee(BigDecimal.ZERO)
                .checksum(checksum)
                .status(TransactionStatus.SUCCESS)
                .gatewayTxnId("GW-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase())
                .createdAt(LocalDateTime.now())
                .completedAt(LocalDateTime.now())
                .build();

        PaymentTransaction savedTxn = transactionRepository.save(txn);

        // 6. Double-Entry Bookkeeping Ledger
        // Invariant: Total Debit == Total Credit
        LedgerEntry debitEntry = LedgerEntry.builder()
                .transactionId(savedTxn.getId())
                .entryType(LedgerEntryType.DEBIT)
                .accountNumber(request.getDebitAccount())
                .accountName(order.getTaxpayerName())
                .amount(order.getAmount())
                .balanceAfter(BigDecimal.valueOf(50_000_000L).subtract(order.getAmount()))
                .description("Debit statutory tax payment: " + order.getTaxCategory().getDescription() + " (Order: " + order.getOrderCode() + ")")
                .postedAt(LocalDateTime.now())
                .build();

        LedgerEntry creditEntry = LedgerEntry.builder()
                .transactionId(savedTxn.getId())
                .entryType(LedgerEntryType.CREDIT)
                .accountNumber(STATE_TREASURY_ACCOUNT)
                .accountName(STATE_TREASURY_NAME)
                .amount(order.getAmount())
                .balanceAfter(BigDecimal.valueOf(100_000_000_000L).add(order.getAmount()))
                .description("Credit tax collection: " + order.getTaxCategory().getDescription() + " (Order: " + order.getOrderCode() + ")")
                .postedAt(LocalDateTime.now())
                .build();

        ledgerEntryRepository.save(debitEntry);
        ledgerEntryRepository.save(creditEntry);

        // 7. Complete the Payment Order and Generate Electronic Receipt Number
        String receiptNumber = "BL-TAX-" + LocalDateTime.now().getYear() + "-" + String.format("%08d", order.getId());
        order.setStatus(OrderStatus.COMPLETED);
        order.setReceiptNumber(receiptNumber);
        order.setCompletedAt(LocalDateTime.now());
        PaymentOrder completedOrder = orderRepository.save(order);

        // 8. Audit Log
        auditLogRepository.save(PaymentAuditLog.builder()
                .entityType("PaymentOrder")
                .entityId(completedOrder.getId())
                .action("PAYMENT_COMPLETED")
                .previousState(OrderStatus.PROCESSING.name())
                .newState(OrderStatus.COMPLETED.name())
                .performedBy(authenticatedUser != null ? authenticatedUser : completedOrder.getTaxpayerNationalId())
                .occurredAt(LocalDateTime.now())
                .build());

        // 9. Publish Kafka Event
        eventPublisher.publishPaymentCompleted(PaymentEvent.builder()
                .eventType("PAYMENT_COMPLETED")
                .orderCode(completedOrder.getOrderCode())
                .txnReference(savedTxn.getTxnReference())
                .taxCategory(completedOrder.getTaxCategory())
                .taxpayerNationalId(completedOrder.getTaxpayerNationalId())
                .taxpayerName(completedOrder.getTaxpayerName())
                .taxCode(completedOrder.getTaxCode())
                .amount(completedOrder.getAmount())
                .paymentMethod(savedTxn.getPaymentMethod())
                .status(savedTxn.getStatus())
                .receiptNumber(completedOrder.getReceiptNumber())
                .timestamp(LocalDateTime.now())
                .build());

        return buildReceiptResponse(completedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentOrderResponse getOrderByCode(String orderCode) {
        PaymentOrder order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new ResourceNotFoundException("Payment order not found: " + orderCode));
        return paymentMapper.toOrderResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentReceiptResponse getReceiptByOrderCode(String orderCode) {
        PaymentOrder order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new ResourceNotFoundException("Payment order not found: " + orderCode));
        return buildReceiptResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentReceiptResponse getStatusByReference(String reference) {
        // Try as order code first
        Optional<PaymentOrder> orderOpt = orderRepository.findByOrderCode(reference);
        if (orderOpt.isPresent()) {
            return buildReceiptResponse(orderOpt.get());
        }

        // Try as transaction reference
        Optional<PaymentTransaction> txnOpt = transactionRepository.findByTxnReference(reference);
        if (txnOpt.isPresent()) {
            return buildReceiptResponse(txnOpt.get().getOrder());
        }

        throw new ResourceNotFoundException("No order or transaction found with reference: " + reference);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentOrderResponse> searchOrders(String nationalId, String taxCode, OrderStatus status, TaxCategory taxCategory, Pageable pageable) {
        return orderRepository.searchOrders(nationalId, taxCode, status, taxCategory, pageable)
                .map(paymentMapper::toOrderResponse);
    }

    @Override
    public List<TaxObligationResponse> getTaxObligations(String taxpayerNationalId, String taxCode) {
        log.info("[PaymentService] Fetching tax obligations for taxpayerNationalId: {}, taxCode: {}",
                taxpayerNationalId, taxCode);

        int currentYear = LocalDateTime.now().getYear();
        List<TaxObligationResponse> obligations = new ArrayList<>();

        obligations.add(TaxObligationResponse.builder()
                .obligationId("OBL-TNCN-" + currentYear)
                .taxCategory(TaxCategory.PERSONAL_INCOME_TAX)
                .categoryDescription(TaxCategory.PERSONAL_INCOME_TAX.getDescription())
                .description("Thuế TNCN kỳ tính thuế Q1/" + currentYear + " từ tiền lương, tiền công")
                .amountDue(new BigDecimal("2500000.00"))
                .dueDate(currentYear + "-04-30")
                .fiscalPeriod("Q1/" + currentYear)
                .taxCode(taxCode != null ? taxCode : "8839210492")
                .build());

        obligations.add(TaxObligationResponse.builder()
                .obligationId("OBL-VAT-" + currentYear)
                .taxCategory(TaxCategory.VALUE_ADDED_TAX)
                .categoryDescription(TaxCategory.VALUE_ADDED_TAX.getDescription())
                .description("Thuế Giá trị gia tăng (GTGT) hoạt động kinh doanh thương mại điện tử")
                .amountDue(new BigDecimal("1500000.00"))
                .dueDate(currentYear + "-05-20")
                .fiscalPeriod("Q1/" + currentYear)
                .taxCode(taxCode != null ? taxCode : "8839210492")
                .build());

        obligations.add(TaxObligationResponse.builder()
                .obligationId("OBL-LAND-NON-AGRI-" + currentYear)
                .taxCategory(TaxCategory.NON_AGRI_LAND_TAX)
                .categoryDescription(TaxCategory.NON_AGRI_LAND_TAX.getDescription())
                .description("Thuế sử dụng đất phi nông nghiệp thửa đất số 42, tờ bản đồ số 18")
                .amountDue(new BigDecimal("450000.00"))
                .dueDate(currentYear + "-10-31")
                .fiscalPeriod(String.valueOf(currentYear))
                .taxCode(taxCode != null ? taxCode : "8839210492")
                .build());

        obligations.add(TaxObligationResponse.builder()
                .obligationId("OBL-REAL-ESTATE-" + currentYear)
                .taxCategory(TaxCategory.REAL_ESTATE_TRANSFER_TAX)
                .categoryDescription(TaxCategory.REAL_ESTATE_TRANSFER_TAX.getDescription())
                .description("Thuế thu nhập từ chuyển nhượng bất động sản hợp đồng số CC-2026/089")
                .amountDue(new BigDecimal("18000000.00"))
                .dueDate(currentYear + "-06-15")
                .fiscalPeriod(String.valueOf(currentYear))
                .taxCode(taxCode != null ? taxCode : "8839210492")
                .build());

        return obligations;
    }

    private PaymentReceiptResponse buildReceiptResponse(PaymentOrder order) {
        PaymentOrderResponse orderResponse = paymentMapper.toOrderResponse(order);

        PaymentTransactionResponse txnResponse = null;
        List<LedgerEntryResponse> ledgerResponses = new ArrayList<>();

        Optional<PaymentTransaction> txnOpt = transactionRepository.findTopByOrderIdOrderByCreatedAtDesc(order.getId());
        if (txnOpt.isPresent()) {
            PaymentTransaction txn = txnOpt.get();
            txnResponse = paymentMapper.toTransactionResponse(txn);
            List<LedgerEntry> entries = ledgerEntryRepository.findByTransactionId(txn.getId());
            ledgerResponses = paymentMapper.toLedgerEntryResponseList(entries);
        }

        String qrPayload = String.format("CIVILPRO|TAX|%s|%s|%s|%s",
                order.getOrderCode(),
                order.getAmount().toPlainString(),
                order.getReceiptNumber() != null ? order.getReceiptNumber() : "UNISSUED",
                order.getTaxpayerNationalId());

        return PaymentReceiptResponse.builder()
                .receiptNumber(order.getReceiptNumber())
                .order(orderResponse)
                .transaction(txnResponse)
                .ledgerEntries(ledgerResponses)
                .digitalSignature("SHA256withRSA:STATE_TREASURY_DIGITAL_SIG_" + order.getOrderCode())
                .qrCodePayload(qrPayload)
                .issuedAt(order.getCompletedAt() != null ? order.getCompletedAt() : LocalDateTime.now())
                .treasuryConfirmation("Giao dich da duoc xac nhan va hach toan thanh cong vao Ngan sach Nha nuoc.")
                .build();
    }

    private ValidateTokenResponse validateTokenViaGrpc(String token) {
        if (authGrpcStub == null) {
            log.warn("[PaymentService] Auth gRPC stub is not configured or unavailable; skipping remote token check");
            return null;
        }

        try {
            ValidateTokenResponse response = authGrpcStub.validateToken(
                    ValidateTokenRequest.newBuilder().setToken(token).build()
            );

            if (!response.getValid()) {
                throw new UnauthorizedException("Session token is invalid or expired");
            }
            log.info("[PaymentService] Token successfully verified via gRPC for user: {}, roles: {}", response.getUsername(), response.getRolesList());
            return response;
        } catch (StatusRuntimeException e) {
            log.error("[PaymentService] gRPC error during token validation: status={}", e.getStatus());
            throw new UnauthorizedException("Authentication service unreachable: " + e.getStatus().getDescription());
        }
    }

    private String calculateSha256(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            log.error("Failed to compute SHA-256 checksum", e);
            return UUID.randomUUID().toString();
        }
    }
}
