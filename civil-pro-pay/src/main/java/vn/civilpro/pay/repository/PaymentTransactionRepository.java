package vn.civilpro.pay.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.civilpro.pay.model.entity.PaymentTransaction;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {

    Optional<PaymentTransaction> findByTxnReference(String txnReference);

    List<PaymentTransaction> findByOrderId(Long orderId);

    Optional<PaymentTransaction> findTopByOrderIdOrderByCreatedAtDesc(Long orderId);
}
