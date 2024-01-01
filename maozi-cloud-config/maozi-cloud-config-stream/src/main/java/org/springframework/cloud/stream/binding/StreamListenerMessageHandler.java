/*
` * Copyright 2017-2019 the original author or authors.
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

package org.springframework.cloud.stream.binding;

import com.maozi.common.BaseCommon;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.Tag;
import org.apache.skywalking.apm.toolkit.trace.Tags;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.integration.handler.AbstractReplyProducingMessageHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.handler.invocation.InvocableHandlerMethod;

@Slf4j
public class StreamListenerMessageHandler extends AbstractReplyProducingMessageHandler {

	private final InvocableHandlerMethod invocableHandlerMethod;

	private final boolean copyHeaders;

	StreamListenerMessageHandler(InvocableHandlerMethod invocableHandlerMethod, boolean copyHeaders,String[] notPropagatedHeaders) {
		super();
		this.invocableHandlerMethod = invocableHandlerMethod;
		this.copyHeaders = copyHeaders;
		this.setNotPropagatedHeaders(notPropagatedHeaders);
	}

	@Override
	protected boolean shouldCopyRequestHeaders() {
		return this.copyHeaders;
	}

	public boolean isVoid() {
		return this.invocableHandlerMethod.isVoid();
	}

	@Override
	protected Object handleRequestMessage(Message<?> requestMessage) {
		
		byte [] messageData = (byte[]) requestMessage.getPayload();
		
		functionParam(new String(messageData)); 
		
		Boolean isError=false; 

		MessageHeaders headerMessage = requestMessage.getHeaders();
		
		Map<String, String> logs = new LinkedHashMap<String, String>();
		
		Long beginTime = System.currentTimeMillis();
		
		logs.put("Type", "Rocket");
    	logs.put("IP", headerMessage.get("rocketmq_BORN_HOST").toString());
    	logs.put("MessageId",headerMessage.get("rocketmq_MESSAGE_ID").toString());
    	logs.put("MessageTopic",headerMessage.get("rocketmq_TOPIC").toString());
    	 
    	
		try { 
			return this.invocableHandlerMethod.invoke(requestMessage);
		} catch (Exception e) {
			
			
			functionError(BaseCommon.getStackTrace(e));
			
			StackTraceElement stackTraceElement = stackTraceElement = e.getStackTrace()[0];
            
            logs.put("ErrorDesc", e.getLocalizedMessage());
            logs.put("ErrorLine", stackTraceElement.toString());
            
            isError=true;
           
			if (e instanceof MessagingException) { 
				throw (MessagingException) e; 
			} else {
				throw new MessagingException(requestMessage,"Exception thrown while invoking " + this.invocableHandlerMethod.getShortLogMessage(), e);
			}
		}finally {
			
			StringBuilder sql = BaseCommon.sql.get();
    		if(BaseCommon.isNotNull(sql)) { logs.put("SQL", sql.toString());}
    		
			logs.put("RT",System.currentTimeMillis()-beginTime+"");
			
			if(isError) {
				log.error(BaseCommon.appendLog(logs).toString());
			}else {
				log.info(BaseCommon.appendLog(logs).toString());
			}  
			
			BaseCommon.clearContext();
		}
	}

	@Trace
    @Tags({@Tag(key = "错误值", value = "arg[0]")})
    public void functionError(Object errorMessage) {}
	
	@Trace(operationName = "入参值")
    @Tags({@Tag(key = "入参值", value = "arg[0]")})
    public void functionParam(Object param) { }

}
