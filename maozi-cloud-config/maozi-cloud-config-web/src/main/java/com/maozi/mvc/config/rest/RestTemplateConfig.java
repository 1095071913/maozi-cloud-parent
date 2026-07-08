package com.maozi.mvc.config.rest;

import jakarta.annotation.Nonnull;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;

@Configuration
public class RestTemplateConfig {

	@Bean
	// @SentinelRestTemplate(fallback = "fallback", fallbackClass =
	// BaseResultFactory.class,
	// blockHandler="handleException",blockHandlerClass=BaseResultFactory.class)
	public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
		RestTemplate restTemplate = restTemplateBuilder.build(RestTemplate.class);
		restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
			@Override
			public void handleError(@Nonnull ClientHttpResponse response) throws IOException {
				if (response.getStatusCode().value() != 400) {
					super.handleError(response);
				}
			}
		});
		return restTemplate;
	}
	
}