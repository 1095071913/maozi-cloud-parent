package com.maozi.lock.lock.impl;

import com.maozi.lock.lock.Lock;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
public class WriteLock implements Lock {

    @Resource
    private RedissonClient redissonClient;

    @Override
    public boolean lock(String key,Long waitTime,Long leaseTime) throws Exception {
        return redissonClient.getReadWriteLock(key).writeLock().tryLock(waitTime,leaseTime,TimeUnit.SECONDS);
    }

    @Override
    public boolean unLock(String key) throws Exception {

        RReadWriteLock lock = redissonClient.getReadWriteLock(key);

        return lock.writeLock().isHeldByCurrentThread() ? lock.writeLock().forceUnlockAsync().get() : false;

    }

}