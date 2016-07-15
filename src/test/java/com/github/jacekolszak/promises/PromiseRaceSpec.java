package com.github.jacekolszak.promises;

import static junit.framework.TestCase.*;

import java.util.concurrent.CountDownLatch;

import org.junit.Test;

public class PromiseRaceSpec {

    private Object resolvedValue;

    private Throwable rejectedException;

    private final CountDownLatch latch = new CountDownLatch(1);

    @Test
    public void shouldResolveReturningFirstResolvedValue() throws InterruptedException {
        // given
        Promise<Object> p1 = new Promise<>(p -> new Thread(() -> {
            p.resolve("first");
            latch.countDown();
        }).start());
        Promise<Object> p2 = resolvedPromise(latch);
        latch.await();

        // when
        Promise.race(p1, p2).then(r -> resolvedValue = r);

        // then
        assertEquals("first", resolvedValue);
    }

    private Promise<Object> resolvedPromise(CountDownLatch latch) {
        return new Promise<>(p -> new Thread(() -> {
            try {
                latch.await();
                p.resolve("last");
            } catch (InterruptedException e) {
                fail(e.getMessage());
            }
        }).start());
    }

    @Test
    public void shouldRejectReturningFirstException() throws InterruptedException {
        // given
        Throwable firstException = new Exception();
        Promise<Object> p1 = rejectedPromise(latch, firstException);

        Promise<Object> p2 = new Promise<>(p -> new Thread(() -> {
            try {
                latch.await();
                p.reject(new Exception());
            } catch (InterruptedException e) {
                fail(e.getMessage());
            }
        }).start());
        latch.await();

        // when
        Promise.race(p1, p2).catchVoid(e -> rejectedException = e);

        // then
        assertEquals(firstException, rejectedException);
    }

    private Promise<Object> rejectedPromise(CountDownLatch latch, Throwable firstException) {
        return new Promise<>(p -> new Thread(() -> {
            p.reject(firstException);
            latch.countDown();
        }).start());
    }

    @Test
    public void shouldRejectWhenFirstPromiseIsRejectedEvenIfSecondPromiseIsResolved() throws InterruptedException {
        // given
        Throwable exception = new Exception();
        Promise<Object> p1 = rejectedPromise(latch, exception);
        Promise<Object> p2 = resolvedPromise(latch);
        latch.await();

        // when
        Promise.race(p1, p2).catchVoid(e -> rejectedException = e);

        // then
        assertEquals(exception, rejectedException);
    }

}
