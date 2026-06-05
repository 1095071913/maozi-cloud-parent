package com.maozi.base.plugin.context;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Data
@Component
@RefreshScope(proxyMode = ScopedProxyMode.NO)
public class QueryEnvironmentContext {

    public static String DEFAULT_ORDER_KEY;
    @Value("${mybatis-plus-join.table-alias}")
    public void setDefaultTableAlias(String defaultOrderKey) {
        QueryEnvironmentContext.DEFAULT_ORDER_KEY = defaultOrderKey;
    }

}
