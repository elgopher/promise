package com.github.jacekolszak.promises;

public class NextPromise<T> {

    private final Promise<T> promise;

    private boolean executed;

    public NextPromise(Promise<T> promise) {
        this.promise = promise;
    }

    void doResolve(T result) {
        if (!executed) {
            executed = true;
            promise.doResolve(result);
        }
    }

    void doReject(Throwable exception) {
        if (!executed) {
            executed = true;
            promise.doReject(exception);
        }
    }

}
