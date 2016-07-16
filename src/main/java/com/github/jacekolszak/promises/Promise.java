package com.github.jacekolszak.promises;

import java.util.ArrayList;
import java.util.List;

public class Promise<RESULT> implements Thenable<RESULT> {

    private final List<NextPromise> next = new ArrayList<>();

    private PromiseStatus status = PromiseStatus.PENDING;

    private PromiseValue value;

    public Promise(CheckedConsumer<PromiseCallbacks<RESULT>> executor) {
        try {
            executor.accept(new PromiseCallbacks<>(this));
        } catch (Throwable throwable) {
            doReject(throwable);
        }
    }

    Promise() {
    }

    void setResult(Object result) {
        if (!isValueSet()) {
            this.status = PromiseStatus.RESOLVED;
            this.value = new PromiseValue(result);
            fire(result);
        }
    }

    void setException(Throwable e) {
        if (!isValueSet()) {
            this.status = PromiseStatus.REJECTED;
            this.value = new PromiseValue(e);
            fireError(e);
        }
    }

    void doResolvePromise(Thenable<RESULT> promise) {
        promise.thenVoid(this::doResolve);
        promise.catchVoid(this::doReject);
    }

    synchronized void doResolve(RESULT result) {
        if (result instanceof Thenable) {
            doResolvePromise((Thenable<RESULT>) result);
        } else {
            setResult(result);
        }
    }

    synchronized void doReject(Throwable exception) {
        setException(exception);
    }

    @Override
    public synchronized <NEW_RESULT> Promise<NEW_RESULT> then(CheckedFunction<RESULT, NEW_RESULT> callback) {
        SuccessPromise<RESULT, NEW_RESULT> next = new SuccessPromise<>(callback);
        addNext(next);
        fireIfNecessarily();
        return (Promise<NEW_RESULT>) next;
    }

    @Override
    public synchronized <NEW_RESULT> Promise<NEW_RESULT> catchReturn(CheckedFunction<Throwable, NEW_RESULT> callback) {
        ErrorPromise next = new ErrorPromise<>(callback);
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

    private void addNext(Promise<RESULT> promise) {
        this.next.add(new NextPromise(promise));
    }

    private boolean isValueSet() {
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

    public static <T> Promise<T> resolve(Thenable<T> promise) {
        return new Promise<>(p -> p.resolve((T) promise));
    }

    public static <R extends Throwable> Promise<R> reject(R exception) {
        return new Promise<>(p -> p.reject(exception));
    }

    public static Promise<Object[]> all(Object... values) {
        return new Promise<>(p -> new PromiseAll(values, p));
    }

    public static Promise<Object> race(Object... values) {
        return new Promise<>(p -> new PromiseRace(values, p));
    }

}
