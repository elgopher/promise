package com.github.jacekolszak.promises;

import static com.github.jacekolszak.promises.Timers.*;
import static junit.framework.TestCase.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

public class TimersSpec {

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
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        timeout(neverEndingPromise(), 10, executorService).
                catchVoid(e -> {
                    exceptionCaught = e;
                    latch.countDown();
                });

        // when
        latch.await(50, TimeUnit.MILLISECONDS);

        // then
        assertTrue(exceptionCaught instanceof TimeoutException);
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

}
