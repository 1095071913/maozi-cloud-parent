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
package com.maozi.base.seata.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.TableComment;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 
 * 	功能说明：用户Do
 * 
 *	功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 *
 *	创建日期：2019-10-02 : 20:03:00
 *
 *	版权归属：蓝河团队
 *
 *	协议说明：Apache2.0（ 文件顶端 ）
 *
 */


@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@TableName("undo_log")
@TableComment("用户")
public class UndoLogDo implements Serializable {
	
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;
	
	@Column(value = "branch_id",comment = "branch transaction id")
	private Long branchId;
	
	@Column(value = "xid",comment = "global transaction id")
	private String xid;
	
	@Column(value = "context",comment = "undo_log context,such as serialization")
	private String context;
	
	@Column(value = "rollback_info",type = MySqlTypeConstant.LONGBLOB,comment = "rollback info")
	private byte[] rollbackInfo;
	
	@Column(value = "log_status",comment = "0:normal status,1:defense status")
	private int logStatus;
	
	@Column(value = "log_created",comment = "create datetime")
	private Timestamp logCreated;
	
	@Column(value = "log_modified",comment = "modify datetime")
	private Timestamp logModified;
	
}
