package com.mountain;

import java.util.concurrent.Future;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FutureServiceImpl implements FutureService {

	@Override
    @Async
    public Future<String> futureTest() throws InterruptedException {
        Thread.sleep(5000);
        System.out.println("任务执行开始,需要：5000ms");
        return new AsyncResult<>(Thread.currentThread().getName());
    }
}