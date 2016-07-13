package com.github.jacekolszak.promises;

class PromiseValue {

    public final Object value;

    public PromiseValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
