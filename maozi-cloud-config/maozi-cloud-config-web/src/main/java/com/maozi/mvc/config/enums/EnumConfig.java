package com.maozi.mvc.config.enums;

import cn.hutool.core.util.ClassUtil;
import com.google.common.collect.Lists;
import com.maozi.base.BaseEnum;
import com.maozi.utils.context.ApplicationEnvironmentContext;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Component
public class EnumConfig {

    public static Map<String, List<BaseEnum>> enums = new HashMap<String, List<BaseEnum>>();

    public EnumConfig(){

        initEnum("com.maozi.base.enums");

        initEnum("com.maozi."+ ApplicationEnvironmentContext.applicationProjectAbbreviation);

    }

    private void initEnum(String packageName){

        ClassUtil.scanPackage(packageName).stream()

            .forEach(item -> {

                if (item.isEnum()) {

                    BaseEnum[] enumConstants = (BaseEnum[]) item.getEnumConstants();

                    char[] charArray = item.getSimpleName().toCharArray();

                    charArray[0] += 32;

                    enums.put(new String(charArray), Lists.newArrayList(enumConstants));

                }

            });

    }

}
