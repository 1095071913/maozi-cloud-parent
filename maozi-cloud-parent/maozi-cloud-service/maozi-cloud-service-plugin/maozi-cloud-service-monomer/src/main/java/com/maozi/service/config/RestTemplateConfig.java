package com.maozi.service.config;

import com.maozi.mvc.config.rest.RestTemplate;
import com.maozi.mvc.config.rest.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author pengjinlong
 * @since 2026/10/12 20:06
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(org.springframework.boot.web.client.RestTemplateBuilder restTemplateBuilder) {
        return RestTemplateBuilder.buildRestTemplate(restTemplateBuilder);
    }

}
