package com.maozi.lock.lock.impl;

import com.maozi.lock.lock.Lock;
import jakarta.annotation.Resource;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 读锁实现
 * <p>
 * 基于 Redisson 的读写锁中的读锁（Read Lock），允许多个线程同时持有读锁，
 * 但在有读锁持有时，写锁将被阻塞。适用于读多写少的并发场景。
 * </p>
 *
 * @author maozi
 */
@Component
public class ReadLock implements Lock {

    /** Redisson 客户端 */
    @Resource
    private RedissonClient redissonClient;

    /**
     * 尝试获取读锁
     *
     * @param key 锁的键名
     * @param waitTime 等待获取锁的最大时间（秒）
     * @param leaseTime 锁的持有时间（秒），超时后自动释放
     * @return 是否成功获取锁
     * @throws Exception 锁操作异常
     */
    @Override
    public boolean lock(String key,Long waitTime,Long leaseTime) throws Exception {
        return redissonClient.getReadWriteLock(key).readLock().tryLock(waitTime,leaseTime,TimeUnit.SECONDS);
    }

    /**
     * 释放读锁
     * <p>
     * 仅当读锁由当前线程持有时才执行释放操作。
     * </p>
     *
     * @param key 锁的键名
     * @return 是否成功释放锁
     * @throws Exception 解锁操作异常
     */
    @Override
    public boolean unLock(String key) throws Exception {

        RReadWriteLock lock = redissonClient.getReadWriteLock(key);

        return lock.readLock().isHeldByCurrentThread() ? lock.readLock().forceUnlockAsync().get() : false;

    }

}
