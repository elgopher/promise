package com.github.jacekolszak.promises;

import java.util.ArrayList;
import java.util.List;

public class Promise<RESULT> implements Thenable<RESULT> {

    private List<NextPromise> next = new ArrayList<>();

    private PromiseStatus status = PromiseStatus.PENDING;

    private PromiseValue value;

    public Promise(CheckedConsumer<PromiseCallbacks<RESULT>> executor) {
        try {
            executor.accept(new PromiseCallbacks<>(this));
        } catch (Throwable throwable) {
            doReject(throwable);
        }
    }

    protected Promise() {
    }

    protected void setResult(Object result) {
        if (!isValueSet()) {
            this.status = PromiseStatus.RESOLVED;
            this.value = new PromiseValue<>(result);
            fire(result);
        }
    }

    protected void setException(Throwable e) {
        if (!isValueSet()) {
            this.status = PromiseStatus.REJECTED;
            this.value = new PromiseValue<>(e);
            fireError(e);
        }
    }

    protected synchronized void doResolvePromise(Promise<RESULT> promise) {
        promise.thenVoid(this::doResolve);
        promise.catchVoid(this::doReject);
    }

    synchronized void doResolve(RESULT result) {
        if (result instanceof Promise) {
            doResolvePromise((Promise<RESULT>) result);
        } else {
            setResult(result);
        }
    }

    synchronized void doReject(Throwable exception) {
        setException(exception);
    }

    /**
     * TODO Does this method should be thread safe?
     */
    @Override
    public synchronized <NEW_RESULT> Promise<NEW_RESULT> then(CheckedFunction<RESULT, NEW_RESULT> then) {
        SuccessPromise next = new SuccessPromise<>(then);
        addNext(next);
        fireIfNecessarily();
        return next;
    }

    /**
     * TODO Does this method should be thread safe?
     */
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
        next.stream().forEach(next -> next.doResolve(result));
    }

    private void fireError(Throwable exception) {
        next.stream().forEach(next -> next.doReject(exception));
    }

    private void addNext(Promise<Object> promise) {
        this.next.add(new NextPromise<>(promise));
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

    public static <V> Promise<V> resolve(V value) {
        return new Promise<>(p -> p.resolve(value));
    }

    public static <T> Promise<T> resolve(Promise<T> promise) {
        return new Promise<>(p -> p.resolve((T) promise));
    }

    public static <R extends Throwable> Promise<R> reject(R exception) {
        return new Promise<>(p -> p.reject(exception));
    }

}
