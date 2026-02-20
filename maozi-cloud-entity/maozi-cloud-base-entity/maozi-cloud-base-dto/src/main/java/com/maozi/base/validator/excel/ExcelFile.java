package com.maozi.base.validator.excel;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ExcelFileValidator.class)
public @interface ExcelFile {

    String message() default "非表格类型";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}