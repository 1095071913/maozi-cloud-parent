package com.maozi.lock.error.strategy.impl;

import com.maozi.common.result.error.code.ErrorCode;
import com.maozi.common.result.error.code.SystemErrorCode;
import com.maozi.common.result.error.exception.BusinessResultException;
import com.maozi.lock.error.strategy.LockTimeoutHandler;
import com.maozi.lock.lock.Lock;

import java.util.concurrent.TimeUnit;

/**
 * 加锁超时策略枚举
 * <p>
 * 定义两种加锁超时处理策略：
 * <ul>
 *   <li>FAIL_FAST - 快速失败，直接抛出限流异常</li>
 *   <li>KEEP_ACQUIRE - 持续重试获取锁，超过最大重试时间后失败</li>
 * </ul>
 * </p>
 *
 * @author maozi
 */
public enum LockTimeoutStrategy implements LockTimeoutHandler {

    /** 快速失败策略 */
    FAIL_FAST() {

        /**
         * 直接抛出限流异常
         */
        @Override
        public void handle(String key,Long waitTime,Long leaseTime,Lock lock) {
            ErrorCode errorCode = SystemErrorCode.CURRENT_LIMITING_ERROR;
            throw new BusinessResultException(errorCode).setHttpCode(errorCode.getCode());
        }

    },

    /** 持续获取锁策略 */
    KEEP_ACQUIRE() {

        /** 默认重试间隔 100ms */
        private static final Long DEFAULT_INTERVAL = 100L;

        /** 默认最大重试时间 3 分钟 */
        private static final Long DEFAULT_MAX_INTERVAL = 3L * 60L * 1000L;

        /**
         * 以指数退避方式持续重试获取锁，超过最大重试时间后抛出限流异常
         */
        @Override
        public void handle(String key,Long waitTime,Long leaseTime,Lock lock) throws Exception {

            Long interval = DEFAULT_INTERVAL;

            while (!lock.lock(key,waitTime,leaseTime)) {

                if (interval > DEFAULT_MAX_INTERVAL) {
                    ErrorCode errorCode = SystemErrorCode.CURRENT_LIMITING_ERROR;
                    throw new BusinessResultException(errorCode).setHttpCode(errorCode.getCode());
                }

                TimeUnit.MILLISECONDS.sleep(interval);

                interval <<= 1;

            }

        }

    }

}
