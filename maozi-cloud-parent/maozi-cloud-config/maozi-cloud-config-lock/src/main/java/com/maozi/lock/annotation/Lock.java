package com.maozi.lock.annotation;

import com.maozi.lock.error.strategy.impl.LockTimeoutStrategy;
import com.maozi.lock.error.strategy.impl.UnLockTimeoutStrategy;
import com.maozi.lock.lock.LockType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 分布式锁注解
 * <p>
 * 标注在方法上，声明该方法需要获取分布式锁后才能执行。
 * 支持多种锁类型（可重入锁、公平锁、读写锁）、自定义超时时间和超时处理策略。
 * 可通过 SpEL 表达式或 {@link LockKey} 注解指定锁的业务键。
 * </p>
 *
 * <p>使用示例：
 * <pre>
 * &#64;Lock(name = "orderLock", keys = {"#orderId"}, type = LockType.REENTRANT)
 * public void processOrder(String orderId) { ... }
 * </pre>
 * </p>
 *
 * @author maozi
 * @see LockKey
 * @see com.maozi.lock.lock.LockType
 */
@Target(value = {ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Lock {

    /**
     * 锁的名称
     * @return name
     */
    String name() default "";

    /**
     * 锁类型，默认可重入锁
     * @return lockType
     */
    LockType type() default LockType.REENTRANT;

    /**
     * 尝试加锁，最多等待时间（单位：秒），默认未指定（Long.MIN_VALUE，回退到全局配置）
     * @return waitTime
     */
    long waitTime() default Long.MIN_VALUE;

    /**
     * 加锁成功后自动释放锁的时长（单位：秒），默认未指定（Long.MIN_VALUE，回退到全局配置）
     * @return leaseTime
     */
    long leaseTime() default Long.MIN_VALUE;

    /**
     * 自定义业务key
     * @return keys
     */
     String [] keys() default {};

     /**
     * 加锁超时的处理策略
     * @return lockTimeoutStrategy
     */
     LockTimeoutStrategy lockTimeoutStrategy() default LockTimeoutStrategy.KEEP_ACQUIRE;

     /**
     * 释放锁时已超时的处理策略
     * @return releaseTimeoutStrategy
     */
     UnLockTimeoutStrategy releaseTimeoutStrategy() default UnLockTimeoutStrategy.NO_OPERATION;

}