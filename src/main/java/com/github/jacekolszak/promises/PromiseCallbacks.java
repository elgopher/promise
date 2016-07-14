package com.github.jacekolszak.promises;

public class PromiseCallbacks<RESULT> {

    private final Promise<RESULT> promise;

    PromiseCallbacks(Promise<RESULT> promise) {
        this.promise = promise;
    }

    public void resolve(RESULT object) {
        promise.doResolve(object);
    }

    public void reject(Throwable exception) {
        promise.doReject(exception);
    }

}
