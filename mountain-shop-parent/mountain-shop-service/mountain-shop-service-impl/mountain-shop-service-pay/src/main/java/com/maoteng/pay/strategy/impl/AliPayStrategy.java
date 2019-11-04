package com.maoteng.pay.strategy.impl;

import com.maoteng.pay.input.PayMentTransac;
import com.maoteng.pay.input.PaymentChannelEntity;
import com.maoteng.pay.strategy.PayStrategy;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AliPayStrategy implements PayStrategy {

	@Override
	public String toPayHtml(PaymentChannelEntity pymentChannel,PayMentTransac payMentTransac) {
		log.info(">>>>>支付宝参数封装开始<<<<<<<<");
		return "支付宝支付from表单提交";
	}

}