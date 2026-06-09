package com.maozi.oauth.properties;

import com.maozi.common.CollectionUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * API 白名单配置属性
 * <p>
 * 定义不需要认证即可访问的 API 路径列表。包含系统默认白名单
 * （OAuth2 内省端点、Swagger 文档、Actuator 监控端点等）和
 * 通过配置文件自定义的项目级白名单。
 * </p>
 *
 * @author maozi
 */
@Data
@Component
public class ApiWhitelistProperties {

    /** 系统默认白名单路径列表 */
    public final static List<String> DEFAULT_WITE_LIST;

    /** 项目自定义白名单路径列表，通过 application-project-whitelist 配置 */
    @Value("${application-project-whitelist:#{null}}")
    private List<String> configWhitelist;

    static {

        DEFAULT_WITE_LIST = CollectionUtil.newArrayList();

        DEFAULT_WITE_LIST.add("/oauth2/introspect");
        DEFAULT_WITE_LIST.add("/webjars/**");
        DEFAULT_WITE_LIST.add("/actuator/**");
        DEFAULT_WITE_LIST.add("/application/**");
        DEFAULT_WITE_LIST.add("/v3/api-docs/**");

    }

}
