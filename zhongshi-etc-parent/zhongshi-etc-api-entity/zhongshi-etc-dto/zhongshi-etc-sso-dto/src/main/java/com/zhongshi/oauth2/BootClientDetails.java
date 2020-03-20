/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.zhongshi.oauth2;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.ClientDetails;

import com.zhongshi.oauth2.Client;
import com.zhongshi.tool.CommonUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * 
 * 	功能说明：Oauth2重写ClientDetails
 * 
 *	功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 *
 *	创建日期：2019-10-02 ：10:03:00
 *
 *	版权归属：蓝河团队
 *
 *	协议说明：Apache2.0（ 文件顶端 ）
 *
 */

@Data
@SuppressWarnings("unchecked")
public final class BootClientDetails implements ClientDetails {

    private Client client;
    private Set<String> scope;

    public BootClientDetails(Client client) {
        this.client = client;
    }

    public BootClientDetails() {
    }

    @Override
    public String getClientId() {
        return client.getClientId();
    }

    @Override
    public Set<String> getResourceIds() {
        return client.getResourceIds()!=null?
                CommonUtils.transformStringToSet(client.getResourceIds(),String.class):null;
    }

    @Override
    public boolean isSecretRequired() {
        return client.getIsSecretRequired();
    }

    @Override
    public String getClientSecret() {
        return client.getClientSecret();
    }

    @Override
    public boolean isScoped() {
        return client.getIsScoped();
    }

    @Override
    public Set<String> getScope() {

        this.scope = client.getScope()!=null?
                CommonUtils.transformStringToSet(client.getScope(),String.class):null;

        return client.getScope()!=null?
                CommonUtils.transformStringToSet(client.getScope(),String.class):null;
    }

    @Override
    public Set<String> getAuthorizedGrantTypes() {
        return client.getAuthorizedGrantTypes()!=null?
                CommonUtils.transformStringToSet(client.getAuthorizedGrantTypes(),String.class):null;
    }

    @Override
    public Set<String> getRegisteredRedirectUri() {
        return client.getRegisteredRedirectUri()!=null?
                CommonUtils.transformStringToSet(client.getRegisteredRedirectUri(),String.class):null;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return (client.getAuthorities()!=null&&client.getAuthorities().trim().length()>0)?
                AuthorityUtils.commaSeparatedStringToAuthorityList(client.getAuthorities()):null;
    }

    @Override
    public Integer getAccessTokenValiditySeconds() {
        return client.getAccessTokenValiditySeconds();
    }

    @Override
    public Integer getRefreshTokenValiditySeconds() {
        return client.getRefreshTokenValiditySeconds();
    }

    @Override
    public boolean isAutoApprove(String scope) {
       return  this.client.getIsAutoApprove()==null ? false: this
               .client.getIsAutoApprove();
    }

    @Override
    public Map<String, Object> getAdditionalInformation() {
        return null;
    }
}
