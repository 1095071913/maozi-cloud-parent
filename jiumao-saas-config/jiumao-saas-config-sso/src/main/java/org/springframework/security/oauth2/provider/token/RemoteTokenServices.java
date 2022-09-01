/*******************************************************************************
 *     Cloud Foundry
 *     Copyright (c) [2009-2014] Pivotal Software, Inc. All Rights Reserved.
 *
 *     This product is licensed to you under the Apache License, Version 2.0 (the "License").
 *     You may not use this product except in compliance with the License.
 *
 *     This product includes a number of subcomponents with
 *     separate copyright notices and license terms. Your use of these
 *     subcomponents is subject to the terms and conditions of the
 *     subcomponent's license, as noted in the LICENSE file.
 *******************************************************************************/
package org.springframework.security.oauth2.provider.token;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.jiumao.factory.BaseResultFactory;
import com.jiumao.factory.result.AbstractBaseResult;
import com.jiumao.factory.result.error.ErrorResult;
import com.jiumao.resource.config.OauthTokenServiceConfig;
import com.jiumao.tool.SpringUtil;

/**
 * Queries the /check_token endpoint to obtain the contents of an access token.
 *
 * If the endpoint returns a 400 response, this indicates that the token is invalid.
 *
 * @author Dave Syer
 * @author Luke Taylor
 *
 */

public class RemoteTokenServices implements ResourceServerTokenServices {

	private RestTemplate restTemplate;

	private String checkTokenEndpointUrl;

	private String clientId;

	private String clientSecret;

    private String tokenName = "token";

	private AccessTokenConverter tokenConverter = new DefaultAccessTokenConverter();
	
	private OauthTokenServiceConfig oauthTokenServiceConfig;

	public RemoteTokenServices() {
		
		oauthTokenServiceConfig=SpringUtil.getBean("oauthTokenServiceConfig");
		
		restTemplate = SpringUtil.getBean("restTemplate");
		
	}

	public void setCheckTokenEndpointUrl(String checkTokenEndpointUrl) {
		this.checkTokenEndpointUrl = checkTokenEndpointUrl;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public void setAccessTokenConverter(AccessTokenConverter accessTokenConverter) {
		this.tokenConverter = accessTokenConverter;
	}

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }

    @Override
	public OAuth2Authentication loadAuthentication(String accessToken) throws AuthenticationException, InvalidTokenException {
    	
    	Map<String,Object> checkUserResult=null;
    		
    	Long returnTime = System.currentTimeMillis(); 
    		
    	if(checkTokenEndpointUrl.indexOf("http")!=-1) {
    		
    		MultiValueMap<String, String> formData = new LinkedMultiValueMap<String, String>();
    		
    		formData.add(tokenName, accessToken);   
    		
    		HttpHeaders headers = new HttpHeaders();      
    		
    		headers.set("Authorization", getAuthorizationHeader(clientId, clientSecret));
    		
    		checkUserResult = postForMap(checkTokenEndpointUrl, formData, headers); 
    		
    		if(BaseResultFactory.isNull(checkUserResult)) {  
    			throw new InvalidTokenException(Long.toString(System.currentTimeMillis() - returnTime)+"|"+"服务未启动(sso)");      
    		}   
    		
    	}else {
    		
    	    AbstractBaseResult<Map> checkTokenResult = oauthTokenServiceConfig.oauthTokenServiceRpc.checkToken(accessToken);
    	    
    	    if(!checkTokenResult.isSuccess()) {
    			throw new InvalidTokenException(((ErrorResult)checkTokenResult).getMessage());   
    		} 
    	    	
    	    checkUserResult=checkTokenResult.getData();   
    	    
    	}
    	
    		        
    	if (checkUserResult.containsKey("error") || !Boolean.TRUE.equals(checkUserResult.get("active"))) { 
    		throw new InvalidTokenException(Long.toString(System.currentTimeMillis() - returnTime)+"|"+"Token错误");
    	} 
    	
		return tokenConverter.extractAuthentication(checkUserResult); 
	}

	@Override
	public OAuth2AccessToken readAccessToken(String accessToken) {
		throw new UnsupportedOperationException("Not supported: read access token");
	}

	private String getAuthorizationHeader(String clientId, String clientSecret) {

		String creds = String.format("%s:%s", clientId, clientSecret);
		try {
			return "Basic " + new String(Base64.encode(creds.getBytes("UTF-8")));
		}
		catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("Could not convert String");
		}
	}
 
	private Map<String, Object> postForMap(String path, MultiValueMap<String, String> formData, HttpHeaders headers) {
		 
		if (headers.getContentType() == null) { 
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED); 
		} 

		ResponseEntity<Map> exchange = restTemplate.exchange(path, HttpMethod.POST,new HttpEntity<MultiValueMap<String, String>>(formData, headers), Map.class);
		
		if(BaseResultFactory.isNull(exchange)) {
			return null;  
		}
		
		return exchange.getBody();     
		
	} 

}
