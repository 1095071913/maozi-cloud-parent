package com.jiumao.tool;

import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.*;

public class ValidatorUtils {
	
    private static Validator validator =  Validation.buildDefaultValidatorFactory().getValidator();

    public static void beanValidate(@Valid Object obj) throws IllegalArgumentException {

        if (obj instanceof Collection){
            ValidCollection validCollection = new ValidCollection();
            validCollection.setList((Collection) obj);
            validate(validCollection);
        }else {
            validate(obj);
        }
    }

    public static void validate(@Valid Object obj) {
        Map<String, String> validatedMsg = new HashMap<>();
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(obj);
        for (ConstraintViolation<Object> c : constraintViolations) {
            validatedMsg.put(c.getPropertyPath().toString(), c.getMessage());
        }
        Assert.isTrue(ObjectUtils.isEmpty(constraintViolations), validatedMsg.toString());
    }

    /**
     * 导入时验证。若不存在，抛出异常
     * @param addDtos
     */
    public static void verifyImportParam(List<?> addDtos) {
    	
        ArrayList<String> list = new ArrayList<>();
        
        StringBuilder sb = null;

        Validator validator =  Validation.buildDefaultValidatorFactory().getValidator();
        for (int i = 1; i <= addDtos.size(); i++) {
            sb = new StringBuilder();
            Object item = addDtos.get(i - 1);
            Set<ConstraintViolation<Object>> constraintViolations = validator.validate(item);
            for (ConstraintViolation<Object> c : constraintViolations) {
                sb.append(c.getPropertyPath().toString()).append(":").append(c.getMessage()).append(";");
            }
            if (!StringUtils.isEmpty(sb.toString())) {
                list.add("第" + i + "条：" + sb.toString());
            }
        }
        Assert.isTrue(ObjectUtils.isEmpty(list), list.toString());
    }

    private static class ValidCollection<T> {
        @Valid
        private Collection<T> list;

        public Collection<T> getList() {
            return list;
        }

        public void setList(Collection<T> list) {
            this.list = list;
        }
    }
}
