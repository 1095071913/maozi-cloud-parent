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

/**
 * 数据库存储类扫描器
 * <p>
 * 应用启动时扫描项目包下所有标注了 {@link TableName} 注解的类，
 * 将表名到类类型的映射注册到 {@link StoreClass} 中，
 * 供运行时动态映射使用。
 * </p>
 *
 * @author maozi
 */
@Component
public class StoreClassDBScan implements ApplicationRunner {

    /**
     * 应用启动时执行数据库实体类扫描
     *
     * @param args 应用启动参数
     * @throws Exception 扫描异常
     */
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
