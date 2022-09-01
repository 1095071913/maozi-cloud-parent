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

package com.jiumao.base;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 功能说明：领域模型Do
 * <p>
 * 功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 * <p>
 * 创建日期：2019-08-03 ：1:32:00
 * <p>
 * 版权归属：蓝河团队
 * <p>
 * 协议说明：Apache2.0（ 文件顶端 ）	
 *
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractBaseDomain implements Serializable {

	@TableId(value = "id", type = IdType.ASSIGN_ID)
	private Long id;

	@TableField(value = "create_by" , fill = FieldFill.INSERT)
	@Column(comment = "创建人")
	private Long createBy;

	@TableLogic
	@Column(defaultValue = "0",comment = "逻辑删除键")
	@TableField(value = "deleted" , fill = FieldFill.INSERT)
	private Integer deleted;

	@Version
	@Column(defaultValue = "0",comment = "版本号")
	@TableField(value= "version" , fill = FieldFill.INSERT)
	private Integer version;

	@TableField(value= "create_time" , fill = FieldFill.INSERT)
	@Column(comment = "创建时间")
	private Long createTime;
	
	@TableField(value= "update_time" , fill = FieldFill.INSERT_UPDATE)
	@Column(comment = "更新时间")
	private Long updateTime;

}