
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
 *
 */

package com.maozi.stream.config;

import com.maozi.base.enums.EnvironmentType;
import com.maozi.common.BaseCommon;
import com.maozi.utils.context.ApplicationLinkContext;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(value = Ordered.HIGHEST_PRECEDENCE + 1 )
public class StreamEntranceLogAop extends BaseCommon {

	private final String POINT = "execution(java.util.function.Consumer com.maozi.*.*.stream..*(..))";

    @Around(POINT)
    public Consumer<Message<Object>> doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        Consumer<Message<Object>> resultData = (Consumer<Message<Object>>) proceedingJoinPoint.proceed();

        return (message) -> {

            Boolean error = false;

            Long beginTime = System.currentTimeMillis();

            Object messageData = message.getPayload();

            functionParam(messageData);

            MessageHeaders headers = message.getHeaders();

            Map<String, String> logs = new LinkedHashMap<String, String>();

            logs.put("Type", "Rocket");
            logs.put("MessageId",headers.get("ROCKET_MQ_MESSAGE_ID").toString());
            logs.put("Topic",headers.get("ROCKET_MQ_TOPIC").toString());
            logs.put("Function", proceedingJoinPoint.getSignature().getDeclaringTypeName()+":"+proceedingJoinPoint.getSignature().getName());

            if(notEnvironment(EnvironmentType.production)){
                logs.put("Param", messageData.toString());
            }

            ApplicationLinkContext.VERSIONS.set(getVersionDefault(headers.get("Version")));

            try{resultData.accept(message);}catch (Exception e){

                error = true;

                functionError(getStackTrace(e));

                StackTraceElement stackTraceElement = stackTraceElement = e.getStackTrace()[0];

                logs.put("ErrorParam", messageData.toString());
                logs.put("ErrorDesc", e.getLocalizedMessage());
                logs.put("ErrorLine", stackTraceElement.toString());

                throw e;

            }finally {

                StringBuilder sql = BaseCommon.sql.get();
                if(isNotNull(sql)) {
                    logs.put("SQL", sql.toString());
                }

                logs.put("RT",System.currentTimeMillis()-beginTime+"");

                log(error,logs);

                clearContext();

            }

        };
    
    }

}
