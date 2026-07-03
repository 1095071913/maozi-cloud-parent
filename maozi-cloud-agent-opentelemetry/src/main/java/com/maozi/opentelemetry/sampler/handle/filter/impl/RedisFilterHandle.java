package com.maozi.opentelemetry.sampler.handle.filter.impl;

import com.maozi.opentelemetry.sampler.handle.filter.FilterHandle;
import io.opentelemetry.api.common.AttributeType;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.internal.InternalAttributeKeyImpl;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Objects;

@Data
@EqualsAndHashCode(callSuper = true)
public class RedisFilterHandle extends FilterHandle {

    private final List<String> names = List.of("redis");

    private final List<String> EXCLUDED_STATEMENT = List.of(
            "PING",
            "AUTH ?",
            "INFO server",
            "HELLO 3 AUTH ? ?"
    );

    @Override
    public Boolean filter(Attributes attributes) {

        String type = attributes.get(InternalAttributeKeyImpl.create("db.system", AttributeType.STRING));
        if(Objects.isNull(type) || !getNames().contains(type)){
            return false;
        }

        String statement = attributes.get(InternalAttributeKeyImpl.create("db.statement", AttributeType.STRING));
        return EXCLUDED_STATEMENT.contains(statement);

    }

}
