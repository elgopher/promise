package com.github.jacekolszak.promises;

class NextPromise {

    private final Promise promise;

    private boolean executed;

    public NextPromise(Promise promise) {
        this.promise = promise;
    }

    void doResolve(Object result) {
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
