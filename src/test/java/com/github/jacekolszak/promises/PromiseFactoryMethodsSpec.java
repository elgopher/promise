package com.github.jacekolszak.promises;

import static com.github.jacekolszak.promises.Promise.*;
import static org.junit.Assert.*;

import java.util.concurrent.CompletableFuture;

import org.junit.Test;

public class PromiseFactoryMethodsSpec {

    private Object resolvedValue;

    @Test
    public void resolveShouldReturnValueImmediately() {
        String value = "OK";
        Promise.resolve(value).then(s -> resolvedValue = s);

        assertEquals(value, resolvedValue);
    }

    @Test
    public void rejectShouldReturnExceptionImmediately() {
        Throwable e = new Exception();
        Promise.reject(e).catchVoid(t -> resolvedValue = t);

        assertEquals(e, resolvedValue);
    }

    @Test
    public void resolveWithPromiseParameterShouldReturnValueFromThisPromise() {
        String nestedValue = "OK";
        Promise<String> nestedPromise = new Promise<>(p -> p.resolve(nestedValue));
        Promise.resolve(nestedPromise).then(s -> resolvedValue = s);

        assertEquals(nestedValue, resolvedValue);
    }

    @Test
    public void shouldPassValueFromCompletableFuture() {
        // given
        CompletableFuture<String> future = new CompletableFuture<>();
        toPromise(future).then(s -> resolvedValue = s);

        // when
        future.complete("OK");

        // then
        assertEquals("OK", resolvedValue);
    }

    @Test
    public void shouldPassExceptionFromCompletableFuture() {
        // given
        CompletableFuture<String> future = new CompletableFuture<>();
        toPromise(future).catchVoid(e -> resolvedValue = e);

        // when
        Exception exception = new Exception();
        future.completeExceptionally(exception);

        // then
        assertSame(exception, resolvedValue);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWhenCompletableFutureIsNull() {
        toPromise(null);
    }

}
