package com.github.jacekolszak.promises;

/**
 * Special kind of Consumer functional interface which throws exceptions
 */
@FunctionalInterface
public interface CheckedConsumer<T> {

    void accept(T t) throws Throwable;

}
