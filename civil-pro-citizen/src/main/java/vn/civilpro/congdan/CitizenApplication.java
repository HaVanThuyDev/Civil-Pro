package vn.civilpro.congdan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * ================================================================
 * CIVIL-PRO CÔNG DÂN SERVICE - MAIN APPLICATION
 * Port REST: 8082
 * Port gRPC: 9082
 * ================================================================
 */
@SpringBootApplication
@EnableDiscoveryClient   // Đăng ký với Eureka
@EnableCaching           // Bật Redis Cache
public class CitizenApplication {

    public static void main(String[] args) {
        SpringApplication.run(CitizenApplication.class, args);
    }



}