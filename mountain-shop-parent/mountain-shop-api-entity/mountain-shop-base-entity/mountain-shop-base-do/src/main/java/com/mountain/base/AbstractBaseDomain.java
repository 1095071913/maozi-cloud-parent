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

package com.mountain.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * 	功能说明：领域模型Do
 * 
 *	功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 *
 *	创建日期：2019-08-03 ：1:32:00
 *
 *	版权归属：蓝河团队
 *
 *	协议说明：Apache2.0（ 文件顶端 ）
 *
 */

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class AbstractBaseDomain implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2577503727587079751L;

	
	
	/**
	 * 该注解需要保留，用于 mybatis 回显 ID
	 */
	@TableId(value = "id", type = IdType.AUTO)
	private Long id;

	
	
	
	
	
	
	/** 创建时间 插入自动填充 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@TableField(fill = FieldFill.INSERT)
	private Date createDate;

	
	
	
	
	
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@TableField(fill = FieldFill.INSERT_UPDATE)
	private Date updateDate;
	
	
	
	
	/**
	 * 创建者
	 */
	private String createBy;

	
	
	
	
	/**
	 * 更新者
	 */
	private String updateBy;
	
	
	
	

	/**
	 * 备注信息
	 */
	private String remarks;
	
	
	
	
	
	/**
	 * 逻辑删除
	 */
	@TableLogic
	private Integer deleted;
	
	
	
	/**
	 * 乐观锁
	 */
	@Version
	private Integer version;
	
	
	
	/**
	 * 类型
	 */
	@TableField
	private Integer type;
	
	
	
	/**
	 * 起始页数
	 */
	@TableField(exist = false)
	private Integer pageBegin;
	
	
	/**
	 * 分页数据数量
	 */
	@TableField(exist = false)
	private Integer pageSize;
	
	

	/**
	 * 扩展 String 1
	 */
	private String extendS1;
	
	
	
	

	/**
	 * 扩展 String 2
	 */
	private String extendS2;
	
	
	
	
	

	/**
	 * 扩展 String 3
	 */
	private String extendS3;
	
	
	
	

	/**
	 * 扩展 String 4
	 */
	private String extendS4;
	
	
	
	
	

	/**
	 * 扩展 String 5
	 */
	private String extendS5;
	
	
	
	
	

	/**
	 * 扩展 String 6
	 */
	private String extendS6;
	
	
	
	
	

	/**
	 * 扩展 String 7
	 */
	private String extendS7;
	
	
	
	

	/**
	 * 扩展 String 8
	 */
	private String extendS8;

	
	
	
	
	/**
	 * 扩展 Integer 1
	 */
	private BigDecimal extendI1;
	
	
	
	
	

	/**
	 * 扩展 Integer 2
	 */
	private BigDecimal extendI2;
	
	
	
	
	
	

	/**
	 * 扩展 Integer 3
	 */
	private BigDecimal extendI3;

	
	
	
	
	
	
	
	/**
	 * 扩展 Integer 4
	 */
	private BigDecimal extendI4;

	
	
	
	
	
	
	
	/**
	 * 扩展 Float 1
	 */
	private BigDecimal extendF1;

	
	
	
	
	
	
	
	/**
	 * 扩展 Float 2
	 */
	private BigDecimal extendF2;
	
	
	
	
	
	
	
	

	/**
	 * 扩展 Float 3
	 */
	private BigDecimal extendF3;
	
	
	
	
	
	
	

	/**
	 * 扩展 Float 4
	 */
	private BigDecimal extendF4;
	
	
	
	
	
	
	

	/**
	 * 扩展 Date 1
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date extendD1;
	
	
	
	
	
	
	

	/**
	 * 扩展 Date 2
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date extendD2;

	
	
	
	
	
	
	
	/**
	 * 扩展 Date 3
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date extendD3;

	
	
	
	
	
	
	
	
	/**
	 * 扩展 Date 4
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date extendD4;
}