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
 * 使用 {@link RefreshScope} 支持配置中心（如 Nacos）动态刷新配置，
 * 无需重启服务即可更新排序相关参数。
 * </p>
 * <p>
 * 配置项从 {@code application.yml} 中的 {@code mybatis-plus-join.table-alias} 读取，
 * 该值作为排序字段的前缀（默认表别名）在 SQL 构建时使用。
 * </p>
 *
 * @author maozi
 */
@Data
@Component
@RefreshScope(proxyMode = ScopedProxyMode.NO)
public class QueryEnvironmentContext {

    /**
     * 默认排序表别名
     * <p>
     * 作为排序字段拼接时的默认前缀（形如 {@code "别名.字段名"}），由配置项
     * {@code mybatis-plus-join.table-alias} 注入。命名 {@code DEFAULT_ORDER_KEY}
     * 强调其作为「排序键前缀」的用途，等同于「默认表别名」。
     * </p>
     */
    public static String DEFAULT_ORDER_KEY;

    /**
     * 设置默认排序表别名
     *
     * @param defaultOrderKey 从配置文件中读取的表别名（排序键前缀）
     */
    @Value("${mybatis-plus-join.table-alias:}")
    public void setDefaultTableAlias(String defaultOrderKey) {
        QueryEnvironmentContext.DEFAULT_ORDER_KEY = defaultOrderKey;
    }

}
