package com.maozi.base.plugin;

import cn.hutool.core.util.ClassUtil;
import com.baomidou.mybatisplus.annotation.TableName;
import com.maozi.base.enums.StoreClassType;
import com.maozi.common.CollectionUtil;
import com.maozi.common.context.ApplicationEnvironmentContext;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class StoreClassDBScan implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {

        Map<String, Class<?>> storeClassDBMap = CollectionUtil.newHashMap();

        ClassUtil.scanPackage(ApplicationEnvironmentContext.PACKAGE_PREFIX + "." + ApplicationEnvironmentContext.APPLICATION_PROJECT_ABBREVIATION).forEach(clazz ->{

            if(clazz.isAnnotationPresent(TableName.class)){

                TableName annotation = clazz.getAnnotation(TableName.class);

                storeClassDBMap.put(annotation.value(),clazz);

            }

        });

        StoreClass.storeClassMap.put(StoreClassType.DB,storeClassDBMap);

    }

}
