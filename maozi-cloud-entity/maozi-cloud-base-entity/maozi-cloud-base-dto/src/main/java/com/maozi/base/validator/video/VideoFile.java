package com.maozi.base.validator.video;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = VideoFileValidator.class)
public @interface VideoFile {

    String message() default "非视频类型";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}