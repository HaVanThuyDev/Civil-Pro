package vn.civilpro.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Cho phép các nguồn (Origin) gửi request tới. Bạn có thể thay "*" bằng "http://localhost:8083" để bảo mật hơn
        configuration.setAllowedOriginPatterns(List.of("*"));

        // Cho phép tất cả các phương thức HTTP
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Cho phép tất cả các Headers (ví dụ: Authorization, Content-Type)
        configuration.setAllowedHeaders(List.of("*"));

        // Cho phép gửi kèm Cookie hoặc thông tin xác thực JWT Token từ Front-end
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Áp dụng cấu hình này cho toàn bộ API
        return source;
    }
}