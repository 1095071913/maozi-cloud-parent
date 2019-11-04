package com.maoteng.init;


import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.unionpay.acp.sdk.SDKConfig;

/**
 * 功能说明:	银联支付初始化
 * 功能作者:	彭晋龙
 * 创建日期:	2019.8.14
 * 版权归属:	忧伤-蓝河
 */

@Component
public class InitUnionPayProject implements ApplicationRunner {

	// springboot 项目启动的时候 执行该方法
	@Override
	public void run(ApplicationArguments args) throws Exception {
		SDKConfig.getConfig().loadPropertiesFromSrc();
	}
}
