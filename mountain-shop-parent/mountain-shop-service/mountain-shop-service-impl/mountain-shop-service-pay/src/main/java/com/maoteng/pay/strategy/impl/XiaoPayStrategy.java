package com.maoteng.pay.strategy.impl;

import com.maoteng.pay.input.PayMentTransac;
import com.maoteng.pay.input.PaymentChannelEntity;
import com.maoteng.pay.strategy.PayStrategy;

public class XiaoPayStrategy implements PayStrategy {

	@Override
	public String toPayHtml(PaymentChannelEntity pymentChannel,PayMentTransac payMentTransac) {

		return "小米支付from表单提交";
	}
	//com.mayikt.pay.strategy.impl.XiaoPayStrategy

}
