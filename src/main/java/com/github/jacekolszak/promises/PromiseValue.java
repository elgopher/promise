package com.github.jacekolszak.promises;

public class PromiseValue<RESULT> {

    public final RESULT value;

    public PromiseValue(RESULT value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
