package com.github.jacekolszak.promises;

import static org.junit.Assert.*;

import java.util.BitSet;

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
        // given
        new Promise<>(p -> p.resolve("OK")).then(val -> returnedValue = val);

        // expect
        assertEquals("OK", returnedValue);
    }

    @Test
    public void rejectedPromiseShouldSendExceptionToCatchBlock() {
        // given
        Exception exception = new Exception();
        new Promise<>(p -> p.reject(exception)).catchVoid(t -> returnedValue = t);

        // expect
        assertSame(exception, returnedValue);
    }

    @Test
    public void valueForAlreadyResolvedPromiseCannotBeOverridden() {
        // given
        new Promise<>(p -> {
            p.resolve(1);
            p.resolve(2);
        }).then(val -> returnedValue = val);

        // expect
        assertEquals(1, returnedValue);
    }

    @Test
    public void exceptionForAlreadyRejectedPromiseCannotBeOverridden() {
        // given
        new Promise<>(p -> {
            p.reject(new Exception("1"));
            p.reject(new Exception("2"));
        }).catchVoid(val -> returnedValue = val.getMessage());

        // expect
        assertEquals("1", returnedValue);
    }

    @Test
    public void resolveShouldExecuteAllThenCallbacks() {
        // given
        BitSet bitSet = new BitSet(3);
        new Promise<>(p -> p.resolve("OK")).
                thenVoid(v -> bitSet.set(0)).
                thenVoid(v -> bitSet.set(1)).
                thenVoid(v -> bitSet.set(2));

        // expect
        assertTrue(bitSet.get(0));
        assertTrue(bitSet.get(1));
        assertTrue(bitSet.get(2));
    }

    @Test
    public void rejectShouldExecuteOnlyFirstCatchCallback() {
        // given
        BitSet bitSet = new BitSet(2);
        new Promise<>(p -> p.reject(new Exception())).
                catchVoid(t -> bitSet.set(0)).
                catchVoid(t -> bitSet.set(1));

        // expect
        assertTrue(bitSet.get(0));
        assertFalse(bitSet.get(1));
    }

    @Test
    public void throwingErrorInExecutorShouldRejectPromise() {
        // given
        Exception e = new Exception();
        new Promise<>(p -> {
            throw e;
        }).catchVoid(t -> returnedValue = t);

        // expect
        assertEquals(e, returnedValue);
    }

    @Test
    public void throwingErrorInThenShouldRejectPromise() {
        // given
        Exception e = new Exception();
        new Promise<>(p -> p.resolve("OK")).
                then(s -> {
                    throw e;
                }).
                catchVoid(t -> returnedValue = t);

        // expect
        assertEquals(e, returnedValue);
    }

    @Test
    public void throwingErrorInCatchShouldRejectPromise() {
        // given
        Exception e = new Exception();
        new Promise<>(p -> p.resolve("OK")).
                then(s -> {
                    throw e;
                }).
                catchVoid(t -> {
                    throw t;
                }).
                catchVoid(t -> this.returnedValue = t);

        // expect
        assertEquals(e, returnedValue);
    }

    @Test
    public void newPromiseShouldReturnInternalStateInToString() {
        // given
        Promise<String> promise = new Promise<>(p -> {
        });

        // expect
        assertEquals("Promise(status=PENDING, value=null)", promise.toString());
    }

    @Test
    public void resolvedPromiseShouldReturnInternalStateInToString() {
        // given
        Promise<String> promise = new Promise<>(p -> p.resolve("OK"));

        // expect
        assertEquals("Promise(status=RESOLVED, value=OK)", promise.toString());
    }

}
