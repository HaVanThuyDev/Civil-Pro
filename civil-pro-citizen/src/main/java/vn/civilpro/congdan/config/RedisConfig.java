package vn.civilpro.congdan.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * ================================================================
 * REDIS CONFIGURATION
 * - RedisTemplate: dùng cho manual cache (redisTemplate.opsForValue())
 * - CacheManager: dùng cho @Cacheable / @CacheEvict annotation
 * - Serialize value thành JSON (dễ debug, không bị class-casting lúc restart)
 * ================================================================
 */
@Configuration
@EnableCaching
public class RedisConfig {

    /**
     * ObjectMapper riêng cho Redis - bật type info để deserialize đúng class
     */
    @Bean("redisObjectMapper")
    public ObjectMapper redisObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        // Ghi type vào JSON để khi đọc lại biết deserialize về class nào
        mapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );
        return mapper;
    }

    /**
     * RedisTemplate<String, Object> - dùng cho manual operations
     * Key: String, Value: JSON
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory factory,
            ObjectMapper redisObjectMapper) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer jsonSerializer =
                new GenericJackson2JsonRedisSerializer(redisObjectMapper);

        // Key luôn là String
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // Value là JSON
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * CacheManager - quản lý TTL cho từng cache name
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory,
                                     ObjectMapper redisObjectMapper) {

        GenericJackson2JsonRedisSerializer jsonSerializer =
                new GenericJackson2JsonRedisSerializer(redisObjectMapper);

        // Cấu hình mặc định
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new StringRedisSerializer()))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer))
                .disableCachingNullValues();

        // TTL riêng cho từng cache
        Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();

        // Cache công dân: 1 tiếng
        cacheConfigs.put("congDan",
                defaultConfig.entryTtl(Duration.ofHours(1)));

        // Cache lookup CCCD: 30 phút
        cacheConfigs.put("cccdLookup",
                defaultConfig.entryTtl(Duration.ofMinutes(30)));

        // Cache ĐVHC (ít thay đổi): 24 tiếng
        cacheConfigs.put("dvhc",
                defaultConfig.entryTtl(Duration.ofHours(24)));

        // Cache thống kê dashboard: 5 phút (refresh thường xuyên hơn)
        cacheConfigs.put("thongKe",
                defaultConfig.entryTtl(Duration.ofMinutes(5)));

        return RedisCacheManager.builder(factory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigs)
                .build();
    }
}