package com.maozi.lock.lock;

import com.maozi.base.BaseEnum;
import com.maozi.lock.error.strategy.impl.LockTimeoutStrategy;
import com.maozi.lock.error.strategy.impl.UnLockTimeoutStrategy;
import com.maozi.lock.lock.impl.FairLock;
import com.maozi.lock.lock.impl.ReadLock;
import com.maozi.lock.lock.impl.ReentrantLock;
import com.maozi.lock.lock.impl.WriteLock;
import com.maozi.utils.SpringUtil;
import com.maozi.utils.context.ApplicationEnvironmentContext;
import lombok.Getter;

import java.util.function.Supplier;

public enum LockType implements BaseEnum {

    REENTRANT(0,"可重入锁",ReentrantLock.class),

    FAIR(1,"公平锁", FairLock.class),

    READ(2,"读锁", ReadLock.class),

    WRITE(3,"写锁", WriteLock.class),

    ;

    LockType(Integer value,String desc, Class< ? extends Lock> lockClass) {

        this.value = value;

        this.desc = desc;

        this.lockClass = lockClass;

    }

    @Getter
    private final Integer value;

    @Getter
    private final String desc;

    @Override
    public String toString() {
        return value+"."+desc;
    }

    private final Class< ? extends Lock > lockClass;

    public Lock getLock(){
        return SpringUtil.getBean(lockClass);
    }

    public <T> T lock(String key, Supplier<T> supplier) throws Exception {

        lock(key,60L,60L,LockTimeoutStrategy.KEEP_ACQUIRE);

        T t = supplier.get();

        unlock(key);

        return t;

    }

    public <T> T lock(String key, LockTimeoutStrategy strategy, Supplier<T> supplier) throws Exception {

        lock(key,60L,60L,strategy);

        T t = supplier.get();

        unlock(key);

        return t;

    }

    public <T> T lock(String key, LockTimeoutStrategy lockTimeoutStrategy,UnLockTimeoutStrategy unLockTimeoutStrategy, Supplier<T> supplier) throws Exception {

        lock(key,60L,60L,lockTimeoutStrategy);

        T t = supplier.get();

        unlock(key,unLockTimeoutStrategy);

        return t;

    }

    public <T> T lock(String key, Long waitTime,Long leaseTime, Supplier<T> supplier) throws Exception {

        lock(key,waitTime,leaseTime,LockTimeoutStrategy.KEEP_ACQUIRE);

        T t = supplier.get();

        unlock(key,UnLockTimeoutStrategy.NO_OPERATION);

        return t;

    }

    public void lock(String key) throws Exception {
        lock(key,60L,60L,LockTimeoutStrategy.KEEP_ACQUIRE);
    }

    public void lock(String key, LockTimeoutStrategy strategy) throws Exception {
        lock(key,60L,60L,strategy);
    }

    public void lock(String key,Long waitTime,Long leaseTime, LockTimeoutStrategy strategy) throws Exception {

        Lock lock = getLock();

        key = ApplicationEnvironmentContext.SERVICE_NAME +":lock:" + key;

        if(!lock.lock(key,waitTime,leaseTime)) {
            strategy.handle(key,waitTime,leaseTime, lock);
        }

    }

    public void unlock(String key) throws Exception {
        unlock(key,UnLockTimeoutStrategy.NO_OPERATION);
    }

    public void unlock(String key, UnLockTimeoutStrategy strategy) throws Exception {

        Lock lock = getLock();

        key = ApplicationEnvironmentContext.SERVICE_NAME +":lock:" + key;

        if (!lock.unLock(key)) {
            strategy.handle();
        }

    }

}