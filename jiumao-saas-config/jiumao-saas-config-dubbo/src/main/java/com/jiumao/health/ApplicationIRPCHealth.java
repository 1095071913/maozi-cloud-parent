package com.jiumao.health;
//package com.zhongshi.health;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.actuate.health.Health;
//import org.springframework.boot.actuate.health.Health.Builder;
//import org.springframework.boot.actuate.health.HealthIndicator;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//import com.zhongshi.dubbo.generic.GenericDubbo;
//import com.zhongshi.factory.BaseResultFactory;
//import com.zhongshi.factory.result.AbstractBaseResult;
//
//@Component("Application-RPC")
//public class ApplicationIRPCHealth implements HealthIndicator {
//
//	@Autowired
//	private GenericDubbo genericDubbo;
//
//	@Value("${application-rpc-service-name:#{null}}")
//	private List<String> rpcServerNames;
//
//	@Override
//	public Health health() {
//
//		if (StringUtils.isEmpty(rpcServerNames)) {
//			return Health.up().withDetail("RPC-Subscribed-Number", 0).up().build();
//		}
//
//		List<String> errorRpcServerNames = new ArrayList<String>();
// 
//		for (String rpcClientName : rpcServerNames) {
//			String rpcClientUrl = BaseResultFactory.rpcDiscoveryServer(rpcClientName);
//			if(!BaseResultFactory.isNull(rpcClientUrl)) {
//				AbstractBaseResult<String> rpcResult = genericDubbo.invoke("applicationHealth",rpcClientUrl, "com.zhongshi.health.rpc.ApplicationRpcHealth",new String[] {},new Object[] {});
//				if (!rpcResult.ifStatus()) {
//					errorRpcServerNames.add(rpcClientName);
//				} 
//			} else {
//				errorRpcServerNames.add(rpcClientName); 
//			}
//		}
//
//		Builder health = Health.up().withDetail("RPC-Server-Detailed", "订阅数：" + rpcServerNames.size())
//				.withDetail(" ", "未连接数：" + errorRpcServerNames.size())
//				.withDetail("  ", "总订阅服务：" + Arrays.asList(rpcServerNames))
//				.withDetail("   ", "未订阅服务：" + Arrays.asList(errorRpcServerNames));
//		
//
//		return errorRpcServerNames.size() > 0 ? health.down().build() : health.up().build();
//	}
//
//}
