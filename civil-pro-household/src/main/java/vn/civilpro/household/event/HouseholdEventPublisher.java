package vn.civilpro.household.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HouseholdEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${civil-pro.kafka.topics.household-events:household.events}")
    private String householdEventsTopic;

    public void publishHouseholdCreated(HouseholdEvent event) {
        send(event.getHouseholdCode(), event);
    }

    public void publishMemberAdded(HouseholdEvent event) {
        send(event.getHouseholdCode(), event);
    }

    private void send(String key, Object payload) {
        try {
            kafkaTemplate.send(householdEventsTopic, key, payload).whenComplete((res, ex) -> {
                if (ex != null) {
                    log.error("[Kafka] Failed to publish household event key={}: {}", key, ex.getMessage());
                } else {
                    log.debug("[Kafka] Household event published key={} offset={}", key, res.getRecordMetadata().offset());
                }
            });
        } catch (Exception e) {
            log.warn("[Kafka] Error triggering kafka event (continuing): {}", e.getMessage());
        }
    }
}
