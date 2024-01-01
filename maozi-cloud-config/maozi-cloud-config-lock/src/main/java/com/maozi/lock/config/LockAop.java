package com.maozi.lock.config;

import com.google.common.collect.Lists;
import com.maozi.common.BaseCommon;
import com.maozi.lock.annotation.LockKey;
import com.maozi.lock.lock.LockType;
import com.maozi.lock.properties.LockProperties;
import com.maozi.utils.context.ApplicationEnvironmentContext;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import javax.annotation.Resource;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.Ordered;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Aspect
@Component
@Order(value = Ordered.HIGHEST_PRECEDENCE + 2 )
public class LockAop {

    private ExpressionParser parser = new SpelExpressionParser();

    private ParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    @Resource
    private LockProperties properties;

    @Around("@annotation(annotation)")
    public Object around(ProceedingJoinPoint joinPoint, com.maozi.lock.annotation.Lock annotation) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        LockType type = annotation.type();

        String businessKeyName = getKeyName(joinPoint,annotation);

        String lockName = BaseCommon.isNotEmpty(annotation.name()) ? annotation.name() : signature.getDeclaringTypeName()+ "." +signature.getMethod().getName();

        lockName = ApplicationEnvironmentContext.applicationName + ":lock:" + lockName + businessKeyName;

        long waitTime = annotation.waitTime() == Long.MIN_VALUE ? properties.getWaitTime() : annotation.waitTime();

        long leaseTime = annotation.leaseTime() == Long.MIN_VALUE ? properties.getLeaseTime() : annotation.leaseTime();

        type.lock(lockName,waitTime,leaseTime,annotation.lockTimeoutStrategy());

        try {return joinPoint.proceed();} catch (Throwable e) {throw e;} finally {
            type.unlock(lockName,annotation.releaseTimeoutStrategy());
        }

    }

    public String getKeyName(JoinPoint joinPoint, com.maozi.lock.annotation.Lock lock) throws Exception {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        Method method = signature.getMethod();

        method = method.getDeclaringClass().isInterface() ? signature.getMethod() : joinPoint.getTarget().getClass().getDeclaredMethod(signature.getName(),method.getParameterTypes());

        List<String> keyList = Lists.newArrayList();

        keyList.addAll( getSpelDefinitionKey(lock.keys(), method, joinPoint.getArgs()) );

        keyList.addAll( getParameterKey(method.getParameters(), joinPoint.getArgs()) );

        return StringUtils.collectionToDelimitedString(keyList,"","-","");

    }

    private List<String> getSpelDefinitionKey(String[] definitionKeys, Method method, Object[] parameterValues) {

        List<String> definitionKeyList = Lists.newArrayList();

        for (String definitionKey : definitionKeys) {

            if (!ObjectUtils.isEmpty(definitionKey)) {

                EvaluationContext context = new MethodBasedEvaluationContext(null, method, parameterValues, nameDiscoverer);

                Object objKey = parser.parseExpression(definitionKey).getValue(context);

                definitionKeyList.add(ObjectUtils.nullSafeToString(objKey));

            }

        }

        return definitionKeyList;

    }

    private List<String> getParameterKey(Parameter[] parameters, Object[] parameterValues) {

        List<String> parameterKey = Lists.newArrayList();

        for (int i = 0; i < parameters.length; i++) {

            if (parameters[i].getAnnotation(LockKey.class) != null) {

                LockKey keyAnnotation = parameters[i].getAnnotation(LockKey.class);

                if (keyAnnotation.value().isEmpty()) {

                    Object parameterValue = parameterValues[i];

                    parameterKey.add(ObjectUtils.nullSafeToString(parameterValue));

                } else {

                    StandardEvaluationContext context = new StandardEvaluationContext(parameterValues[i]);

                    Object key = parser.parseExpression(keyAnnotation.value()).getValue(context);

                    parameterKey.add(ObjectUtils.nullSafeToString(key));

                }

            }

        }

        return parameterKey;

    }

}