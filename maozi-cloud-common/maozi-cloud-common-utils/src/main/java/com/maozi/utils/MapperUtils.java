package com.maozi.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

@Slf4j
@Component
public class MapperUtils {

	private static ObjectMapper objectMapper = new ObjectMapper();

	static{
		MapperUtils.setObjectMapperConfig(objectMapper);
	}

	public static void setObjectMapperConfig(ObjectMapper objectMapper){

		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

		objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		objectMapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
		objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);

		SimpleModule module = new SimpleModule();

		module.addSerializer(Long.class, ToStringSerializer.instance);

		module.addSerializer(Long.TYPE, ToStringSerializer.instance);

		module.addDeserializer(String.class, new StdDeserializer<>(String.class) {

			@Override
			public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {

				String result = StringDeserializer.instance.deserialize(p, ctxt);

				if (StringUtils.isEmpty(result)) {
					return null;
				}

				return result;

			}

		});

		objectMapper.registerModule(module);

	}

	public static ObjectMapper getObjectMapper(){
		return objectMapper;
	}

	public static String objToJson(Object obj){

		try {return objectMapper.writeValueAsString(obj);} catch (JsonProcessingException e) {
			log.error(e.getLocalizedMessage());
			return null;
		}

	}
	
	public static <T> T jsonToPojo(String jsonString, Class<T> clazz){

		try {return objectMapper.readValue(jsonString, clazz);} catch (JsonProcessingException e) {
			log.error(e.getLocalizedMessage());
			return null;
		}

	}

	public static <T> Map<String, T> jsonToMap(String jsonString, Class<T> clazz) {

		Map<String, T> map = Maps.newHashMap();
		try {map = objectMapper.readValue(jsonString, new TypeReference<Map<String, T>>() {});} catch (Exception e) {

			log.error(e.getLocalizedMessage());

			return map;

		}

		Map<String, T> result = Maps.newHashMap();

		for (Entry<String, T> entry : map.entrySet()) {
			result.put(entry.getKey(), mapToPojo(entry.getValue(), clazz));
		}

		return result;

	}

	public static <T> T mapToPojo(T t, Class<T> clazz) {
		return objectMapper.convertValue(t, clazz);
	}

	public static <T> T mapToPojo(Map map, Class<T> clazz) {
		return objectMapper.convertValue(map, clazz);
	}
	
	public static <T> T mapToPojo(Map map, Type type) {

		JavaType constructType = TypeFactory.defaultInstance().constructType(type);

		return objectMapper.convertValue(map, constructType);

	}

	public static Map<String, String> pojoToMap(Object pojo) {
		return objectMapper.convertValue(pojo, new TypeReference<Map<String, String>>() {});
	}

	public static String mapToJson(Map map) {

		try {return objectMapper.writeValueAsString(map);}catch (Exception e) {
			log.error(e.getLocalizedMessage());
			return null;
		}

	}

	public static void setResponseBody(HttpServletResponse response,Object data){

		try{objectMapper.writeValue(response.getOutputStream(),data);}catch (Exception e){
			log.error(e.getLocalizedMessage());
		}

	}

	public static JsonNode read(String json) {

		try {return objectMapper.readTree(json);} catch (Exception e) {
			log.error("json解析错误，json：{}", json, e);
			return null;
		}

	}
	
}
