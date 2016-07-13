package com.github.jacekolszak.promises;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class PromiseFactoryMethodsSpec {

    private Object returnedObject;

    @Before
    public void setup() {
        this.returnedObject = null;
    }

    @Test
    public void resolveShouldReturnValueImmediately() {
        String value = "OK";
        Promise.resolve(value).thenVoid(s -> returnedObject = s);

        assertEquals(value, returnedObject);
    }

    @Test
    public void rejectShouldReturnExceptionImmediately() {
        Throwable e = new Exception();
        Promise.reject(e).catchVoid(t -> returnedObject = t);

        assertEquals(e, returnedObject);
    }

}
