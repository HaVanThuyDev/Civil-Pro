package vn.civilpro.congdan.event;


import vn.civilpro.congdan.model.entity.Citizen;

/**
 * Domain event nội bộ (Spring ApplicationEvent), KHÔNG phải Kafka event.
 * Được publish trong transaction, chỉ thực sự đẩy sang Kafka sau khi commit
 * thành công (xem CitizenEventPublisher#onCitizenCreated).
 */
public record CitizenCreatedEvent(Citizen citizen) {
}