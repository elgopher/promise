package com.github.jacekolszak.promises;

import static org.junit.Assert.*;

import org.junit.Test;

public class ThenableSpec {

    private Object resolvedValue;

    private Throwable rejectedException;

    @Test
    public void thenableShouldBeResolved() {
        Promise.resolve(new ResolvingThenable()).then(s -> resolvedValue = s);

        assertEquals("OK", resolvedValue);
    }

    class ResolvingThenable implements Thenable<String> {

        @Override
        public ResolvingThenable then(CheckedFunction then) {
            try {
                then.apply("OK");
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
            return this;
        }

        @Override
        public ResolvingThenable catchReturn(CheckedFunction then) {
            return this;
        }
    }

    @Test
    public void thenableShouldBeRejected() {
        RejectingThenable thenable = new RejectingThenable();
        Promise.resolve(thenable).catchVoid(e -> rejectedException = e);

        assertEquals(thenable.exception, rejectedException);
    }

    class RejectingThenable implements Thenable<String> {

        public final Throwable exception = new Exception();

        @Override
        public RejectingThenable then(CheckedFunction then) {
            return this;
        }

        @Override
        public RejectingThenable catchReturn(CheckedFunction then) {
            try {
                then.apply(exception);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
            return this;
        }
    }

}
