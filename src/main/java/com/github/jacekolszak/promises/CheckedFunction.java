package com.github.jacekolszak.promises;

@FunctionalInterface
public interface CheckedFunction<IN, OUT> {

    OUT apply(IN in) throws Throwable;

}
