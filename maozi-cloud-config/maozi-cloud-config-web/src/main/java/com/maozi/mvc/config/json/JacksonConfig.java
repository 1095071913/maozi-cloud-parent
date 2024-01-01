package com.maozi.mvc.config.json;

import cn.hutool.core.util.ClassUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Configuration
public class JacksonConfig {

	@Bean
	public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
		
		ObjectMapper objectMapper = builder.createXmlMapper(false).build();
		
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		
        objectMapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
        objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        
        SimpleModule module = new SimpleModule();
        
        module.addSerializer(Long.class, ToStringSerializer.instance);
        
        module.addSerializer(Long.TYPE, ToStringSerializer.instance);
        
        module.addDeserializer(String.class, new StdDeserializer<String>(String.class) {
        	
    	    @Override
    	    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
    	        
    	    	String result = StringDeserializer.instance.deserialize(p, ctxt);
    	    	
    	        if (StringUtils.isEmpty(result)) {
    	            return null;
    	        }
    	        
    	        return result;
    	        
    	    }
    	    
    	});
        
        objectMapper.registerModule(module);
		
		return objectMapper;
		
	}

	@Bean
    public MappingJackson2HttpMessageConverter httpMessageConverter(ObjectMapper objectMapper) {
		
        ClassUtil.scanPackage("com.maozi").stream().forEach(item -> {
        	
        	if(item.isEnum()) {
        		objectMapper.configOverride(item).setFormat(JsonFormat.Value.forShape(JsonFormat.Shape.OBJECT));
        	}
        	
        });
        
        return new MappingJackson2HttpMessageConverter(objectMapper);
        
    }

}
