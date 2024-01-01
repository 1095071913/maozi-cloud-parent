package com.maozi.lock.error.strategy;

import com.maozi.lock.lock.Lock;

public interface LockTimeoutHandler {

    void handle(String key,Long waitTime,Long leaseTime,Lock lock) throws Exception ;

}