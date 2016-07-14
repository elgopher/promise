package com.github.jacekolszak.promises;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

class PromiseAll {

    private final PromiseCallbacks<Object[]> promiseCallbacks;

    private final int count;

    private final ConcurrentMap<Integer, Object> responses = new ConcurrentHashMap<>();

    public PromiseAll(Object[] values, PromiseCallbacks<Object[]> promiseCallbacks) {
        this.promiseCallbacks = promiseCallbacks;
        this.count = values.length;
        for (int i = 0; i < count; i++) {
            resolve(i, values[i]);
        }
    }

    private void resolve(int valueNumber, Object value) {
        Promise.resolve(value).
                thenVoid(response -> handleResponse(valueNumber, response)).
                catchVoid(promiseCallbacks::reject);
    }

    private void handleResponse(int valueNumber, Object response) {
        responses.put(valueNumber, response);
        if (responses.size() == count) {
            Object[] values = new Object[count];
            for (int i = 0; i < count; i++) {
                values[i] = responses.get(i);
            }
            promiseCallbacks.resolve(values);
        }
    }

}
