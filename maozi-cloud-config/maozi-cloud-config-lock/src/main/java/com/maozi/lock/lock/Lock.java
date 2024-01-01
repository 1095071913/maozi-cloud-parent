package com.maozi.lock.lock;

public interface Lock {

    boolean lock(String key,Long waitTime,Long leaseTime) throws Exception;

    boolean unLock(String key) throws Exception;

}