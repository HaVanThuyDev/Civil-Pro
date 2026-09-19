package vn.civilpro.pay.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import vn.civil.grpc.common.GrpcResponse;
import vn.civil.grpc.payment.*;
import vn.civilpro.pay.model.dto.response.PaymentReceiptResponse;
import vn.civilpro.pay.model.dto.response.TaxObligationResponse;
import vn.civilpro.pay.model.entity.PaymentOrder;
import vn.civilpro.pay.model.entity.PaymentTransaction;
import vn.civilpro.pay.model.enums.PaymentMethod;
import vn.civilpro.pay.exception.DuplicateTransactionException;
import vn.civilpro.pay.exception.ResourceNotFoundException;
import vn.civilpro.pay.exception.UnauthorizedException;
import vn.civilpro.pay.mapper.PaymentMapper;
import vn.civilpro.pay.model.enums.TaxCategory;
import vn.civilpro.pay.repository.PaymentOrderRepository;
import vn.civilpro.pay.repository.PaymentTransactionRepository;
import vn.civilpro.pay.service.PaymentService;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class PaymentGrpcServiceImpl extends PaymentGrpcServiceGrpc.PaymentGrpcServiceImplBase {

    private final PaymentService paymentService;
    private final PaymentOrderRepository orderRepository;
    private final PaymentTransactionRepository transactionRepository;
    private final PaymentMapper paymentMapper;

    @Override
    public void createOrder(CreatePaymentOrderRequest request, StreamObserver<CreatePaymentOrderResponse> responseObserver) {
        try {
            log.info("[gRPC] createOrder taxpayer: {}, amount: {}", request.getTaxpayerNationalId(), request.getAmount());

            TaxCategory category = paymentMapper.toDomainTaxCategory(request.getTaxCategory());
            if (category == null) {
                category = TaxCategory.PERSONAL_INCOME_TAX;
            }

            var dto = vn.civilpro.pay.model.dto.request.CreatePaymentOrderRequest.builder()
                    .taxCategory(category)
                    .taxpayerNationalId(request.getTaxpayerNationalId())
                    .taxpayerName(request.getTaxpayerName())
                    .taxCode(request.getTaxCode())
                    .amount(BigDecimal.valueOf(request.getAmount()))
                    .fiscalPeriod(request.getFiscalPeriod())
                    .notes(request.getNotes())
                    .build();

            var orderResponse = paymentService.createPaymentOrder(dto);
            PaymentOrder order = orderRepository.findByOrderCode(orderResponse.getOrderCode()).orElse(null);

            PaymentOrderInfo orderInfo = paymentMapper.toProtoOrderInfo(order);

            responseObserver.onNext(CreatePaymentOrderResponse.newBuilder()
                    .setMeta(GrpcResponse.newBuilder().setSuccess(true).setCode(200).setMessage("Order created successfully").build())
                    .setOrder(orderInfo)
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("[gRPC] Error creating order: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void processPayment(ProcessPaymentRequest request, StreamObserver<ProcessPaymentResponse> responseObserver) {
        try {
            log.info("[gRPC] processPayment orderCode: {}, idempotencyKey: {}", request.getOrderCode(), request.getIdempotencyKey());

            PaymentMethod method = PaymentMethod.BANK_TRANSFER;
            if (request.getPaymentMethod() != null && !request.getPaymentMethod().isBlank()) {
                try {
                    method = PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase());
                } catch (IllegalArgumentException ignored) {
                    method = PaymentMethod.BANK_TRANSFER;
                }
            }

            var dto = vn.civilpro.pay.model.dto.request.ProcessPaymentRequest.builder()
                    .orderCode(request.getOrderCode())
                    .idempotencyKey(request.getIdempotencyKey())
                    .paymentMethod(method)
                    .debitAccount(request.getDebitAccount())
                    .authToken(request.getAuthToken())
                    .build();

            PaymentReceiptResponse receipt = paymentService.processPayment(dto);

            PaymentOrder order = orderRepository.findByOrderCode(receipt.getOrder().getOrderCode()).orElse(null);
            PaymentTransaction txn = null;
            if (receipt.getTransaction() != null) {
                txn = transactionRepository.findByTxnReference(receipt.getTransaction().getTxnReference()).orElse(null);
            }

            PaymentOrderInfo orderInfo = paymentMapper.toProtoOrderInfo(order);
            PaymentTransactionInfo txnInfo = paymentMapper.toProtoTransactionInfo(txn);

            responseObserver.onNext(ProcessPaymentResponse.newBuilder()
                    .setMeta(GrpcResponse.newBuilder().setSuccess(true).setCode(200).setMessage("Payment processed successfully").build())
                    .setOrder(orderInfo)
                    .setTransaction(txnInfo)
                    .setReceiptUrl("/api/pay/orders/" + receipt.getOrder().getOrderCode())
                    .build());
            responseObserver.onCompleted();
        } catch (DuplicateTransactionException e) {
            log.warn("[gRPC] Duplicate transaction: {}", e.getMessage());
            responseObserver.onNext(ProcessPaymentResponse.newBuilder()
                    .setMeta(GrpcResponse.newBuilder().setSuccess(false).setCode(409).setMessage(e.getMessage()).build())
                    .build());
            responseObserver.onCompleted();
        } catch (UnauthorizedException e) {
            log.warn("[gRPC] Unauthorized payment: {}", e.getMessage());
            responseObserver.onNext(ProcessPaymentResponse.newBuilder()
                    .setMeta(GrpcResponse.newBuilder().setSuccess(false).setCode(401).setMessage(e.getMessage()).build())
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("[gRPC] Error processing payment: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getStatus(GetPaymentStatusRequest request, StreamObserver<GetPaymentStatusResponse> responseObserver) {
        try {
            log.info("[gRPC] getStatus reference: {}", request.getReference());

            PaymentReceiptResponse receipt = paymentService.getStatusByReference(request.getReference());
            PaymentOrder order = orderRepository.findByOrderCode(receipt.getOrder().getOrderCode()).orElse(null);
            PaymentTransaction txn = null;
            if (receipt.getTransaction() != null) {
                txn = transactionRepository.findByTxnReference(receipt.getTransaction().getTxnReference()).orElse(null);
            }

            PaymentOrderInfo orderInfo = paymentMapper.toProtoOrderInfo(order);
            PaymentTransactionInfo txnInfo = paymentMapper.toProtoTransactionInfo(txn);

            responseObserver.onNext(GetPaymentStatusResponse.newBuilder()
                    .setMeta(GrpcResponse.newBuilder().setSuccess(true).setCode(200).setMessage("OK").build())
                    .setOrder(orderInfo)
                    .setTransaction(txnInfo)
                    .build());
            responseObserver.onCompleted();
        } catch (ResourceNotFoundException e) {
            log.warn("[gRPC] Not found: {}", e.getMessage());
            responseObserver.onNext(GetPaymentStatusResponse.newBuilder()
                    .setMeta(GrpcResponse.newBuilder().setSuccess(false).setCode(404).setMessage(e.getMessage()).build())
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("[gRPC] Error getStatus: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getTaxObligations(GetTaxObligationsRequest request, StreamObserver<GetTaxObligationsResponse> responseObserver) {
        try {
            log.info("[gRPC] getTaxObligations nationalId: {}, taxCode: {}", request.getTaxpayerNationalId(), request.getTaxCode());

            List<TaxObligationResponse> list = paymentService.getTaxObligations(request.getTaxpayerNationalId(), request.getTaxCode());

            List<TaxObligationInfo> protoList = list.stream()
                    .map(paymentMapper::toProtoTaxObligationInfo)
                    .toList();

            double totalAmount = list.stream()
                    .map(TaxObligationResponse::getAmountDue)
                    .mapToDouble(BigDecimal::doubleValue)
                    .sum();

            responseObserver.onNext(GetTaxObligationsResponse.newBuilder()
                    .setMeta(GrpcResponse.newBuilder().setSuccess(true).setCode(200).setMessage("OK").build())
                    .addAllObligations(protoList)
                    .setTotalAmountDue(totalAmount)
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("[gRPC] Error getTaxObligations: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }
}
