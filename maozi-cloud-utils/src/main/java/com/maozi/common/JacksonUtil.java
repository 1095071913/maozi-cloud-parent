package com.maozi.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

@Slf4j
public class JacksonUtil {

	@Getter
    private static final ObjectMapper objectMapper;

	static{
		objectMapper = initObjectMapperConfig();
	}

	public static ObjectMapper initObjectMapperConfig(){
		return initObjectMapperConfig(new ObjectMapper());
	}

	public static ObjectMapper initObjectMapperConfig(ObjectMapper objectMapper){

		if(ObjectUtil.isNullEmpty(objectMapper)){
			return null;
		}

		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

		objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		objectMapper.setDateFormat(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		objectMapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
		objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);

		SimpleModule module = new SimpleModule();
		// Long 序列化为字符串，防止前端精度丢失
		module.addSerializer(Long.class, ToStringSerializer.instance);
		module.addSerializer(Long.TYPE, ToStringSerializer.instance);
		// 空字符串反序列化为 null
		module.addDeserializer(String.class, new StdDeserializer<>(String.class) {
			@Override
			public String deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
				String result = StringDeserializer.instance.deserialize(jsonParser, deserializationContext);
				if (ObjectUtil.isNullEmpty(result)) {
					return null;
				}
				return result;
			}
		});

		// 注册 JavaTimeModule，支持 Java 8 日期时间类型序列化/反序列化
		JavaTimeModule javaTimeModule = new JavaTimeModule();
		javaTimeModule.addSerializer(LocalDateTime.class,new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		javaTimeModule.addDeserializer(LocalDateTime.class,new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		objectMapper.registerModule(javaTimeModule);
		objectMapper.registerModule(module);

		return objectMapper;

	}

    public static String objectToJson(Object data){

		try {return objectMapper.writeValueAsString(data);} catch (JsonProcessingException e) {
			log.error(e.getLocalizedMessage(),e);
			return null;
		}

	}
	
	public static <T> T jsonToObject(String jsonString, Class<T> type){

		try {return objectMapper.readValue(jsonString, type);} catch (JsonProcessingException e) {
			log.error(e.getLocalizedMessage(),e);
			return null;
		}

	}

	public static <T> List<T> jsonToList(String json) {

		TypeReference<List<T>> typeReference = new TypeReference<>() {};

		try {return objectMapper.readValue(json,typeReference);} catch (Exception e) {
			log.error(e.getLocalizedMessage(),e);
			return null;
		}

	}

	public static <T> Map<String, T> jsonToMap(String json) {

		TypeReference<Map<String, T>> typeReference = new TypeReference<>() {};

		try {return objectMapper.readValue(json,typeReference);} catch (Exception e) {
			log.error(e.getLocalizedMessage(),e);
			return null;
		}

	}

	public static <T> Map<String, T> objectToMap(Object data) {

		TypeReference<Map<String, T>> typeReference = new TypeReference<>() {};

		try {return objectMapper.convertValue(data,typeReference);} catch (Exception e) {
			log.error(e.getLocalizedMessage(),e);
			return null;
		}

	}

	public static <T> T mapToObject(Map<String, ?> data, Class<T> type) {

		try {return objectMapper.convertValue(data,type);} catch (Exception e) {
			log.error(e.getLocalizedMessage(),e);
			return null;
		}

	}

	public static <T> T mapToObject(Map<String, ?> data, Type type) {

		JavaType constructType = TypeFactory.defaultInstance().constructType(type);

		try {return objectMapper.convertValue(data,constructType);} catch (Exception e) {
			log.error(e.getLocalizedMessage(),e);
			return null;
		}

	}

	public static <T> List<T> mapListToListObject(List<Map<String,Object>> data) {

		TypeReference<List<T>> typeReference = new TypeReference<>() {};

		try {return objectMapper.convertValue(data,typeReference);} catch (Exception e) {
			log.error(e.getLocalizedMessage(),e);
			return null;
		}

	}

	public static <T> List<Map<String,Object>> listObjectToMapList(List<?> data) {

		TypeReference<List<Map<String,Object>>> typeReference = new TypeReference<>() {};

		try {return objectMapper.convertValue(data,typeReference);} catch (Exception e) {
			log.error(e.getLocalizedMessage(),e);
			return null;
		}

	}
	
}
