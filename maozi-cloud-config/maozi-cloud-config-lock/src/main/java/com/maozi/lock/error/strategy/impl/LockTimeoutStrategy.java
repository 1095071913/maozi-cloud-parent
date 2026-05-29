package com.maozi.lock.error.strategy.impl;

import com.maozi.common.result.error.code.ErrorCode;
import com.maozi.common.result.error.code.SystemErrorCode;
import com.maozi.common.result.error.exception.BusinessResultException;
import com.maozi.lock.error.strategy.LockTimeoutHandler;
import com.maozi.lock.lock.Lock;

import java.util.concurrent.TimeUnit;

public enum LockTimeoutStrategy implements LockTimeoutHandler {

    FAIL_FAST() {

        @Override
        public void handle(String key,Long waitTime,Long leaseTime,Lock lock) {
            ErrorCode errorCode = SystemErrorCode.CURRENT_LIMITING_ERROR;
            throw new BusinessResultException(errorCode).setHttpCode(errorCode.getCode());
        }

    },

    KEEP_ACQUIRE() {

        private static final Long DEFAULT_INTERVAL = 100L;

        private static final Long DEFAULT_MAX_INTERVAL = 3L * 60L * 1000L;

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