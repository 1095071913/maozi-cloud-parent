package com.maoteng.pay.strategy;

import com.maoteng.pay.input.PayMentTransac;
import com.maoteng.pay.input.PaymentChannelEntity;

/**
 * 功能说明:	支付api
 * 功能作者:	彭晋龙
 * 创建日期:	2019.8.14
 * 版权归属:	忧伤-蓝河
 */

public interface PayStrategy {

	/**
	 * 
	 * @param pymentChannel
	 *            渠道参数
	 * @param payMentTransacDTO
	 *            支付参数   
	 * @return
	 */
	public String toPayHtml(PaymentChannelEntity paymentChannel,PayMentTransac payMentTransac);

}
