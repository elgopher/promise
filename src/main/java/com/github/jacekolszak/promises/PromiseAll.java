package com.github.jacekolszak.promises;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

class PromiseAll {

    private final PromiseCallbacks<Object[]> promiseCallbacks;

    private final int count;

    private final ConcurrentMap<Integer, Optional<Object>> responses = new ConcurrentHashMap<>();

    public PromiseAll(Object[] values, PromiseCallbacks<Object[]> promiseCallbacks) {
        if (values == null) {
            throw new IllegalArgumentException("Null array passed to Promise.all");
        }
        this.promiseCallbacks = promiseCallbacks;
        this.count = values.length;
        if (count == 0) {
            promiseCallbacks.resolve(new Object[0]);
        } else {
            for (int i = 0; i < count; i++) {
                resolve(i, values[i]);
            }
        }
    }

    private void resolve(int valueNumber, Object value) {
        Promise.resolve(value).
                then(response -> handleResponse(valueNumber, response)).
                catchVoid(promiseCallbacks::reject);
    }

    private void handleResponse(int valueNumber, Object response) {
        responses.put(valueNumber, Optional.ofNullable(response));
        if (responses.size() == count) {
            Object[] values = new Object[count];
            for (int i = 0; i < count; i++) {
                values[i] = responses.get(i).orElseGet(() -> null);
            }
            promiseCallbacks.resolve(values);
        }
    }

}
