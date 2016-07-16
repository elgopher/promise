package com.github.jacekolszak.promises;

class PromiseRace {

    private final PromiseCallbacks<Object> promiseCallbacks;

    public PromiseRace(Object[] values, PromiseCallbacks<Object> promiseCallbacks) {
        if (values == null) {
            throw new IllegalArgumentException("Null array passed to Promise.race");
        }
        this.promiseCallbacks = promiseCallbacks;
        if (values.length == 0) {
            throw new IllegalArgumentException("Array passed to Promise.race cannot be empty");
        } else {
            for (Object value : values) {
                resolve(value);
            }
        }
    }

    private void resolve(Object value) {
        Promise.resolve(value).
                thenVoid(promiseCallbacks::resolve).
                catchVoid(promiseCallbacks::reject);
    }
}
