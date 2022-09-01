package com.jiumao.mvc.filter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiumao.mvc.config.json.ReadOnlyMultipartFormDataEndpointConverter;

@Configuration
public class WebFilterConfig implements WebMvcConfigurer {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private CleanThreadLoadFilter loginInterceptor;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(loginInterceptor).addPathPatterns("/**");
	}

	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {

		ReadOnlyMultipartFormDataEndpointConverter converter = new ReadOnlyMultipartFormDataEndpointConverter(objectMapper);
		List<MediaType> supportedMediaTypes = new ArrayList<>();
		supportedMediaTypes.addAll(converter.getSupportedMediaTypes());
		supportedMediaTypes.add(MediaType.APPLICATION_OCTET_STREAM);
		converter.setSupportedMediaTypes(supportedMediaTypes);

		converters.add(converter);
	}

}