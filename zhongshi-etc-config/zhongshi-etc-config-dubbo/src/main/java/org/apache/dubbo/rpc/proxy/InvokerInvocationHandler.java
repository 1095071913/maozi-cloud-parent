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
import org.apache.dubbo.rpc.Constants;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.RpcInvocation;
import org.apache.dubbo.rpc.model.ApplicationModel;
import org.apache.dubbo.rpc.model.ConsumerModel;
import com.zhongshi.factory.BaseResultFactory;
import com.zhongshi.factory.result.code.CodeAttribute;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InvokerInvocationHandler implements InvocationHandler {

	private final Invoker<?> invoker;
	private ConsumerModel consumerModel;

	public InvokerInvocationHandler(Invoker<?> handler) {
		this.invoker = handler;
		String serviceKey = invoker.getUrl().getServiceKey();
		if (serviceKey != null) {
			this.consumerModel = ApplicationModel.getConsumerModel(serviceKey);
		}
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
			RpcInvocation rpcInvocation = new RpcInvocation(method, invoker.getInterface().getName(), args);
			String serviceKey = invoker.getUrl().getServiceKey();
			rpcInvocation.setTargetServiceUniqueName(serviceKey);

			if (consumerModel != null) {
				rpcInvocation.put(Constants.CONSUMER_MODEL, consumerModel);
				rpcInvocation.put(Constants.METHOD_MODEL, consumerModel.getMethodModel(method));
			}
			return invoker.invoke(rpcInvocation).recreate();
		} catch (RpcException rpcError) {
			
			String errorMessage = rpcError.getLocalizedMessage();

			String[] errorMessageSplit = errorMessage.split("com.zhongshi.");

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

					log.error(BaseResultFactory.getStackTrace(rpcError));

					log.error(BaseResultFactory.appendLog(logs).toString());
				}
				return BaseResultFactory.error(new CodeAttribute(6,"服务错误" + "(" + applicationName + ")"),500);
			}

			throw rpcError;
		}

	}
}
