package com.github.jacekolszak.promises;

import static org.junit.Assert.*;

import org.junit.Test;

public class PromiseAllSpec {

    private Object[] resolvedArray;

    private Throwable caughtException;

    @Test
    public void shouldResolveAllPassedArguments() {
        Promise.all(Promise.resolve(1), new Promise<>(p -> p.resolve(2)), 3).
                then(arr -> resolvedArray = arr);

        assertArrayEquals(new Object[]{ 1, 2, 3 }, resolvedArray);
    }

    @Test
    public void shouldRejectWhenOneOfTheArgumentsWasRejected() {
        Throwable exception = new Exception();
        Promise.all(Promise.resolve(1), new Promise<>(p -> p.reject(exception)), 3).
                catchVoid(e -> caughtException = e);

        assertEquals(exception, caughtException);
    }

    @Test
    public void shouldResolveNestedPromise() {
        Promise.all(new Promise<>(p -> p.resolve(new Promise<>(p2 -> p2.resolve(1))))).
                then(arr -> resolvedArray = arr);

        assertArrayEquals(new Object[]{ 1 }, resolvedArray);
    }

    @Test
    public void shouldRejectPromiseWhenArrayIsNull() {
        Object[] args = null;
        Promise.all(args).catchVoid(t -> caughtException = t);

        assertTrue(caughtException instanceof IllegalArgumentException);
    }

    @Test
    public void shouldResolvePromiseForEmptyArray() {
        Object[] args = new Object[0];
        Promise.all(args).thenVoid(t -> resolvedArray = args);

        assertTrue(resolvedArray.length == 0);
    }

    @Test
    public void shouldAllowNullValues() {
        Object[] arrayOfNulls = { null, null };
        Promise.all(arrayOfNulls).then(arr -> resolvedArray = arr);

        assertArrayEquals(arrayOfNulls, resolvedArray);
    }

}
