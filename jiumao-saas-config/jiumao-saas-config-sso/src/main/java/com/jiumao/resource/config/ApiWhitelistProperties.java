package com.jiumao.resource.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import lombok.Data;


@Data
@Component
public class ApiWhitelistProperties {
	
    @Value("${application-project-whitelist:#{null}}")
    private List<String> whitelist = Lists.newArrayList();
    
    private List<String> defaultWitelist = new ArrayList<String>(){
    	{
    		add("/oauth/check_token");
    		add("/webjars/**");
    		add("/actuator");
    		
    		add("/actuator/**");
    		add("/application/**");
    		
			add("/doc.html");
			add("/v2/api-docs/**");
			add("/swagger-ui.html/**");
			add("/swagger-ui.html");
			add("/swagger-resources/**");
			add("/oss/**");
			
    	}
    };
    
}