/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.rpc.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.rpc.Constants;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.RpcInvocation;
import org.apache.dubbo.rpc.model.ConsumerModel;
import org.apache.dubbo.rpc.model.ServiceModel;

import com.maozi.factory.BaseResultFactory;
import com.maozi.factory.result.code.CodeAttribute;

/**
 * InvokerHandler
 */
public class InvokerInvocationHandler implements InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(InvokerInvocationHandler.class);
    private final Invoker<?> invoker;
    private ServiceModel serviceModel;
    private URL url;
    private String protocolServiceKey;

    public InvokerInvocationHandler(Invoker<?> handler) {
        this.invoker = handler;
        this.url = invoker.getUrl();
        this.protocolServiceKey = this.url.getProtocolServiceKey();
        this.serviceModel = this.url.getServiceModel();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    	
    	try {
    		
    		if (method.getDeclaringClass() == Object.class) {
                return method.invoke(invoker, args);
            }
            String methodName = method.getName();
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length == 0) {
                if ("toString".equals(methodName)) {
                    return invoker.toString();
                } else if ("$destroy".equals(methodName)) {
                    invoker.destroy();
                    return null;
                } else if ("hashCode".equals(methodName)) {
                    return invoker.hashCode();
                }
            } else if (parameterTypes.length == 1 && "equals".equals(methodName)) {
                return invoker.equals(args[0]);
            }
            RpcInvocation rpcInvocation = new RpcInvocation(serviceModel, method.getName(), invoker.getInterface().getName(), protocolServiceKey, method.getParameterTypes(), args);

            if (serviceModel instanceof ConsumerModel) {
                rpcInvocation.put(Constants.CONSUMER_MODEL, serviceModel);
                rpcInvocation.put(Constants.METHOD_MODEL, ((ConsumerModel) serviceModel).getMethodModel(method));
            }
            return InvocationUtil.invoke(invoker, rpcInvocation);
    		
    	} catch (RpcException rpcError) {
			
			String errorMessage = rpcError.getLocalizedMessage();

			String[] errorMessageSplit = errorMessage.split("com.maozi.");

			if (errorMessageSplit.length > 1 && errorMessage.indexOf("GenericService") == -1) {

				String applicationName = errorMessageSplit[1].substring(0, errorMessageSplit[1].indexOf("."));

				if (!applicationName.equals("health")) {

					String arg = Arrays.toString(args);

					Map<String, String> logs = new LinkedHashMap<String, String>();

					logs.put("reqType", "dubbo");
					logs.put("rpcApp", applicationName);
					logs.put("reqFunc", method.getName());
					logs.put("errorParam", arg);
					logs.put("errorDesc", rpcError.getLocalizedMessage());
					logs.put("errorLine", rpcError.getStackTrace()[0].toString());

					BaseResultFactory.log.error(BaseResultFactory.getStackTrace(rpcError));

					BaseResultFactory.log.error(BaseResultFactory.appendLog(logs).toString());
				}
				return BaseResultFactory.error(new CodeAttribute(6,"服务错误" + "(" + applicationName + ")"),500);
			}

			throw rpcError;
		}
        
    }
}
