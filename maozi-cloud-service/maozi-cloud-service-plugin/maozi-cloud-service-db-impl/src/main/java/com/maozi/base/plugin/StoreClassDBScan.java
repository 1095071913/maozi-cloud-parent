package com.maozi.base.plugin;

import cn.hutool.core.util.ClassUtil;
import com.baomidou.mybatisplus.annotation.TableName;
import com.google.common.collect.Maps;
import com.maozi.base.enums.StoreClassType;
import com.maozi.utils.context.ApplicationEnvironmentContext;
import java.util.HashMap;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class StoreClassDBScan implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {

        HashMap<String, Class<?>> storeClassDBMap = Maps.newHashMap();

        ClassUtil.scanPackage("com.maozi."+ ApplicationEnvironmentContext.applicationProjectAbbreviation).stream().forEach(clazz ->{

            if(clazz.isAnnotationPresent(TableName.class)){

                TableName annotation = clazz.getAnnotation(TableName.class);

                storeClassDBMap.put(annotation.value(),clazz);

            }

        });

        StoreClass.storeClassMap.put(StoreClassType.db,storeClassDBMap);

    }

}
