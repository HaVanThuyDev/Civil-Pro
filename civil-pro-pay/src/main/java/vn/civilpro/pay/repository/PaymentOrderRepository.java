package vn.civilpro.pay.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.civilpro.pay.model.entity.PaymentOrder;
import vn.civilpro.pay.model.enums.OrderStatus;
import vn.civilpro.pay.model.enums.TaxCategory;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentOrderRepository extends JpaRepository<PaymentOrder, Long> {

    Optional<PaymentOrder> findByOrderCode(String orderCode);

    Optional<PaymentOrder> findByIdempotencyKey(String idempotencyKey);

    boolean existsByIdempotencyKey(String idempotencyKey);

    Page<PaymentOrder> findByTaxpayerNationalIdOrderByCreatedAtDesc(String nationalId, Pageable pageable);

    List<PaymentOrder> findByTaxpayerNationalIdAndStatus(String nationalId, OrderStatus status);

    @Query("""
        SELECT o FROM PaymentOrder o
        WHERE (:nationalId IS NULL OR :nationalId = '' OR o.taxpayerNationalId = :nationalId)
        AND (:taxCode IS NULL OR :taxCode = '' OR o.taxCode = :taxCode)
        AND (:status IS NULL OR o.status = :status)
        AND (:taxCategory IS NULL OR o.taxCategory = :taxCategory)
        ORDER BY o.createdAt DESC
        """)
    Page<PaymentOrder> searchOrders(
            @Param("nationalId") String nationalId,
            @Param("taxCode") String taxCode,
            @Param("status") OrderStatus status,
            @Param("taxCategory") TaxCategory taxCategory,
            Pageable pageable
    );
}
