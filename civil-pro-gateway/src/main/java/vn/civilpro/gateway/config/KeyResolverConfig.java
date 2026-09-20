package vn.civilpro.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

@Configuration
public class KeyResolverConfig {

    @Primary
    @Bean(name = "authKeyResolver")
    public KeyResolver authKeyResolver() {
        return exchange -> {
            String authHeader = exchange.getRequest()
                    .getHeaders()
                    .getFirst("Authorization");
            if (authHeader == null || authHeader.isEmpty()) {
                return Mono.just("anonymous");
            }
            return Mono.just(authHeader);
        };
    }

    @Bean(name = "paymentKeyResolver")
    public KeyResolver paymentKeyResolver() {
        return exchange -> {
            var request = exchange.getRequest();
            String userId = request.getHeaders().getFirst("X-User-Id");
            if (userId != null && !userId.isBlank()) {
                return Mono.just("pay:user:" + userId);
            }
            String authHeader = request.getHeaders().getFirst("Authorization");
            if (authHeader != null && !authHeader.isBlank()) {
                return Mono.just("pay:token:" + authHeader);
            }
            String clientIp = request.getRemoteAddress() != null && request.getRemoteAddress().getAddress() != null
                    ? request.getRemoteAddress().getAddress().getHostAddress()
                    : "anonymous";
            return Mono.just("pay:ip:" + clientIp);
        };
    }
}