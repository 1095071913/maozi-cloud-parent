package com.jiumao.swagger.config;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.github.xiaoymin.knife4j.core.util.AnnotationUtils;
import com.jiumao.enums.config.annotation.SwaggerDisplayEnum;
import com.jiumao.factory.BaseResultFactory;

import springfox.documentation.builders.ModelPropertyBuilder;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;

@Component
public class EnumModelPropertyBuilderPlugin implements ModelPropertyBuilderPlugin {

    @Override
    public void apply(ModelPropertyContext context) {
    	
        Optional<BeanPropertyDefinition> optional = context.getBeanPropertyDefinition();
        if (!optional.isPresent()) {
            return;
        }

        BeanPropertyDefinition beanPropertyDefinition = optional.get();
        
        AnnotatedField field = beanPropertyDefinition.getField();
        
        if(BaseResultFactory.isNotNull(field)) {
        	addDescForEnum(context, field.getRawType());
        }
        
    }

    @Override
    public boolean supports(DocumentationType delimiter) {
        return true;
    }
	
    private void addDescForEnum(ModelPropertyContext context, Class<?> fieldType) {
        if (Enum.class.isAssignableFrom(fieldType)) {
            SwaggerDisplayEnum annotation = AnnotationUtils.findAnnotation(fieldType, SwaggerDisplayEnum.class).get();
            if (annotation != null) {
                String index = annotation.index();
                String name = annotation.name();
                
                Object[] enumConstants = fieldType.getEnumConstants();

                List<String> displayValues =
                        Arrays.stream(enumConstants)
                                .filter(Objects::nonNull)
                                .map(item -> {
                                    Class<?> currentClass = item.getClass();

                                    Field indexField = ReflectionUtils.findField(currentClass, index);
                                    ReflectionUtils.makeAccessible(indexField);
                                    Object value = ReflectionUtils.getField(indexField, item);

                                    Field descField = ReflectionUtils.findField(currentClass, name);
                                    ReflectionUtils.makeAccessible(descField);
                                    Object desc = ReflectionUtils.getField(descField, item);
                                    return value + ":" + desc;

                                }).collect(Collectors.toList());


                ModelPropertyBuilder builder = context.getBuilder();
                Field descField = ReflectionUtils.findField(builder.getClass(), "description");
                ReflectionUtils.makeAccessible(descField);
                String joinText = ReflectionUtils.getField(descField, builder)
                        + " (" + String.join("; ", displayValues) + ")";

                builder.description(joinText).type(context.getResolver().resolve(Integer.class));
            }
        }

    }
}