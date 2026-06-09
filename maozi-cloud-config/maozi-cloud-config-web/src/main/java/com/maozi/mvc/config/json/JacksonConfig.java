package com.maozi.mvc.config.json;

import cn.hutool.core.util.ClassUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maozi.common.JacksonUtil;
import com.maozi.common.context.ApplicationEnvironmentContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/**
 * Jackson JSON 序列化配置
 * <p>
 * 配置全局 ObjectMapper 实例，初始化自定义序列化/反序列化规则，
 * 并扫描项目包下所有枚举类统一设置为整数格式输出（{@code JsonFormat.Shape.NUMBER_INT}）。
 * </p>
 *
 * @author maozi
 */
@Configuration
public class JacksonConfig {

	/**
	 * 创建全局 Jackson ObjectMapper
	 *
	 * @param builder Jackson 构建器
	 * @return 配置好的 ObjectMapper 实例
	 */
	@Bean
	public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {

		ObjectMapper objectMapper = builder.createXmlMapper(false).build();

		JacksonUtil.initObjectMapperConfig(objectMapper);

		return objectMapper;

	}

	/**
	 * 创建 HTTP 消息转换器，配置枚举类以整数格式序列化
	 *
	 * @param objectMapper 全局 ObjectMapper 实例
	 * @return 配置好的 MappingJackson2HttpMessageConverter
	 */
	@Bean
    public MappingJackson2HttpMessageConverter httpMessageConverter(ObjectMapper objectMapper) {

        ClassUtil.scanPackage(ApplicationEnvironmentContext.PACKAGE_PREFIX).forEach(item -> {
        	if(item.isEnum()) {
        		objectMapper.configOverride(item).setFormat(JsonFormat.Value.forShape(JsonFormat.Shape.NUMBER_INT));
        	}
        });

        return new MappingJackson2HttpMessageConverter(objectMapper);

    }

}
