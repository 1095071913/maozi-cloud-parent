package com.maozi.base.validator.excel;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ExcelFileValidator implements ConstraintValidator<ExcelFile, MultipartFile> {

    @Override
    public void initialize(ExcelFile constraintAnnotation) {}

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {

        if(file == null || file.isEmpty()) {
            return true;
        }
        
        String contentType = file.getContentType();
        return "application/vnd.ms-excel".equals(contentType) || "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(contentType);

    }

}