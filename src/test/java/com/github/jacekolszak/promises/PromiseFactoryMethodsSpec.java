package com.github.jacekolszak.promises;

import static org.junit.Assert.*;

import org.junit.Test;

public class PromiseFactoryMethodsSpec {

    private Object resolvedValue;

    @Test
    public void resolveShouldReturnValueImmediately() {
        String value = "OK";
        Promise.resolve(value).thenVoid(s -> resolvedValue = s);

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

}
