package com.maozi.base.plugin.context;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

/**
 * 查询环境上下文
 * <p>
 * 存储查询相关的全局配置参数，如默认表别名。
 * 使用 {@link RefreshScope} 支持配置动态刷新。
 * </p>
 *
 * @author maozi
 */
@Data
@Component
@RefreshScope(proxyMode = ScopedProxyMode.NO)
public class QueryEnvironmentContext {

    /** 默认排序键（表别名） */
    public static String DEFAULT_ORDER_KEY;

    /**
     * 设置默认表别名
     *
     * @param defaultOrderKey 从配置文件中读取的表别名
     */
    @Value("${mybatis-plus-join.table-alias}")
    public void setDefaultTableAlias(String defaultOrderKey) {
        QueryEnvironmentContext.DEFAULT_ORDER_KEY = defaultOrderKey;
    }

}
