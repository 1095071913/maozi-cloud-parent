package com.maozi.base.validator.image;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ImageFileValidator.class)
public @interface ImageFile {

    String message() default "非图片类型";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}