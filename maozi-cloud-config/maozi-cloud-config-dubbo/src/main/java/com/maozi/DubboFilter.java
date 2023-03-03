package com.maozi;
//
//import java.util.Map;
//
//import org.apache.dubbo.common.constants.CommonConstants;
//import org.apache.dubbo.common.extension.Activate;
//import org.apache.dubbo.rpc.Filter;
//import org.apache.dubbo.rpc.Invocation;
//import org.apache.dubbo.rpc.Invoker;
//import org.apache.dubbo.rpc.Result;
//import org.apache.dubbo.rpc.RpcException;
//import org.springframework.stereotype.Component;
//
//import com.maozi.factory.BaseResultFactory;
//import com.maozi.factory.result.AbstractBaseResult;
//import com.maozi.factory.result.error.ErrorResult;
//import com.maozi.factory.result.success.SuccessResult;
//import com.maozi.tool.MapperUtils;
//
//@Component
//@Activate(group = { CommonConstants.CONSUMER },order=-10000) 
//public class DubboFilter extends BaseResultFactory implements Filter {
//
//	@Override
//	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
//		
//		Long returnDate=System.currentTimeMillis();
//		
//		Result invoke = invoker.invoke(invocation);
//		
//		try {  
//			
//			returnDate = (System.currentTimeMillis() - returnDate);  
//			
//			Object value = invoke.getValue(); 
//			
//			AbstractBaseResult rpcResult=null; 
//			
//			
//			if(value instanceof Map) { 
//				
//				Map valueMap=(Map)value; 
//				
//				Object code = valueMap.get("code");
//				
//				if(!isNull(code)) {
//					if(code.toString().equals("200")) {
//						rpcResult=MapperUtils.map2pojo(valueMap, SuccessResult.class);  
//					}else { 
//						rpcResult=MapperUtils.map2pojo(valueMap, ErrorResult.class); 
//					}  
//					invoke.setValue(rpcResult); 
//				}
//				
//			}
//			
//			
//			else if(value instanceof AbstractBaseResult) {
//				rpcResult=(AbstractBaseResult)value;  
//			}
//			
//			
//			else {
//				return invoke;
//			}
//			
//			
//			
//			
//			 
//		}catch (Exception e) {log.error(getStackTrace(e));};
//		
//		 
//		return invoke;
//		
//	}
//	
//
//}
