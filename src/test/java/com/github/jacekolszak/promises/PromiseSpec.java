package com.github.jacekolszak.promises;

import static org.junit.Assert.*;

import java.util.BitSet;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;

public class PromiseSpec {

    private Object returnedValue;

    @Before
    public void setup() {
        returnedValue = null;
    }

    @Test
    public void resolvedPromiseShouldSendValueToThenBlock() {
        new Promise<>(p -> p.resolve("OK")).then(val -> returnedValue = val);

        assertEquals("OK", returnedValue);
    }

    @Test
    public void rejectedPromiseShouldSendExceptionToCatchBlock() {
        Exception exception = new Exception();
        new Promise<>(p -> p.reject(exception)).catchVoid(t -> returnedValue = t);

        assertSame(exception, returnedValue);
    }

    @Test
    public void newPromisePassedToResolveShouldBeBoundedToOriginalPromise() {
        Promise<String> nestedPromise = new Promise<>(nested -> nested.resolve("OK"));
        new Promise<>(p -> p.resolve(nestedPromise)).
                then(s -> returnedValue = s);

        assertEquals("OK", returnedValue);
    }

    @Test
    public void valueForAlreadyResolvedPromiseCannotBeOverridden() {
        new Promise<>(p -> {
            p.resolve(1);
            p.resolve(2);
        }).then(val -> returnedValue = val);

        assertEquals(1, returnedValue);
    }

    @Test
    public void exceptionForAlreadyRejectedPromiseCannotBeOverridden() {
        new Promise<>(p -> {
            p.reject(new Exception("1"));
            p.reject(new Exception("2"));
        }).catchVoid(val -> returnedValue = val.getMessage());

        assertEquals("1", returnedValue);
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
        }).catchVoid(t -> returnedValue = t);

        assertEquals(e, returnedValue);
    }

    @Test
    public void throwingErrorInThenShouldRejectPromise() {
        Exception e = new Exception();
        new Promise<>(p -> p.resolve("OK")).
                then(s -> {
                    throw e;
                }).
                catchVoid(t -> returnedValue = t);

        assertEquals(e, returnedValue);
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
                catchVoid(t -> this.returnedValue = t);

        assertEquals(e, returnedValue);
    }

    @Test
    public void promiseReturnedByThenShouldCallNextThenInTheOuterChain() {
        new Promise<>(p -> p.resolve("OUTER")).
                thenPromise(s -> new Promise<>(p -> p.resolve("NESTED"))).
                then(n -> returnedValue = n);

        assertEquals("NESTED", returnedValue);
    }

    @Test
    public void promiseReturnedByThenShouldCallNextCatchInTheOuterChain() {
        Exception nested = new Exception();
        new Promise<>(p -> p.resolve("OUTER")).
                thenPromise(s -> new Promise<>(p -> p.reject(nested))).
                catchReturn(t -> returnedValue = t);

        assertEquals(nested, returnedValue);
    }

    @Test
    public void catchCouldReplaceExceptionWithAnyObject() {
        Exception e = new Exception();
        new Promise<>(p -> p.reject(e)).
                catchReturn(t -> "OK").
                then(s -> returnedValue = s);

        assertEquals("OK", returnedValue);
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
        promise.thenVoid(s -> {});

        // when
        promise.then(s ->  returnedValue = s);

        // then
        assertEquals("OK", returnedValue);
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

}
