package com.maozi.lock.annotation;

import com.maozi.lock.error.strategy.impl.LockTimeoutStrategy;
import com.maozi.lock.error.strategy.impl.UnLockTimeoutStrategy;
import com.maozi.lock.lock.LockType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


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
    LockType type() default LockType.Reentrant;

    /**
     * 尝试加锁，最多等待时间
     * @return waitTime
     */
    long waitTime() default Long.MIN_VALUE;

    /**
     *上锁以后xxx秒自动解锁
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