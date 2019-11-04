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
 */

package com.mountain.oauth2;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mountain.base.AbstractBaseDomain;

import lombok.Data;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

/**
 * 
 * 	功能说明：Oauth2 Client认证DO
 * 
 *	功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 *
 *	创建日期：2019-10-02 ：7:33:00
 *
 *	版权归属：蓝河团队
 *
 *	协议说明：Apache2.0（ 文件顶端 ）
 *
 */

@Data
@TableName("clients")
public class Client extends AbstractBaseDomain implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = -6556141649415714792L;
	@TableField("clientId")
    @NotNull
    private String clientId;
    @TableField("resourceIds")
    private String resourceIds;
    @TableField("isSecretRequired")
    private Boolean isSecretRequired;
    @TableField("clientSecret")
    @NotNull
    private String clientSecret;
    @TableField("isScoped")
    private Boolean isScoped;
    @TableField("scope")
    private String scope;
    @TableField("authorizedGrantTypes")
    @NotNull
    private String authorizedGrantTypes;
    @TableField("registeredRedirectUri")
    @NotNull
    private String registeredRedirectUri;
    @TableField("authorities")
    private String authorities;
    @TableField("isAutoApprove")
    private Boolean isAutoApprove;
    @TableField("accessTokenValiditySeconds")
    @NotNull
    private Integer accessTokenValiditySeconds;
    @TableField("refreshTokenValiditySeconds")
    @NotNull
    private Integer refreshTokenValiditySeconds;

}
