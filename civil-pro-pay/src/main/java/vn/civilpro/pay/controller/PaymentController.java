package vn.civilpro.pay.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.civilpro.pay.model.dto.request.CreatePaymentOrderRequest;
import vn.civilpro.pay.model.dto.request.ProcessPaymentRequest;
import vn.civilpro.pay.model.dto.response.ApiResponse;
import vn.civilpro.pay.model.dto.response.PaymentOrderResponse;
import vn.civilpro.pay.model.dto.response.PaymentReceiptResponse;
import vn.civilpro.pay.model.dto.response.TaxObligationResponse;
import vn.civilpro.pay.model.enums.OrderStatus;
import vn.civilpro.pay.model.enums.TaxCategory;
import vn.civilpro.pay.service.PaymentService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/pay")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/orders")
    public ResponseEntity<ApiResponse<PaymentOrderResponse>> createOrder(
            @Valid @RequestBody CreatePaymentOrderRequest request) {
        log.info("[REST] POST /api/pay/orders for taxpayer: {}", request.getTaxpayerNationalId());
        PaymentOrderResponse response = paymentService.createPaymentOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Tax payment order created successfully", response));
    }

    @PostMapping("/process")
    public ResponseEntity<ApiResponse<PaymentReceiptResponse>> processPayment(
            @Valid @RequestBody ProcessPaymentRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        log.info("[REST] POST /api/pay/process for order: {}, idempotencyKey: {}",
                request.getOrderCode(), request.getIdempotencyKey());

        if ((request.getAuthToken() == null || request.getAuthToken().isBlank()) && authHeader != null) {
            if (authHeader.startsWith("Bearer ")) {
                request.setAuthToken(authHeader.substring(7));
            } else {
                request.setAuthToken(authHeader);
            }
        }

        PaymentReceiptResponse response = paymentService.processPayment(request);
        return ResponseEntity.ok(ApiResponse.success("Payment processed successfully", response));
    }

    @GetMapping("/orders/{orderCode}")
    public ResponseEntity<ApiResponse<PaymentOrderResponse>> getOrderByCode(@PathVariable String orderCode) {
        log.info("[REST] GET /api/pay/orders/{}", orderCode);
        PaymentOrderResponse response = paymentService.getOrderByCode(orderCode);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/orders/{orderCode}/receipt")
    public ResponseEntity<ApiResponse<PaymentReceiptResponse>> getReceiptByOrderCode(@PathVariable String orderCode) {
        log.info("[REST] GET /api/pay/orders/{}/receipt", orderCode);
        PaymentReceiptResponse response = paymentService.getReceiptByOrderCode(orderCode);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/status/{reference}")
    public ResponseEntity<ApiResponse<PaymentReceiptResponse>> getStatusByReference(@PathVariable String reference) {
        log.info("[REST] GET /api/pay/status/{}", reference);
        PaymentReceiptResponse response = paymentService.getStatusByReference(reference);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<Page<PaymentOrderResponse>>> searchOrders(
            @RequestParam(required = false) String nationalId,
            @RequestParam(required = false) String taxCode,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) TaxCategory taxCategory,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("[REST] GET /api/pay/orders nationalId: {}, taxCategory: {}", nationalId, taxCategory);
        Page<PaymentOrderResponse> page = paymentService.searchOrders(nationalId, taxCode, status, taxCategory, pageable);
        return ResponseEntity.ok(ApiResponse.success(page));
    }

    @GetMapping("/obligations")
    public ResponseEntity<ApiResponse<List<TaxObligationResponse>>> getTaxObligations(
            @RequestParam(required = false) String nationalId,
            @RequestParam(required = false) String taxCode) {
        log.info("[REST] GET /api/pay/obligations nationalId: {}, taxCode: {}", nationalId, taxCode);
        List<TaxObligationResponse> obligations = paymentService.getTaxObligations(nationalId, taxCode);
        return ResponseEntity.ok(ApiResponse.success(obligations));
    }
}
