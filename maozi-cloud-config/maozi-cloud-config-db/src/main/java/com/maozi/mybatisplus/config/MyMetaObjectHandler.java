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

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.maozi.base.enums.Deleted;
import com.maozi.base.enums.Status;
import com.maozi.common.BaseCommon;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Configuration;

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
@Configuration
public class MyMetaObjectHandler extends BaseCommon implements MetaObjectHandler {

	@Override
	public void insertFill(MetaObject metaObject) {

		this.strictInsertFill(metaObject, "createTime", Long.class, System.currentTimeMillis());
		this.strictInsertFill(metaObject, "updateTime", Long.class, System.currentTimeMillis());
		this.strictInsertFill(metaObject, "status", Status.class, Status.enable);
		this.strictInsertFill(metaObject, "deleted", Deleted.class, Deleted.none);
		this.strictInsertFill(metaObject, "version", Integer.class, 0);

		String currentUserName = getCurrentUserName();
		this.strictInsertFill(metaObject, "createUsername", String.class, currentUserName);
		this.strictInsertFill(metaObject, "updateUsername", String.class, currentUserName);
		
	}

	@Override
	public void updateFill(MetaObject metaObject) {
		
		this.strictInsertFill(metaObject, "updateUsername", String.class, getCurrentUserName());
		
		this.strictUpdateFill(metaObject, "updateTime", Long.class, System.currentTimeMillis());
		
	}
}