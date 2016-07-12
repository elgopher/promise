package com.github.jacekolszak.promises;

@FunctionalInterface
public interface CheckedConsumer<T> {

    void accept(T t) throws Throwable;

}