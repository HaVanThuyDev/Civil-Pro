package vn.civilpro.pay;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import vn.civilpro.auth.grpc.proto.AuthServiceGrpc;
import vn.civilpro.auth.grpc.proto.ValidateTokenRequest;
import vn.civilpro.auth.grpc.proto.ValidateTokenResponse;
import vn.civilpro.pay.model.dto.request.CreatePaymentOrderRequest;
import vn.civilpro.pay.model.dto.request.ProcessPaymentRequest;
import vn.civilpro.pay.model.dto.response.PaymentOrderResponse;
import vn.civilpro.pay.model.dto.response.PaymentReceiptResponse;
import vn.civilpro.pay.model.dto.response.TaxObligationResponse;
import vn.civilpro.pay.model.entity.LedgerEntry;
import vn.civilpro.pay.model.entity.PaymentAuditLog;
import vn.civilpro.pay.model.entity.PaymentOrder;
import vn.civilpro.pay.model.entity.PaymentTransaction;
import vn.civilpro.pay.event.PaymentEvent;
import vn.civilpro.pay.event.PaymentEventPublisher;
import vn.civilpro.pay.exception.DuplicateTransactionException;
import vn.civilpro.pay.exception.ResourceNotFoundException;
import vn.civilpro.pay.exception.UnauthorizedException;
import vn.civilpro.pay.mapper.PaymentMapper;
import vn.civilpro.pay.model.enums.*;
import vn.civilpro.pay.repository.LedgerEntryRepository;
import vn.civilpro.pay.repository.PaymentAuditLogRepository;
import vn.civilpro.pay.repository.PaymentOrderRepository;
import vn.civilpro.pay.repository.PaymentTransactionRepository;
import vn.civilpro.pay.service.impl.PaymentServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PaymentServiceTest {

    @Mock
    private PaymentOrderRepository orderRepository;

    @Mock
    private PaymentTransactionRepository transactionRepository;

    @Mock
    private LedgerEntryRepository ledgerEntryRepository;

    @Mock
    private PaymentAuditLogRepository auditLogRepository;

    @Mock
    private PaymentEventPublisher eventPublisher;

    @Mock
    private AuthServiceGrpc.AuthServiceBlockingStub authGrpcStub;

    @Spy
    private PaymentMapper paymentMapper = new PaymentMapper();

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private PaymentOrder sampleOrder;

    @BeforeEach
    void setUp() {
        paymentService.setAuthGrpcStub(authGrpcStub);

        sampleOrder = PaymentOrder.builder()
                .id(100L)
                .orderCode("ORD-TAX-2026-TEST01")
                .taxCategory(TaxCategory.PERSONAL_INCOME_TAX)
                .taxpayerNationalId("001200000001")
                .taxpayerName("Nguyen Van B")
                .taxCode("8839210492")
                .amount(new BigDecimal("5000000.00"))
                .currency("VND")
                .fiscalPeriod("Q1/2026")
                .status(OrderStatus.PENDING)
                .notes("Tax declaration test")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Create Payment Order - Successfully persists and logs audit")
    void testCreatePaymentOrder_Success() {
        CreatePaymentOrderRequest request = CreatePaymentOrderRequest.builder()
                .taxCategory(TaxCategory.PERSONAL_INCOME_TAX)
                .taxpayerNationalId("001200000001")
                .taxpayerName("Nguyen Van B")
                .taxCode("8839210492")
                .amount(new BigDecimal("5000000.00"))
                .fiscalPeriod("Q1/2026")
                .notes("Personal Income Tax Q1")
                .build();

        when(orderRepository.save(any(PaymentOrder.class))).thenAnswer(inv -> {
            PaymentOrder po = inv.getArgument(0);
            po.setId(100L);
            return po;
        });

        PaymentOrderResponse response = paymentService.createPaymentOrder(request);

        assertNotNull(response);
        assertNotNull(response.getOrderCode());
        assertTrue(response.getOrderCode().startsWith("ORD-TAX-"));
        assertEquals(TaxCategory.PERSONAL_INCOME_TAX, response.getTaxCategory());
        assertEquals("001200000001", response.getTaxpayerNationalId());
        assertEquals(new BigDecimal("5000000.00"), response.getAmount());
        assertEquals(OrderStatus.PENDING, response.getStatus());

        verify(orderRepository, times(1)).save(any(PaymentOrder.class));
        verify(auditLogRepository, times(1)).save(any(PaymentAuditLog.class));
    }

    @Test
    @DisplayName("Process Payment - Full Banking Ledger & gRPC Auth Validation")
    void testProcessPayment_Success_DoubleEntryLedger() {
        ProcessPaymentRequest request = ProcessPaymentRequest.builder()
                .orderCode("ORD-TAX-2026-TEST01")
                .idempotencyKey("IDEMP-KEY-999")
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .debitAccount("19038291028301")
                .authToken("valid-jwt-token")
                .build();

        when(orderRepository.findByIdempotencyKey("IDEMP-KEY-999")).thenReturn(Optional.empty());
        when(orderRepository.findByOrderCode("ORD-TAX-2026-TEST01")).thenReturn(Optional.of(sampleOrder));
        when(orderRepository.saveAndFlush(any(PaymentOrder.class))).thenReturn(sampleOrder);
        when(orderRepository.save(any(PaymentOrder.class))).thenReturn(sampleOrder);

        // Mock gRPC auth response
        ValidateTokenResponse authResponse = ValidateTokenResponse.newBuilder()
                .setValid(true)
                .setUsername("nguyenvanb")
                .setUserId("10")
                .build();
        when(authGrpcStub.validateToken(any(ValidateTokenRequest.class))).thenReturn(authResponse);

        when(transactionRepository.save(any(PaymentTransaction.class))).thenAnswer(inv -> {
            PaymentTransaction txn = inv.getArgument(0);
            txn.setId(500L);
            return txn;
        });

        PaymentReceiptResponse receipt = paymentService.processPayment(request);

        // Verify receipt
        assertNotNull(receipt);
        assertNotNull(receipt.getReceiptNumber());
        assertTrue(receipt.getReceiptNumber().startsWith("BL-TAX-"));
        assertEquals(OrderStatus.COMPLETED, sampleOrder.getStatus());
        assertNotNull(receipt.getDigitalSignature());

        // Verify Double-Entry Bookkeeping Ledger: 1 DEBIT and 1 CREDIT
        ArgumentCaptor<LedgerEntry> ledgerCaptor = ArgumentCaptor.forClass(LedgerEntry.class);
        verify(ledgerEntryRepository, times(2)).save(ledgerCaptor.capture());

        List<LedgerEntry> savedEntries = ledgerCaptor.getAllValues();
        assertEquals(2, savedEntries.size());

        LedgerEntry debitEntry = savedEntries.stream()
                .filter(e -> e.getEntryType() == LedgerEntryType.DEBIT)
                .findFirst().orElseThrow();
        LedgerEntry creditEntry = savedEntries.stream()
                .filter(e -> e.getEntryType() == LedgerEntryType.CREDIT)
                .findFirst().orElseThrow();

        // Invariant: Debit Amount == Credit Amount == Order Amount
        assertEquals(sampleOrder.getAmount(), debitEntry.getAmount());
        assertEquals(sampleOrder.getAmount(), creditEntry.getAmount());
        assertEquals("19038291028301", debitEntry.getAccountNumber());
        assertEquals("VN-STATE-TREASURY-8888", creditEntry.getAccountNumber());

        // Verify Kafka event published
        verify(eventPublisher, times(1)).publishPaymentCompleted(any(PaymentEvent.class));
        // Verify Audit log
        verify(auditLogRepository, times(1)).save(any(PaymentAuditLog.class));
    }

    @Test
    @DisplayName("Idempotency Guarantee - Re-calling with same Idempotency Key returns existing receipt without double charge")
    void testProcessPayment_Idempotency_RepeatReturnsExistingReceipt() {
        ProcessPaymentRequest request = ProcessPaymentRequest.builder()
                .orderCode("ORD-TAX-2026-TEST01")
                .idempotencyKey("IDEMP-KEY-DUPLICATE")
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .debitAccount("19038291028301")
                .build();

        sampleOrder.setStatus(OrderStatus.COMPLETED);
        sampleOrder.setIdempotencyKey("IDEMP-KEY-DUPLICATE");
        sampleOrder.setReceiptNumber("BL-TAX-2026-00000100");

        PaymentTransaction existingTxn = PaymentTransaction.builder()
                .id(777L)
                .txnReference("TXN-EXISTING-777")
                .order(sampleOrder)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .debitAccount("19038291028301")
                .creditAccount("VN-STATE-TREASURY-8888")
                .amount(sampleOrder.getAmount())
                .status(TransactionStatus.SUCCESS)
                .build();

        when(orderRepository.findByIdempotencyKey("IDEMP-KEY-DUPLICATE")).thenReturn(Optional.of(sampleOrder));
        when(transactionRepository.findTopByOrderIdOrderByCreatedAtDesc(100L)).thenReturn(Optional.of(existingTxn));

        PaymentReceiptResponse receipt = paymentService.processPayment(request);

        assertNotNull(receipt);
        assertEquals("BL-TAX-2026-00000100", receipt.getReceiptNumber());

        // Must NOT create new transaction or ledger entries
        verify(transactionRepository, never()).save(any(PaymentTransaction.class));
        verify(ledgerEntryRepository, never()).save(any(LedgerEntry.class));
    }

    @Test
    @DisplayName("Idempotency In-Flight Check - Concurrent request on PROCESSING throws DuplicateTransactionException")
    void testProcessPayment_ConcurrentProcessing_ThrowsDuplicateException() {
        ProcessPaymentRequest request = ProcessPaymentRequest.builder()
                .orderCode("ORD-TAX-2026-TEST01")
                .idempotencyKey("IDEMP-PROCESSING-KEY")
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .debitAccount("19038291028301")
                .build();

        sampleOrder.setStatus(OrderStatus.PROCESSING);
        sampleOrder.setIdempotencyKey("IDEMP-PROCESSING-KEY");

        when(orderRepository.findByIdempotencyKey("IDEMP-PROCESSING-KEY")).thenReturn(Optional.of(sampleOrder));

        assertThrows(DuplicateTransactionException.class, () -> paymentService.processPayment(request));
    }

    @Test
    @DisplayName("gRPC Authentication Check - Invalid token throws UnauthorizedException")
    void testProcessPayment_InvalidToken_ThrowsUnauthorizedException() {
        ProcessPaymentRequest request = ProcessPaymentRequest.builder()
                .orderCode("ORD-TAX-2026-TEST01")
                .idempotencyKey("IDEMP-INVALID-AUTH")
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .debitAccount("19038291028301")
                .authToken("bad-token")
                .build();

        when(orderRepository.findByIdempotencyKey("IDEMP-INVALID-AUTH")).thenReturn(Optional.empty());

        ValidateTokenResponse invalidAuth = ValidateTokenResponse.newBuilder()
                .setValid(false)
                .build();
        when(authGrpcStub.validateToken(any(ValidateTokenRequest.class))).thenReturn(invalidAuth);

        assertThrows(UnauthorizedException.class, () -> paymentService.processPayment(request));
    }

    @Test
    @DisplayName("Order Not Found - Throws ResourceNotFoundException")
    void testGetOrderByCode_NotFound() {
        when(orderRepository.findByOrderCode("NON_EXISTENT")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentService.getOrderByCode("NON_EXISTENT"));
    }

    @Test
    @DisplayName("Support All 7 Statutory Tax Categories")
    void testSupportAllSevenTaxCategories() {
        TaxCategory[] categories = {
                TaxCategory.PERSONAL_INCOME_TAX,
                TaxCategory.VALUE_ADDED_TAX,
                TaxCategory.SPECIAL_CONSUMPTION_TAX,
                TaxCategory.NON_AGRI_LAND_TAX,
                TaxCategory.AGRI_LAND_TAX,
                TaxCategory.REAL_ESTATE_TRANSFER_TAX,
                TaxCategory.SECURITIES_TRANSFER_TAX
        };

        assertEquals(7, categories.length);

        for (TaxCategory category : categories) {
            assertNotNull(category.getDescription());
            assertFalse(category.getDescription().isBlank());

            var protoCategory = paymentMapper.toProtoTaxCategory(category);
            assertNotEquals(vn.civil.grpc.payment.TaxCategory.TAX_CATEGORY_UNSPECIFIED, protoCategory);
            assertEquals(category, paymentMapper.toDomainTaxCategory(protoCategory));
        }
    }

    @Test
    @DisplayName("Tax Obligations - Retrieves statutory obligations")
    void testGetTaxObligations() {
        List<TaxObligationResponse> obligations = paymentService.getTaxObligations("001200000001", "8839210492");

        assertNotNull(obligations);
        assertFalse(obligations.isEmpty());
        assertTrue(obligations.size() >= 4);
        assertTrue(obligations.stream().anyMatch(o -> o.getTaxCategory() == TaxCategory.PERSONAL_INCOME_TAX));
        assertTrue(obligations.stream().anyMatch(o -> o.getTaxCategory() == TaxCategory.VALUE_ADDED_TAX));
    }
}
