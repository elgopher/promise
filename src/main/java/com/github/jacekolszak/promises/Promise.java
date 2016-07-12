package com.github.jacekolszak.promises;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Placeholder for outcome
 * Includes callback code - then or catch
 * Contains multiple references to next promises
 */
public class Promise<RESULT> implements Thenable<RESULT> {

    private List<Promise> next = new ArrayList<>();

    private Result<RESULT> result;

    private Throwable exception;

    public Promise(Consumer<ExecutorParam<RESULT>> executor) {
        executor.accept(new ExecutorParam<>(this));
    }

    Promise() {
    }

    protected void setResult(Object result) {
        this.result = new Result(result);
    }

    protected void setException(Throwable e) {
        this.exception = e;
    }

    public synchronized void resolve(RESULT result) {
        setResult(result);
        fire();
    }

    public synchronized void reject(Throwable exception) {
        setException(exception);
        fireError(exception);
    }

    @Override
    public synchronized <NEW_RESULT> Promise<NEW_RESULT> then(Function<RESULT, NEW_RESULT> then) {
        SuccessPromise next = new SuccessPromise<>(then);
        addNext(next);
        fire();
        return next;
    }

    @Override
    public synchronized <NEW_RESULT> Promise<NEW_RESULT> thenPromise(Function<RESULT, Promise<NEW_RESULT>> then) {
        SuccessPromisePromise next = new SuccessPromisePromise<>(then);
        addNext(next);
        fire();
        return next;
    }

    @Override
    public synchronized <NEW_RESULT> Promise<NEW_RESULT> catchReturn(Function<Throwable, NEW_RESULT> caught) {
        ErrorPromise next = new ErrorPromise<>(caught);
        addNext(next);
        fire();
        return next;
    }

    private void fire() {
        if (result != null) {
            fire(result.result);
        } else if (exception != null) {
            fireError(exception);
        }
    }

    protected void fire(RESULT result) {
        next.stream().forEach(next -> next.resolve(result));
    }

    protected void fireError(Throwable exception) {
        next.stream().forEach(next -> next.reject(exception));
    }

    private void addNext(Promise promise) {
        this.next.add(promise);
    }

}
