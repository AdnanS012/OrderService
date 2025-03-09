package org.example.orderservice.Config;

import org.example.orderservice.Service.FulfillmentClient;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .basicAuthentication("admin", "admin123") // Set Basic Auth globally
                .build();
    }
    @Bean
    public FulfillmentClient fulfillmentClient() {
        String username = "delivery1";
        String password = "password123";
        return new FulfillmentClient(username, password);
    }
}
