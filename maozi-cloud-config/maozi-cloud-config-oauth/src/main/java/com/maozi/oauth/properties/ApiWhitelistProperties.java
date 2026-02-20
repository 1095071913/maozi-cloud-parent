package com.maozi.oauth.properties;

import com.google.common.collect.Lists;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Data
@Component
public class ApiWhitelistProperties {
	
    @Value("${application-project-whitelist:#{null}}")
    private List<String> whitelist = Lists.newArrayList();
    
    private List<String> defaultWitelist = new ArrayList<>(){

		{
    		add("/oauth/check_token");
    		add("/webjars/**");
    		
    		add("/actuator/**");
    		add("/application/**");

			add("/v3/api-docs/**");
			
    	}

    };
    
}