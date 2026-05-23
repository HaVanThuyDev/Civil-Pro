package vn.civilpro.biendong.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import vn.civilpro.biendong.entity.BienDongDanCu;
import vn.civilpro.biendong.repository.BienDongRepository;

import java.time.LocalDate;

/**
 * ================================================================
 * KAFKA CONSUMER - BIẾN ĐỘNG DÂN CƯ
 * Subscribe các topic từ CongDan Service và HoKhau Service.
 * Tự động tạo bản ghi biến động khi có sự kiện KHAI_TU.
 * ================================================================
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BienDongKafkaConsumer {

    private final BienDongRepository bienDongRepository;

    /**
     * Nhận event khai tử từ CongDan Service.
     * Tự động tạo bản ghi biến động TU.
     */
    @KafkaListener(
            topics = "${civil-pro.kafka.topics.cong-dan-khai-tu:cong-dan.khai-tu}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onCongDanKhaiTu(
            @Payload CongDanKafkaEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("[Kafka Consumer][BienDong] Nhận event KHAI_TU: congDanId={}, topic={}, offset={}",
                event.getCongDanId(), topic, offset);

        try {
            LocalDate ngayHom = LocalDate.now();

            BienDongDanCu bienDong = BienDongDanCu.builder()
                    .maBienDong(generateMaBienDong("TU"))
                    .loaiBienDong("TU")
                    .idCongDan(event.getCongDanId())
                    .hoTen(event.getHoTen())
                    .maDvhc(event.getMaDvhc())
                    .ngayBienDong(ngayHom)
                    .thangBienDong(ngayHom.getMonthValue())
                    .namBienDong(ngayHom.getYear())
                    .moTa("Khai tử tự động từ sự kiện hệ thống")
                    .nguoiKhaiBao("SYSTEM")
                    .build();

            bienDongRepository.save(bienDong);
            log.info("[Kafka Consumer][BienDong] Đã ghi biến động TU cho congDanId={}", event.getCongDanId());

        } catch (Exception e) {
            log.error("[Kafka Consumer][BienDong] Lỗi xử lý event KHAI_TU: {}", e.getMessage(), e);
            // Ném lại để Kafka retry theo ErrorHandler config
            throw e;
        }
    }

    /**
     * Nhận event tạo công dân mới - ghi biến động SINH nếu năm sinh = năm hiện tại.
     */
    @KafkaListener(
            topics = "${civil-pro.kafka.topics.cong-dan-created:cong-dan.created}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void onCongDanCreated(@Payload CongDanKafkaEvent event) {
        log.debug("[Kafka Consumer][BienDong] Nhận event CREATED: congDanId={}", event.getCongDanId());

        // Chỉ ghi biến động SINH nếu là trẻ sơ sinh (khai sinh mới)
        // Logic: nếu năm sinh = năm hiện tại → đây là khai sinh
        if (event.getNgaySinh() != null) {
            LocalDate ngaySinh = LocalDate.parse(event.getNgaySinh());
            if (ngaySinh.getYear() == LocalDate.now().getYear()) {

                BienDongDanCu bienDong = BienDongDanCu.builder()
                        .maBienDong(generateMaBienDong("SINH"))
                        .loaiBienDong("SINH")
                        .idCongDan(event.getCongDanId())
                        .hoTen(event.getHoTen())
                        .ngaySinh(ngaySinh)
                        .maDvhc(event.getMaDvhc())
                        .ngayBienDong(LocalDate.now())
                        .thangBienDong(LocalDate.now().getMonthValue())
                        .namBienDong(LocalDate.now().getYear())
                        .moTa("Khai sinh tự động")
                        .build();

                bienDongRepository.save(bienDong);
            }
        }
    }

    private String generateMaBienDong(String loai) {
        return String.format("BD-%s-%d-%d",
                loai,
                LocalDate.now().getYear(),
                System.currentTimeMillis() % 100000);
    }
}