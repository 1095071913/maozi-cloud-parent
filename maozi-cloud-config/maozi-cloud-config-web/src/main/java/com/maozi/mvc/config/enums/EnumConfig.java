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

/**
 * 枚举配置
 * <p>
 * 启动时扫描基础枚举包和当前项目枚举包下所有实现了 {@link BaseEnum} 接口的枚举类，
 * 将枚举类名（首字母小写）作为键、枚举常量列表作为值，注册到全局枚举映射表中，
 * 供前端下拉选择等场景使用。
 * </p>
 *
 * @author maozi
 */
@Data
@Component
public class EnumConfig {

    /** 全局枚举映射表，键为枚举类名（首字母小写），值为枚举常量列表 */
    public final static Map<String, List<BaseEnum>> enums = CollectionUtil.newHashMap();

    /**
     * 构造方法，初始化基础枚举和项目枚举
     */
    public EnumConfig(){

        initEnum(ApplicationEnvironmentContext.PACKAGE_PREFIX + ".base.enums");

        initEnum(ApplicationEnvironmentContext.PACKAGE_PREFIX + "." + ApplicationEnvironmentContext.APPLICATION_PROJECT_ABBREVIATION);

    }

    /**
     * 扫描指定包下的枚举类并注册到映射表
     *
     * @param packageName 需要扫描的包路径
     */
    private void initEnum(String packageName){

        ClassUtil.scanPackage(packageName)

            .forEach(item -> {

                if (item.isEnum() && item.getEnumConstants() instanceof BaseEnum[] enumConstants) {

                    // 将类名首字母转为小写作为映射键
                    char[] charArray = item.getSimpleName().toCharArray();

                    charArray[0] += 32;

                    enums.put(new String(charArray), Lists.newArrayList(enumConstants));

                }

            });

    }

}
