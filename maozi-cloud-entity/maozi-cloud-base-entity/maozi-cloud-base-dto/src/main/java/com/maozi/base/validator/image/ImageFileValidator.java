package com.maozi.base.validator.image;

import com.maozi.common.ObjectUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * 图片文件校验器
 * <p>
 * 实现 {@link ConstraintValidator} 接口，校验上传文件是否为图片格式。
 * 通过检查 Content-Type 是否以 "image" 开头来判断。
 * 文件为空时跳过校验。
 * </p>
 *
 * @author maozi
 */
public class ImageFileValidator implements ConstraintValidator<ImageFile, MultipartFile> {

    /**
     * 初始化校验器
     *
     * @param constraintAnnotation 注解实例
     */
    @Override
    public void initialize(ImageFile constraintAnnotation) {}

    /**
     * 校验上传文件是否为图片格式
     *
     * @param file 上传的文件
     * @param context 校验上下文
     * @return 文件为空时返回 true（跳过校验），否则返回 Content-Type 是否以 "image" 开头
     */
    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {

        if(ObjectUtil.isNullEmpty(file) || file.isEmpty()) {
            return true;
        }

        String contentType = file.getContentType();
        return StringUtils.isNotEmpty(contentType) && contentType.startsWith("image");

    }

}
