package vn.civilpro.pay.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.civilpro.pay.model.dto.request.CreatePaymentOrderRequest;
import vn.civilpro.pay.model.dto.request.ProcessPaymentRequest;
import vn.civilpro.pay.model.dto.response.PaymentOrderResponse;
import vn.civilpro.pay.model.dto.response.PaymentReceiptResponse;
import vn.civilpro.pay.model.dto.response.TaxObligationResponse;
import vn.civilpro.pay.model.enums.OrderStatus;
import vn.civilpro.pay.model.enums.TaxCategory;

import java.util.List;

public interface PaymentService {

    PaymentOrderResponse createPaymentOrder(CreatePaymentOrderRequest request);

    PaymentReceiptResponse processPayment(ProcessPaymentRequest request);

    PaymentOrderResponse getOrderByCode(String orderCode);

    PaymentReceiptResponse getReceiptByOrderCode(String orderCode);

    PaymentReceiptResponse getStatusByReference(String reference);

    Page<PaymentOrderResponse> searchOrders(String nationalId, String taxCode, OrderStatus status, TaxCategory taxCategory, Pageable pageable);

    List<TaxObligationResponse> getTaxObligations(String taxpayerNationalId, String taxCode);
}
