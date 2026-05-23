package vn.civilpro.congdan.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import vn.civilpro.congdan.entity.CongDan;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

/**
 * ================================================================
 * KAFKA EVENT PUBLISHER - CÔNG DÂN
 * Publish domain events khi có thay đổi dữ liệu công dân.
 * Các service khác (HoKhau, CuTru, ThongKe) subscribe các topic này.
 *
 * Topic naming convention: {domain}.{event}
 * VD: cong-dan.created, cong-dan.updated, cong-dan.khai-tu
 * ================================================================
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CongDanEventPublisher {

    private final KafkaTemplate<String, CongDanEvent> kafkaTemplate;

    @Value("${civil-pro.kafka.topics.cong-dan-created}")
    private String topicCreated;

    @Value("${civil-pro.kafka.topics.cong-dan-updated}")
    private String topicUpdated;

    @Value("${civil-pro.kafka.topics.cong-dan-khai-tu}")
    private String topicKhaiTu;

    /** Publish event khi tạo công dân mới */
    public void publishCongDanCreated(CongDan congDan) {
        CongDanEvent event = CongDanEvent.builder()
                .eventType("CONG_DAN_CREATED")
                .congDanId(congDan.getId())
                .maCongDan(congDan.getMaCongDan())
                .hoTen(congDan.getHoTen())
                .maDvhc(congDan.getMaDvhcThuongTru())
                .trangThai(congDan.getTrangThai())
                .occurredAt(Instant.now())
                .build();

        sendEvent(topicCreated, congDan.getMaCongDan(), event);
    }

    /** Publish event khi cập nhật thông tin công dân */
    public void publishCongDanUpdated(CongDan congDan) {
        CongDanEvent event = CongDanEvent.builder()
                .eventType("CONG_DAN_UPDATED")
                .congDanId(congDan.getId())
                .maCongDan(congDan.getMaCongDan())
                .hoTen(congDan.getHoTen())
                .maDvhc(congDan.getMaDvhcThuongTru())
                .trangThai(congDan.getTrangThai())
                .occurredAt(Instant.now())
                .build();

        sendEvent(topicUpdated, congDan.getMaCongDan(), event);
    }

    /** Publish event khi khai tử công dân */
    public void publishCongDanKhaiTu(CongDan congDan) {
        CongDanEvent event = CongDanEvent.builder()
                .eventType("CONG_DAN_KHAI_TU")
                .congDanId(congDan.getId())
                .maCongDan(congDan.getMaCongDan())
                .hoTen(congDan.getHoTen())
                .maDvhc(congDan.getMaDvhcThuongTru())
                .trangThai("DA_CHET")
                .occurredAt(Instant.now())
                .build();

        sendEvent(topicKhaiTu, congDan.getMaCongDan(), event);
    }

    // ---- Private helper ----

    private void sendEvent(String topic, String key, CongDanEvent event) {
        CompletableFuture<SendResult<String, CongDanEvent>> future =
                kafkaTemplate.send(topic, key, event);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("[Kafka] Gửi event thất bại | topic={} key={} error={}",
                        topic, key, ex.getMessage());
            } else {
                log.debug("[Kafka] Gửi event thành công | topic={} key={} offset={}",
                        topic, key,
                        result.getRecordMetadata().offset());
            }
        });
    }
}