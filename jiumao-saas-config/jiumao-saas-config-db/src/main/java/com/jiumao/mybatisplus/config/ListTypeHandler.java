package com.jiumao.mybatisplus.config;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

@MappedTypes({List.class})
@MappedJdbcTypes({JdbcType.VARCHAR})
public abstract class ListTypeHandler<T> extends AbstractJsonTypeHandler<Object> {
	
    protected static ObjectMapper objectMapper = new ObjectMapper();
    
    private Class<?> type;

    public ListTypeHandler(){}

    public ListTypeHandler(Class<?> type) {
        this.type = type;
    }

    protected Object parse(String json) {
        try {
            Type type = getSuperGenricTypes(this.getClass());
            JavaType javaType = objectMapper.getTypeFactory().constructType(type);
            return objectMapper.readValue(json, javaType);
        } catch (IOException var3) {
            throw new RuntimeException(var3);
        }
    }

    protected String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException var3) {
            throw new RuntimeException(var3);
        }
    }
    
    public static Type getSuperGenricTypes(final Class<?> clz) {
        Class<?> superClass = clz;
        Type type = superClass.getGenericSuperclass();
        while (superClass != Object.class && !(type instanceof ParameterizedType)) {
            superClass = superClass.getSuperclass();
            type = superClass.getGenericSuperclass();
        }
        if (superClass == Object.class) {
            throw new IllegalArgumentException("父类不含泛型类型：" + clz);
        }
        return ((ParameterizedType) type).getActualTypeArguments()[0];
    }
    
}