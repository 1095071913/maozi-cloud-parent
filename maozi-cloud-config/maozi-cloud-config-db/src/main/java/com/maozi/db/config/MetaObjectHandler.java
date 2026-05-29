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

package com.maozi.db.config;

import com.maozi.base.enums.Deleted;
import com.maozi.base.enums.Status;
import com.maozi.common.context.ApplicationLinkContext;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetaObjectHandler implements com.baomidou.mybatisplus.core.handlers.MetaObjectHandler {

	@Override
	public void insertFill(MetaObject metaObject) {

		long currentTime = System.currentTimeMillis();

		this.strictInsertFill(metaObject, "createTime", Long.class, currentTime);
		this.strictInsertFill(metaObject, "updateTime", Long.class, currentTime);
		this.strictInsertFill(metaObject, "status", Status.class, Status.ENABLE);
		this.strictInsertFill(metaObject, "deleted", Deleted.class, Deleted.NONE);
		this.strictInsertFill(metaObject, "version", Integer.class, 0);

		String currentUserName = ApplicationLinkContext.USERNAMES.get();
		this.strictInsertFill(metaObject, "createUsername", String.class, currentUserName);
		this.strictInsertFill(metaObject, "updateUsername", String.class, currentUserName);
		
	}

	@Override
	public void updateFill(MetaObject metaObject) {

		this.strictUpdateFill(metaObject, "updateTime", Long.class, System.currentTimeMillis());

		String currentUserName = ApplicationLinkContext.USERNAMES.get();
		this.strictInsertFill(metaObject, "updateUsername", String.class, currentUserName);
		
	}
}