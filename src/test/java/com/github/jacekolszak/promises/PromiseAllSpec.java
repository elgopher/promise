package com.github.jacekolszak.promises;

import static org.junit.Assert.*;

import org.junit.Test;

public class PromiseAllSpec {

    private Object[] returnedArray;
    private Throwable returnedException;

    @Test
    public void shouldResolveAllPassedArguments() {
        Promise.all(Promise.resolve(1), new Promise<>(p -> p.resolve(2)), 3).
                then(arr -> returnedArray = arr);

        assertArrayEquals(new Object[] { 1, 2, 3 }, returnedArray);
    }

    @Test
    public void shouldRejectWhenOneOfTheArgumentsWasRejected() {
        Throwable exception = new Exception();
        Promise.all(Promise.resolve(1), new Promise<>(p -> p.reject(exception)), 3).
                catchVoid(e -> returnedException = e);

        assertEquals(exception, returnedException);
    }

    @Test
    public void shouldResolveNestedPromise() {
        Promise.all(new Promise<>(p -> p.resolve(new Promise<>(p2 -> p2.resolve(1))))).
                then(arr -> returnedArray = arr);

        assertArrayEquals(new Object[] { 1 }, returnedArray);
    }


}
