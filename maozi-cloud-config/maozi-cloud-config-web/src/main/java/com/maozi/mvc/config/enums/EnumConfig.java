package com.maozi.mvc.config.enums;

import cn.hutool.core.util.ClassUtil;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import com.maozi.base.BaseEnum;
import com.maozi.common.CollectionUtil;
import com.maozi.common.context.ApplicationEnvironmentContext;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Data
@Component
public class EnumConfig {

    public final static Map<String, List<BaseEnum>> enums = CollectionUtil.newHashMap();

    public EnumConfig(){

        initEnum(ApplicationEnvironmentContext.PACKAGE_PREFIX + ".base.enums");

        initEnum(ApplicationEnvironmentContext.PACKAGE_PREFIX + "." + ApplicationEnvironmentContext.APPLICATION_PROJECT_ABBREVIATION);

    }

    private void initEnum(String packageName){

        ClassUtil.scanPackage(packageName)

            .forEach(item -> {

                if (item.isEnum() && item.getEnumConstants() instanceof BaseEnum[] enumConstants) {

                    char[] charArray = item.getSimpleName().toCharArray();

                    charArray[0] += 32;

                    enums.put(new String(charArray), Lists.newArrayList(enumConstants));

                }

            });

    }

}
