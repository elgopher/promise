package com.github.jacekolszak.promises;

public class ExecutorParam<RESULT> {

    private final Promise<RESULT> promise;

    public ExecutorParam(Promise<RESULT> promise) {
        this.promise = promise;
    }

    public void resolve(RESULT object) {
        promise.resolve(object);
    }

    public void reject(Throwable exception) {
        promise.reject(exception);
    }

}
