package com.maozi.base.validator.video;

import com.maozi.common.ObjectUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public class VideoFileValidator implements ConstraintValidator<VideoFile, MultipartFile> {

    @Override
    public void initialize(VideoFile constraintAnnotation) {}

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {

        if(ObjectUtil.isNullEmpty(file) || file.isEmpty()) {
            return true;
        }

        String contentType = file.getContentType();
        return StringUtils.isNotEmpty(contentType) && contentType.startsWith("video/");

    }

}