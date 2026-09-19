package vn.civilpro.congdan.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

/**
 * Cấu hình Kafka consumer.
 *
 * - AckMode = MANUAL_IMMEDIATE: bắt buộc phải có vì
 *   spring.kafka.consumer.enable-auto-commit=false. Nếu thiếu dòng này,
 *   offset sẽ không bao giờ được commit và message cũ sẽ bị xử lý lại mỗi
 *   lần service khởi động lại. @KafkaListener cần khai báo tham số
 *   Acknowledgment và gọi ack.acknowledge() sau khi xử lý xong.
 *
 * - errorHandler: dùng DeadLetterPublishingRecoverer thay vì recoverer mặc
 *   định (log rồi bỏ qua). Sau khi retry FixedBackOff(1000ms, 3 lần) thất
 *   bại, message được đẩy sang topic "<topic-gốc>.DLT" thay vì mất vĩnh viễn.
 */
@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    /**
     * Consumer factory với error handling và retry
     */
    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false); // Manual commit
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 100);
        // Sửa lại đúng package thật của dự án — tránh trùng "startsWith" ngoài ý muốn
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "vn.civilpro.congdan.*");

        return new DefaultKafkaConsumerFactory<>(props);
    }

    /**
     * Error handler: retry 3 lần (delay 1s/lần), sau đó publish message lỗi
     * sang topic Dead Letter "<topic>.DLT" thay vì mất message.
     */
    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<String, Object> kafkaTemplate) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
                (record, ex) -> new TopicPartition(record.topic() + ".DLT", record.partition()));
        return new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 3L));
    }

    /**
     * Listener container factory - hỗ trợ batch + error handling + manual ack
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            DefaultErrorHandler errorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(3); // 3 consumer threads
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.setCommonErrorHandler(errorHandler);

        return factory;
    }
}