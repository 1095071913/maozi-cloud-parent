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
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class MetaObjectHandler implements com.baomidou.mybatisplus.core.handlers.MetaObjectHandler {

	private final static String STATUS = "status";

	private final static String DELETED = "deleted";

	private final static String CREATE_TIME = "createTime";

	@Override
	public void insertFill(MetaObject metaObject) {
		this.strictInsertFill(metaObject, STATUS, Status.class, Status.ENABLE);
		this.strictInsertFill(metaObject, DELETED, Deleted.class, Deleted.NONE);
		this.strictInsertFill(metaObject, CREATE_TIME, LocalDateTime.class, LocalDateTime.now());
	}

	@Override
	public void updateFill(MetaObject metaObject) {}
}