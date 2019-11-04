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
package com.maoteng.pay.input;

import java.util.Date;

import com.mountain.base.AbstractBaseDomain;

import lombok.Data;

/**
 * 
 * 	功能说明：聚合支付Do
 * 
 *	功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 *
 *	创建日期：2019-08-5 ：7:33:00
 *
 *	版权归属：蓝河团队
 *
 *	协议说明：Apache2.0（ 文件顶端 ）
 *
 */

@Data
public class PaymentChannelEntity extends AbstractBaseDomain{
//	/** ID */
//	private Integer id;
	/** 渠道名称 */
	private String channelName;
	/** 渠道ID */
	private String channelId;
	/** 商户id */ 
	private String merchantId;
	/** 同步回调URL */
	private String syncUrl;
	/** 异步回调URL */
	private String asynUrl;
	/** 公钥 */
	private String publicKey;
	/** 私钥 */
	private String privateKey;
	/** 渠道状态;0开启1关闭 */
	private Integer channelState;
	/** 乐观锁 */
	private Integer revision;
	/** 创建人 */
	private String createdBy;
	/** 创建时间 */
	private Date createdTime;
	/** 更新人 */
	private String updatedBy;
	/** 更新时间 */
	private Date updatedTime;

	/**
	 * 接口实现地址
	 */
	private String classAddres;
}