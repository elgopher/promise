package com.github.jacekolszak.promises;

/**
 * Object used to resolve or reject the Promise.
 *
 * @param <RESULT> Type of resolved Promise value.
 */
public class PromiseCallbacks<RESULT> {

    private final Promise<RESULT> promise;

    PromiseCallbacks(Promise<RESULT> promise) {
        this.promise = promise;
    }

    /**
     * Resolve the Promise and run it's all "then" callbacks immediately.
     *
     * @see Thenable
     */
    public void resolve(RESULT object) {
        promise.doResolve(object);
    }

    /**
     * Reject the Promise and run it's all "catch" callbacks immediately.
     *
     * @see Thenable
     */
    public void reject(Throwable exception) {
        promise.doReject(exception);
    }

}
