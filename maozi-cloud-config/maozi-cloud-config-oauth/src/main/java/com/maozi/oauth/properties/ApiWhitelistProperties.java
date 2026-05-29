package com.maozi.oauth.properties;

import com.maozi.common.CollectionUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;


@Data
@Component
public class ApiWhitelistProperties {

	public final static List<String> DEFAULT_WITE_LIST;

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