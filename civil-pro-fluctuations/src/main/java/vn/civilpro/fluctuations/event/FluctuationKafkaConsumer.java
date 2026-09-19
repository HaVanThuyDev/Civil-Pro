package vn.civilpro.fluctuations.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import vn.civilpro.fluctuations.model.entity.PopulationFluctuation;
import vn.civilpro.fluctuations.repository.PopulationFluctuationRepository;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class FluctuationKafkaConsumer {

    private final PopulationFluctuationRepository fluctuationRepository;

    @KafkaListener(
            topics = "${civil-pro.kafka.topics.citizen-deceased:citizen.deceased}",
            groupId = "${spring.kafka.consumer.group-id:fluctuations-service}"
    )
    public void onCitizenDeceased(@Payload CitizenKafkaEvent event) {
        log.info("[Kafka][Fluctuation] Received citizen deceased event: citizenId={}", event.getCitizenId());
        try {
            LocalDate today = LocalDate.now();
            PopulationFluctuation record = PopulationFluctuation.builder()
                    .fluctuationCode(generateCode("DEATH"))
                    .fluctuationType("DEATH")
                    .citizenId(event.getCitizenId())
                    .fullName(event.getFullName())
                    .areaCode(event.getAreaCode() != null ? event.getAreaCode() : "UNKNOWN")
                    .fluctuationDate(today)
                    .month(today.getMonthValue())
                    .year(today.getYear())
                    .dateOfDeath(today)
                    .description("Automatic death registration from citizen event")
                    .declaredBy("SYSTEM")
                    .build();

            fluctuationRepository.save(record);
            log.info("[Kafka][Fluctuation] Recorded DEATH fluctuation for citizenId={}", event.getCitizenId());
        } catch (Exception e) {
            log.error("[Kafka][Fluctuation] Error processing citizen deceased event: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(
            topics = "${civil-pro.kafka.topics.citizen-created:citizen.created}",
            groupId = "${spring.kafka.consumer.group-id:fluctuations-service}"
    )
    public void onCitizenCreated(@Payload CitizenKafkaEvent event) {
        log.info("[Kafka][Fluctuation] Received citizen created event: citizenId={}", event.getCitizenId());
        try {
            LocalDate today = LocalDate.now();
            PopulationFluctuation record = PopulationFluctuation.builder()
                    .fluctuationCode(generateCode("BIRTH"))
                    .fluctuationType("BIRTH")
                    .citizenId(event.getCitizenId())
                    .fullName(event.getFullName())
                    .areaCode(event.getAreaCode() != null ? event.getAreaCode() : "UNKNOWN")
                    .fluctuationDate(today)
                    .month(today.getMonthValue())
                    .year(today.getYear())
                    .dateOfBirth(today)
                    .description("Automatic birth registration from citizen creation")
                    .declaredBy("SYSTEM")
                    .build();

            fluctuationRepository.save(record);
            log.info("[Kafka][Fluctuation] Recorded BIRTH fluctuation for citizenId={}", event.getCitizenId());
        } catch (Exception e) {
            log.error("[Kafka][Fluctuation] Error processing citizen created event: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(
            topics = "${civil-pro.kafka.topics.household-events:household.events}",
            groupId = "${spring.kafka.consumer.group-id:fluctuations-service}"
    )
    public void onHouseholdEvent(@Payload HouseholdKafkaEvent event) {
        log.info("[Kafka][Fluctuation] Received household event: type={}, code={}", event.getEventType(), event.getHouseholdCode());
        try {
            LocalDate today = LocalDate.now();
            PopulationFluctuation record = PopulationFluctuation.builder()
                    .fluctuationCode(generateCode("MIGRATION"))
                    .fluctuationType("INTERNAL_MIGRATION")
                    .citizenId(event.getCitizenId())
                    .fullName(event.getCitizenFullName())
                    .areaCode(event.getAreaCode() != null ? event.getAreaCode() : "UNKNOWN")
                    .fluctuationDate(today)
                    .month(today.getMonthValue())
                    .year(today.getYear())
                    .description("Household event: " + event.getEventType() + " for household " + event.getHouseholdCode())
                    .declaredBy("SYSTEM")
                    .build();

            fluctuationRepository.save(record);
        } catch (Exception e) {
            log.error("[Kafka][Fluctuation] Error processing household event: {}", e.getMessage(), e);
        }
    }

    private String generateCode(String prefix) {
        return "FL-" + prefix + "-" + LocalDate.now().getYear() + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}
