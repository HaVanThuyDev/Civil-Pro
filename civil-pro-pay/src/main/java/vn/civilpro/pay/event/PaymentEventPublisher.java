package vn.civilpro.pay.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${civil-pro.kafka.topics.payment-events:payment.events}")
    private String paymentEventsTopic;

    public void publishPaymentCompleted(PaymentEvent event) {
        send(event.getOrderCode(), event);
    }

    private void send(String key, Object payload) {
        try {
            kafkaTemplate.send(paymentEventsTopic, key, payload).whenComplete((res, ex) -> {
                if (ex != null) {
                    log.error("[Kafka] Failed to publish payment event key={}: {}", key, ex.getMessage());
                } else {
                    log.debug("[Kafka] Payment event published key={} offset={}", key, res.getRecordMetadata().offset());
                }
            });
        } catch (Exception e) {
            log.warn("[Kafka] Error triggering kafka payment event (continuing): {}", e.getMessage());
        }
    }
}
