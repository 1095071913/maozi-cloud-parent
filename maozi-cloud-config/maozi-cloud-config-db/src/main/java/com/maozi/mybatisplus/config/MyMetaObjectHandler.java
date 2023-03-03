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

package com.maozi.mybatisplus.config;

import java.util.Map;

import org.apache.ibatis.reflection.MetaObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.maozi.factory.BaseResultFactory;
import com.maozi.sso.OauthUserDetails;

/**
 * 
 * 功能说明：Mybatis修改拦截设置
 * 
 * 功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 *
 * 创建日期：2019-09-03 ：3:44:00
 *
 * 版权归属：蓝河团队
 *
 * 协议说明：Apache2.0（ 文件顶端 ）
 *
 */

public class MyMetaObjectHandler extends BaseResultFactory implements MetaObjectHandler {

	@Override
	public void insertFill(MetaObject metaObject) {

		this.strictInsertFill(metaObject, "createTime", Long.class, System.currentTimeMillis());
		this.strictInsertFill(metaObject, "updateTime", Long.class, System.currentTimeMillis());
		this.strictInsertFill(metaObject, "deleted", Integer.class, 0);
		this.strictInsertFill(metaObject, "version", Integer.class, 0);

		OauthUserDetails oauthUserDetails = getOauthUserDetails();
		if (isNotNull(oauthUserDetails)) {

			Map<String,Map<String,Object>> userInfos = oauthUserDetails.getUserInfos();
			
			Long userId = Long.parseLong(userInfos.get("com.maozi.sso.info.SystemUser").get("id").toString());
			
			this.strictInsertFill(metaObject, "createBy", Long.class, userId);
			
		}
		
	}

	@Override
	public void updateFill(MetaObject metaObject) {
		this.strictUpdateFill(metaObject, "updateTime", Long.class, System.currentTimeMillis());
	}
}