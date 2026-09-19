package vn.civilpro.pay.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.civilpro.pay.model.entity.PaymentAuditLog;

import java.util.List;

@Repository
public interface PaymentAuditLogRepository extends JpaRepository<PaymentAuditLog, Long> {

    List<PaymentAuditLog> findByEntityTypeAndEntityIdOrderByOccurredAtDesc(String entityType, Long entityId);
}
