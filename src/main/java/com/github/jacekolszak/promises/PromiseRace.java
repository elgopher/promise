package com.github.jacekolszak.promises;

class PromiseRace {

    private final PromiseCallbacks<Object> promiseCallbacks;

    public PromiseRace(Object[] values, PromiseCallbacks<Object> promiseCallbacks) {
        this.promiseCallbacks = promiseCallbacks;
        for (Object value : values) {
            resolve(value);
        }
    }

    private void resolve(Object value) {
        Promise.resolve(value).
                thenVoid(promiseCallbacks::resolve).
                catchVoid(promiseCallbacks::reject);
    }
}
