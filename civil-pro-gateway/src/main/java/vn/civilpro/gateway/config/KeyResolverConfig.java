package vn.civilpro.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class KeyResolverConfig {

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
}