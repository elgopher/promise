package com.github.jacekolszak.promises;

import java.util.ArrayList;
import java.util.List;

public class Promise<RESULT> implements Thenable<RESULT> {

    private List<Promise> next = new ArrayList<>();

    private PromiseStatus status = PromiseStatus.PENDING;

    private PromiseValue value;

    public Promise(CheckedConsumer<ExecutorParam<RESULT>> executor) {
        try {
            executor.accept(new ExecutorParam<>(this));
        } catch (Throwable throwable) {
            reject(throwable);
        }
    }

    Promise() {
    }

    protected void setResult(Object result) {
        this.status = PromiseStatus.RESOLVED;
        this.value = new PromiseValue<>(result);
        fire(result);
    }

    protected void setException(Throwable e) {
        this.status = PromiseStatus.REJECTED;
        this.value = new PromiseValue<>(e);
        fireError(e);
    }

    synchronized void resolve(RESULT result) {
        if (!isValueSet()) {
            setResult(result);
        }
    }

    synchronized void reject(Throwable exception) {
        if (!isValueSet()) {
            setException(exception);
        }
    }

    @Override
    public synchronized <NEW_RESULT> Promise<NEW_RESULT> then(CheckedFunction<RESULT, NEW_RESULT> then) {
        SuccessPromise next = new SuccessPromise<>(then);
        addNext(next);
        fireIfNecessarily();
        return next;
    }

    @Override
    public synchronized <NEW_RESULT> Promise<NEW_RESULT> thenPromise(
            CheckedFunction<RESULT, Promise<NEW_RESULT>> then) {
        NestedPromise next = new NestedPromise<>(then);
        addNext(next);
        fireIfNecessarily();
        return next;
    }

    @Override
    public synchronized <NEW_RESULT> Promise<NEW_RESULT> catchReturn(CheckedFunction<Throwable, NEW_RESULT> caught) {
        ErrorPromise next = new ErrorPromise<>(caught);
        addNext(next);
        fireIfNecessarily();
        return next;
    }

    private void fireIfNecessarily() {
        if (isValueSet()) {
            if (status == PromiseStatus.RESOLVED) {
                fire(value.value);
            } else {
                fireError((Throwable) value.value);
            }
        }
    }

    private void fire(Object result) {
        next.stream().forEach(next -> next.resolve(result));
    }

    private void fireError(Throwable exception) {
        next.stream().forEach(next -> next.reject(exception));
    }

    private void addNext(Promise promise) {
        this.next.add(promise);
    }

    protected boolean isValueSet() {
        return value != null;
    }

    @Override
    public String toString() {
        return "Promise(" +
                "status=" + status +
                ", value=" + value + ")";
    }
}
