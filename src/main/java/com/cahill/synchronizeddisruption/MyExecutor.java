package com.cahill.synchronizeddisruption;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class MyExecutor {

    private final static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private static volatile int runCount = 0;
    public final static AtomicInteger atomicInteger = new AtomicInteger(0);

    public synchronized int submit(Runnable runnable, long initDelay, long delay, TimeUnit timeUnit) {
        if (atomicInteger.get() <= 0) {
            executor.scheduleAtFixedRate(runnable, initDelay, delay, timeUnit);
            runCount += 1;
            atomicInteger.incrementAndGet();
        }
        return MyExecutor.atomicInteger.get();
    }

}
