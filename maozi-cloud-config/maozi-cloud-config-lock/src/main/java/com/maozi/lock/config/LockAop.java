package com.maozi.lock.config;

import com.maozi.common.CollectionUtil;
import com.maozi.common.context.ApplicationEnvironmentContext;
import com.maozi.lock.annotation.LockKey;
import com.maozi.lock.lock.LockType;
import com.maozi.lock.properties.LockProperties;
import jakarta.annotation.Resource;
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

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

/**
 * 分布式锁切面处理器
 * <p>
 * 拦截带有 {@link com.maozi.lock.annotation.Lock} 注解的方法，
 * 根据注解配置自动获取和释放分布式锁。支持 SpEL 表达式解析锁键、
 * 多种锁类型（可重入锁、公平锁、读写锁）和超时策略。
 * </p>
 *
 * @author maozi
 */
@Aspect
@Component
@Order(value = Ordered.HIGHEST_PRECEDENCE + 2 )
public class LockAop {

    /** SpEL 表达式解析器 */
    private final ExpressionParser parser = new SpelExpressionParser();

    /** 参数名发现器 */
    private final ParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    /** 锁配置属性 */
    @Resource
    private LockProperties properties;

    /**
     * 环绕通知，执行加锁、业务逻辑和释放锁
     *
     * @param joinPoint AOP 连接点
     * @param annotation 锁注解实例
     * @return 业务方法执行结果
     * @throws Throwable 业务方法或锁操作抛出的异常
     */
    @Around("@annotation(annotation)")
    public Object around(ProceedingJoinPoint joinPoint, com.maozi.lock.annotation.Lock annotation) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        LockType type = annotation.type();

        String businessKeyName = getKeyName(joinPoint,annotation);

        String lockName = org.apache.commons.lang3.StringUtils.isNotBlank(annotation.name()) ? annotation.name() : signature.getDeclaringTypeName()+ "." +signature.getMethod().getName();

        lockName = ApplicationEnvironmentContext.SERVICE_NAME + ":lock:" + lockName + businessKeyName;

        long waitTime = annotation.waitTime() == Long.MIN_VALUE ? properties.getWaitTime() : annotation.waitTime();

        long leaseTime = annotation.leaseTime() == Long.MIN_VALUE ? properties.getLeaseTime() : annotation.leaseTime();

        type.lock(lockName,waitTime,leaseTime,annotation.lockTimeoutStrategy());

        try {return joinPoint.proceed();} catch (Throwable e) {throw e;} finally {
            type.unlock(lockName,annotation.releaseTimeoutStrategy());
        }

    }

    /**
     * 获取完整的锁键名称
     *
     * @param joinPoint AOP 连接点
     * @param lock 锁注解实例
     * @return 拼接后的锁键名称
     * @throws Exception 反射操作异常
     */
    public String getKeyName(JoinPoint joinPoint, com.maozi.lock.annotation.Lock lock) throws Exception {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        Method method = signature.getMethod();

        method = method.getDeclaringClass().isInterface() ? signature.getMethod() : joinPoint.getTarget().getClass().getDeclaredMethod(signature.getName(),method.getParameterTypes());

        List<String> keyList = CollectionUtil.newArrayList();

        keyList.addAll( getSpelDefinitionKey(lock.keys(), method, joinPoint.getArgs()) );

        keyList.addAll( getParameterKey(method.getParameters(), joinPoint.getArgs()) );

        return StringUtils.collectionToDelimitedString(keyList,"","-","");

    }

    /**
     * 解析 SpEL 表达式定义的锁键
     *
     * @param definitionKeys SpEL 表达式数组
     * @param method 目标方法
     * @param parameterValues 方法参数值
     * @return 解析后的锁键列表
     */
    private List<String> getSpelDefinitionKey(String[] definitionKeys, Method method, Object[] parameterValues) {

        List<String> definitionKeyList = CollectionUtil.newArrayList();

        for (String definitionKey : definitionKeys) {

            if (!ObjectUtils.isEmpty(definitionKey)) {

                EvaluationContext context = new MethodBasedEvaluationContext(null, method, parameterValues, nameDiscoverer);

                Object objKey = parser.parseExpression(definitionKey).getValue(context);

                definitionKeyList.add(ObjectUtils.nullSafeToString(objKey));

            }

        }

        return definitionKeyList;

    }

    /**
     * 获取带有 {@link LockKey} 注解的参数值作为锁键
     *
     * @param parameters 方法参数数组
     * @param parameterValues 参数值数组
     * @return 锁键列表
     */
    private List<String> getParameterKey(Parameter[] parameters, Object[] parameterValues) {

        List<String> parameterKey = CollectionUtil.newArrayList();

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
