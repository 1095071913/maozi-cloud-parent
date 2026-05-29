package com.maozi.base.validator.image;

import com.maozi.common.ObjectUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public class ImageFileValidator implements ConstraintValidator<ImageFile, MultipartFile> {

    @Override
    public void initialize(ImageFile constraintAnnotation) {}

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {

        if(ObjectUtil.isNullEmpty(file) || file.isEmpty()) {
            return true;
        }

        String contentType = file.getContentType();
        return StringUtils.isNotEmpty(contentType) && contentType.startsWith("image");

    }

}