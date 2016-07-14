package com.github.jacekolszak.promises;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

class PromiseAll {

    private final PromiseCallbacks<Object[]> promiseCallbacks;

    private int count;

    private ConcurrentMap<Integer, Object> responses = new ConcurrentHashMap<>();

    public PromiseAll(Object[] objects, PromiseCallbacks<Object[]> promiseCallbacks) {
        this.promiseCallbacks = promiseCallbacks;
        this.count = objects.length;
        for (int i = 0; i < count; i++) {
            resolve(i, objects[i]);
        }
    }

    private Promise<Void> resolve(int objectNumber, Object object) {
        return Promise.resolve(object).
                thenVoid(response -> handleResponse(objectNumber, response)).
                catchVoid(promiseCallbacks::reject);
    }

    private void handleResponse(int objectNumber, Object response) {
        responses.put(objectNumber, response);
        if (responses.size() == count) {
            Object[] values = new Object[count];
            for (int i = 0; i < count; i++) {
                values[i] = responses.get(i);
            }
            promiseCallbacks.resolve(values);
        }
    }

}
