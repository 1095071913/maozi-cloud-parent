package com.maozi.db.handler;

import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maozi.utils.MapperUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

@MappedTypes({List.class})
@MappedJdbcTypes({JdbcType.VARCHAR})
public abstract class ListTypeHandler<T> extends AbstractJsonTypeHandler<Object> {
    
    private Class<?> type;

    public ListTypeHandler(){}

    public ListTypeHandler(Class<?> type) {
        this.type = type;
    }

    protected Object parse(String json) {

        try {

            ObjectMapper objectMapper = MapperUtils.getObjectMapper();

            Type type = getSuperGenricTypes(this.getClass());

            JavaType javaType = objectMapper.getTypeFactory().constructType(type);

            return objectMapper.readValue(json, javaType);

        } catch (IOException var3) {
            throw new RuntimeException(var3);
        }

    }

    protected String toJson(Object obj) {

        try {return MapperUtils.getObjectMapper().writeValueAsString(obj);} catch (JsonProcessingException var3) {
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