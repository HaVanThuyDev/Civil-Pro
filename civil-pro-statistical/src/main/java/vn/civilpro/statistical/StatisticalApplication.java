package vn.civilpro.statistical;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class StatisticalApplication {

    public static void main(String[] args) {
        SpringApplication.run(StatisticalApplication.class, args);
    }
}
