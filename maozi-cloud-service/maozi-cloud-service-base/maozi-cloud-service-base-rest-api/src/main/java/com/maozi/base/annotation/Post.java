package com.maozi.base.annotation;

import io.swagger.v3.oas.annotations.Operation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Operation
@RequestMapping(method = {RequestMethod.POST})
public @interface Post {

    @AliasFor(annotation = RequestMapping.class)
    String[] value() default {};

    @AliasFor(value = "summary",annotation = Operation.class)
    String description() default "";

}
