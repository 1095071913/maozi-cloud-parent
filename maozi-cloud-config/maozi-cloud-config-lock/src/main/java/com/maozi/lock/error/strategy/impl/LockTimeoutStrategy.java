package com.maozi.lock.error.strategy.impl;

import com.maozi.common.result.error.exception.BusinessResultException;
import com.maozi.lock.error.strategy.LockTimeoutHandler;
import com.maozi.lock.lock.Lock;
import java.util.concurrent.TimeUnit;

public enum LockTimeoutStrategy implements LockTimeoutHandler {

    FAIL_FAST() {
        @Override
        public void handle(String key,Long waitTime,Long leaseTime,Lock lock) throws Exception {
            throw new BusinessResultException(429,"限流中",429);
        }
    },

    KEEP_ACQUIRE() {

        private final Long DEFAULT_INTERVAL = 100L;

        private final Long DEFAULT_MAX_INTERVAL = 3L * 60L * 1000L;

        @Override
        public void handle(String key,Long waitTime,Long leaseTime,Lock lock) throws Exception {

            Long interval = DEFAULT_INTERVAL;

            while (!lock.lock(key,waitTime,leaseTime)) {

                if (interval > DEFAULT_MAX_INTERVAL) {
                    throw new BusinessResultException(429,"限流中",429);
                }

                TimeUnit.MILLISECONDS.sleep(interval);

                interval <<= 1;

            }

        }

    }

}