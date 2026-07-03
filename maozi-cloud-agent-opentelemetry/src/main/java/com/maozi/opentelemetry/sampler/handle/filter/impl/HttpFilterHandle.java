package com.maozi.opentelemetry.sampler.handle.filter.impl;

import com.maozi.opentelemetry.sampler.handle.filter.FilterHandle;
import io.opentelemetry.api.common.AttributeType;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.internal.InternalAttributeKeyImpl;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Data
@EqualsAndHashCode(callSuper = true)
public class HttpFilterHandle extends FilterHandle {

    private List<String> names = List.of(
            "/actuator",
            "/actuator/**",
            "/v3/api-docs/**"
    );

    @Override
    public Boolean filter(Attributes attributes) {

        String httpPath = attributes.get(InternalAttributeKeyImpl.create("url.path", AttributeType.STRING));
        if(Objects.isNull(httpPath)){
            httpPath = attributes.get(InternalAttributeKeyImpl.create("url.full", AttributeType.STRING));
        }

        if(Objects.isNull(httpPath)){
            return false;
        }

        if(httpPath.contains("http://") || httpPath.contains("https://")){

            httpPath = httpPath.split("://")[1];

            int index = httpPath.indexOf("/");

            if(index != -1){

                httpPath = httpPath.substring(index);

                httpPath = httpPath.split("\\?")[0];

            }

        }

        return matchPath(httpPath);

    }

    public boolean matchPath(String path){

        for(String excludedPath : names){

            String regex = excludedPath.replace("**", ".*");
            if(Pattern.matches(regex, path)){
                return true;
            }

        }

        return false;

    }

}
