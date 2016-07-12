package com.github.jacekolszak.promises;

import static org.junit.Assert.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.LongStream;

import org.junit.Test;

public class PromiseParallelSpec {

    private Object returnedResult;

    @Test
    public void parallelPromiseResolutionNotPossible() throws InterruptedException {
        // given
        int threadsCount = (int) range().count();
        ExecutorService executorService = Executors.newFixedThreadPool(threadsCount);
        AtomicInteger thenExecutionsCount = new AtomicInteger(0);
        CountDownLatch allThreadsReady = new CountDownLatch(threadsCount);

        // when
        new Promise<>(p ->
                range().
                        forEach(i -> executorService.submit(() -> {
                            try {
                                allThreadsReady.countDown();
                                allThreadsReady.await();
                                Thread.sleep(1);
                                p.resolve(i);
                            } catch (InterruptedException e) {
                                fail(e.getMessage());
                            }
                        }))).
                thenVoid(i -> {
                    thenExecutionsCount.incrementAndGet();
                    this.returnedResult = i;
                });
        executorService.awaitTermination(1, TimeUnit.SECONDS);

        // then
        assertEquals(1, thenExecutionsCount.get());
        assertTrue(range().anyMatch(i -> (Long) returnedResult == i));
    }

    private LongStream range() {
        return LongStream.range((long) Integer.MAX_VALUE - 5000, (long) Integer.MAX_VALUE + 5000);
    }

}
