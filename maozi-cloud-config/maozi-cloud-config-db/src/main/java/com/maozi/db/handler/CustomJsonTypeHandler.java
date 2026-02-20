package com.maozi.db.handler;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.maozi.utils.MapperUtils;

import java.io.IOException;

public class CustomJsonTypeHandler extends JacksonTypeHandler {

    private final Class<?> type;

    public CustomJsonTypeHandler(Class<?> type) {

        super(type);

        this.type = type;

    }

    protected Object parse(String json) {

        try {
            return MapperUtils.getObjectMapper().readValue(json, this.type);
        } catch (IOException var3) {
            IOException e = var3;
            throw new RuntimeException(e);
        }

    }

    protected String toJson(Object obj) {

        try {
            return MapperUtils.getObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException var3) {
            JsonProcessingException e = var3;
            throw new RuntimeException(e);
        }

    }

}
