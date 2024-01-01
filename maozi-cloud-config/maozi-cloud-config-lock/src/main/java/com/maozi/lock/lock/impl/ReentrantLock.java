package com.maozi.lock.lock.impl;

import com.maozi.lock.lock.Lock;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
public class ReentrantLock implements Lock {

    @Resource
    private RedissonClient redissonClient;

    @Override
    public boolean lock(String key,Long waitTime,Long leaseTime) throws Exception {
        return redissonClient.getLock(key).tryLock(waitTime,leaseTime,TimeUnit.SECONDS);
    }

    @Override
    public boolean unLock(String key) throws Exception {

        RLock lock = redissonClient.getLock(key);

        return lock.isHeldByCurrentThread() ? lock.forceUnlockAsync().get() : false;

    }

}