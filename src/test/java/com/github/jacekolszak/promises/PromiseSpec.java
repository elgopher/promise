package com.github.jacekolszak.promises;

import static org.junit.Assert.*;

import java.util.BitSet;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

public class PromiseSpec {

    private Object resolvedValue;

    @Test
    public void resolvedPromiseShouldSendValueToThenBlock() {
        new Promise<>(p -> p.resolve("OK")).then(val -> resolvedValue = val);

        assertEquals("OK", resolvedValue);
    }

    @Test
    public void rejectedPromiseShouldSendExceptionToCatchBlock() {
        Exception exception = new Exception();
        new Promise<>(p -> p.reject(exception)).catchVoid(t -> resolvedValue = t);

        assertSame(exception, resolvedValue);
    }

    @Test
    public void newPromisePassedToResolveShouldBeBoundedToOriginalPromise() {
        Promise<String> nestedPromise = new Promise<>(nested -> nested.resolve("OK"));
        new Promise<>(p -> p.resolve(nestedPromise)).
                then(s -> resolvedValue = s);

        assertEquals("OK", resolvedValue);
    }

    @Test
    public void valueForAlreadyResolvedPromiseCannotBeOverridden() {
        new Promise<>(p -> {
            p.resolve(1);
            p.resolve(2);
        }).then(val -> resolvedValue = val);

        assertEquals(1, resolvedValue);
    }

    @Test
    public void exceptionForAlreadyRejectedPromiseCannotBeOverridden() {
        new Promise<>(p -> {
            p.reject(new Exception("1"));
            p.reject(new Exception("2"));
        }).catchVoid(val -> resolvedValue = val.getMessage());

        assertEquals("1", resolvedValue);
    }

    @Test
    public void resolveShouldExecuteAllThenCallbacks() {
        BitSet bitSet = new BitSet(3);
        new Promise<>(p -> p.resolve("OK")).
                thenVoid(v -> bitSet.set(0)).
                thenVoid(v -> bitSet.set(1)).
                thenVoid(v -> bitSet.set(2));

        assertTrue(bitSet.get(0));
        assertTrue(bitSet.get(1));
        assertTrue(bitSet.get(2));
    }

    @Test
    public void rejectShouldExecuteOnlyFirstCatchCallback() {
        BitSet bitSet = new BitSet(2);
        new Promise<>(p -> p.reject(new Exception())).
                catchVoid(t -> bitSet.set(0)).
                catchVoid(t -> bitSet.set(1));

        assertTrue(bitSet.get(0));
        assertFalse(bitSet.get(1));
    }

    @Test
    public void throwingErrorInExecutorShouldRejectPromise() {
        Exception e = new Exception();
        new Promise<>(p -> {
            throw e;
        }).catchVoid(t -> resolvedValue = t);

        assertEquals(e, resolvedValue);
    }

    @Test
    public void throwingErrorInThenShouldRejectPromise() {
        Exception e = new Exception();
        new Promise<>(p -> p.resolve("OK")).
                then(s -> {
                    throw e;
                }).
                catchVoid(t -> resolvedValue = t);

        assertEquals(e, resolvedValue);
    }

    @Test
    public void throwingErrorInCatchShouldRejectPromise() {
        Exception e = new Exception();
        new Promise<>(p -> p.resolve("OK")).
                then(s -> {
                    throw e;
                }).
                catchVoid(t -> {
                    throw t;
                }).
                catchVoid(t -> this.resolvedValue = t);

        assertEquals(e, resolvedValue);
    }

    @Test
    public void promiseReturnedByThenShouldCallNextThenInTheOuterChain() {
        new Promise<>(p -> p.resolve("OUTER")).
                thenPromise(s -> new Promise<>(p -> p.resolve("NESTED"))).
                then(n -> resolvedValue = n);

        assertEquals("NESTED", resolvedValue);
    }

    @Test
    public void promiseReturnedByThenShouldCallNextCatchInTheOuterChain() {
        Exception nested = new Exception();
        new Promise<>(p -> p.resolve("OUTER")).
                thenPromise(s -> new Promise<>(p -> p.reject(nested))).
                catchReturn(t -> resolvedValue = t);

        assertEquals(nested, resolvedValue);
    }

    @Test
    public void catchCouldReplaceExceptionWithAnyObject() {
        Exception e = new Exception();
        new Promise<>(p -> p.reject(e)).
                catchReturn(t -> "OK").
                then(s -> resolvedValue = s);

        assertEquals("OK", resolvedValue);
    }

    @Test
    public void newPromiseShouldReturnInternalStateInToString() {
        Promise<String> promise = new Promise<>(p -> {
        });

        assertEquals("Promise(status=PENDING, value=null)", promise.toString());
    }

    @Test
    public void resolvedPromiseShouldReturnInternalStateInToString() {
        Promise<String> promise = new Promise<>(p -> p.resolve("OK"));

        assertEquals("Promise(status=RESOLVED, value=OK)", promise.toString());
    }

    @Test
    public void rejectedPromiseShouldReturnInternalStateInToString() {
        Exception e = new Exception("ERROR");
        Promise<String> promise = new Promise<>(p -> p.reject(e));

        assertEquals("Promise(status=REJECTED, value=" + e + ")", promise.toString());
    }

    @Test
    public void thenCanBeAddedAfterPromiseWasResolvedAndNextPromiseWasExecuted() {
        // given
        Promise<String> promise = new Promise<>(p -> p.resolve("OK"));
        promise.thenVoid(s -> {
        });

        // when
        promise.then(s -> resolvedValue = s);

        // then
        assertEquals("OK", resolvedValue);
    }

    @Test
    public void everyThenCallbackCanBeExecutedOnlyOnce() {
        // given
        AtomicInteger i = new AtomicInteger();
        Promise<String> promise = new Promise<>(p -> p.resolve("OK"));
        promise.thenVoid(s -> i.incrementAndGet());

        // when
        promise.thenVoid(s -> i.incrementAndGet());

        // then
        assertEquals(2, i.get());
    }

    @Test
    public void everyCatchCallbackCanBeExecutedOnlyOnce() {
        // given
        AtomicInteger i = new AtomicInteger();
        Promise<String> promise = new Promise<>(p -> p.reject(new Exception()));
        promise.catchVoid(s -> i.incrementAndGet());

        // when
        promise.catchVoid(s -> i.incrementAndGet());

        // then
        assertEquals(2, i.get());
    }
}
