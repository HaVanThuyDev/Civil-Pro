package vn.civilpro.congdan.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import vn.civilpro.congdan.model.entity.Citizen;
import vn.civilpro.congdan.model.entity.CitizenChangeLog;
import vn.civilpro.congdan.model.entity.FamilyRelationship;
import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class CitizenEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${civil-pro.kafka.topics.citizen-created}")
    private String topicCreated;

    @Value("${civil-pro.kafka.topics.citizen-updated}")
    private String topicUpdated;

    @Value("${civil-pro.kafka.topics.citizen-deceased}")
    private String topicDeceased;

    @Value("${civil-pro.kafka.topics.change-log-created:citizen.change-log-created}")
    private String topicChangeLogCreated;

    @Value("${civil-pro.kafka.topics.relationship-created:citizen.relationship-created}")
    private String topicRelationshipCreated;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCitizenCreated(CitizenCreatedEvent event) {
        Citizen c = event.citizen();
        sendEvent(topicCreated, c.getCitizenCode(), buildCitizenEvent("CITIZEN_CREATED", c, c.getStatus()));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCitizenUpdated(CitizenUpdatedEvent event) {
        Citizen c = event.citizen();
        sendEvent(topicUpdated, c.getCitizenCode(), buildCitizenEvent("CITIZEN_UPDATED", c, c.getStatus()));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCitizenDeceased(CitizenDeceasedEvent event) {
        Citizen c = event.citizen();
        sendEvent(topicDeceased, c.getCitizenCode(), buildCitizenEvent("CITIZEN_DECEASED", c, 0));
    }

    public void publishChangeLogCreated(CitizenChangeLog changeLog) {
        sendEvent(topicChangeLogCreated, String.valueOf(changeLog.getCitizenId()), changeLog);
    }

    public void publishRelationshipCreated(FamilyRelationship relationship) {
        sendEvent(topicRelationshipCreated, String.valueOf(relationship.getCitizenId()), relationship);
    }

    private CitizenEvent buildCitizenEvent(String eventType, Citizen citizen, Integer status) {
        return CitizenEvent.builder()
                .eventType(eventType)
                .citizenId(citizen.getId())
                .citizenCode(citizen.getCitizenCode())
                .fullName(citizen.getFullName())
                .areaCode(citizen.getPermanentAreaCode())
                .status(status)
                .occurredAt(Instant.now())
                .build();
    }

    private void sendEvent(String topic, String key, Object event) {
        kafkaTemplate.send(topic, key, event).whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("[Kafka] Fail | topic={} key={} error={}", topic, key, ex.getMessage());
            } else {
                log.debug("[Kafka] Success | topic={} key={} offset={}", topic, key, result.getRecordMetadata().offset());
            }
        });
    }
}