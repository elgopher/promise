package com.github.jacekolszak.promises;

import static com.github.jacekolszak.promises.Timers.*;
import static junit.framework.TestCase.*;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

public class TimersSpec {

    private boolean resolved;

    private Throwable exceptionCaught;

    private CountDownLatch latch = new CountDownLatch(1);

    @Test
    public void shouldTimeoutWithException() throws InterruptedException {
        // given
        timeout(neverEndingPromise(), 10).
                catchVoid(e -> {
                    exceptionCaught = e;
                    latch.countDown();
                });

        // when
        latch.await(50, TimeUnit.MILLISECONDS);

        // then
        assertTrue(exceptionCaught instanceof TimeoutException);
    }

    @Test
    public void shouldTimeoutUsingExecutorService() throws InterruptedException {
        // given
        ExecutorServiceSpy executorService = new ExecutorServiceSpy();
        timeout(neverEndingPromise(), 10, executorService).
                catchVoid(e -> {
                    latch.countDown();
                });

        // when
        latch.await(50, TimeUnit.MILLISECONDS);

        // then
        assertEquals(1, executorService.numberOfSubmittedTasks.get());
    }

    private Promise<Object> neverEndingPromise() {
        return new Promise<>(p -> {
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullPromiseShouldThrowIllegalArgumentException() {
        timeout(null, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeTimeoutShouldThrowIllegalArgumentException() {
        timeout(neverEndingPromise(), -10);
    }

    @Test
    public void passingNullExecutorServiceShouldUseDefaultOne() throws InterruptedException {
        // given
        timeout(neverEndingPromise(), 10, null).
                catchVoid(e -> {
                    exceptionCaught = e;
                    latch.countDown();
                });

        // when
        latch.await(50, TimeUnit.MILLISECONDS);

        // then
        assertTrue(exceptionCaught instanceof TimeoutException);
    }

    @Test
    public void delayShouldRunThenCallbackAfterSpecifiedAmountOfTime() throws InterruptedException {
        // given
        delay(10).thenVoid(v -> {
            resolved = true;
            latch.countDown();
        });

        // when
        latch.await(50, TimeUnit.MILLISECONDS);

        // then
        assertTrue(resolved);
    }

    @Test
    public void delayShouldRunThenCallbackWithCustomExecutorService() throws InterruptedException {
        // given
        ExecutorServiceSpy executorService = new ExecutorServiceSpy();
        delay(10, executorService).thenVoid(v -> latch.countDown());

        // when
        latch.await(50, TimeUnit.MILLISECONDS);

        // then
        assertEquals(1, executorService.numberOfSubmittedTasks.get());
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeDelayShouldThrowIllegalArgumentException() {
        delay(-10);
    }

    @Test
    public void passingNullExecutorServiceToDelayShouldUseDefaultOne() throws InterruptedException {
        // given
        delay(10, null).thenVoid(v -> {
            resolved = true;
            latch.countDown();
        });

        // when
        latch.await(50, TimeUnit.MILLISECONDS);

        // then
        assertTrue(resolved);
    }

    class ExecutorServiceSpy extends ThreadPoolExecutor {

        final AtomicInteger numberOfSubmittedTasks = new AtomicInteger(0);

        ExecutorServiceSpy() {
            super(1, 1, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1));
        }

        @Override
        public Future<?> submit(Runnable task) {
            Future<?> future = super.submit(task);
            numberOfSubmittedTasks.incrementAndGet();
            return future;
        }

    }
}
