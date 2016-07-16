package com.github.jacekolszak.promises;

/**
 * Special kind of {@link java.util.function.Consumer} functional interface which throws exceptions
 */
@FunctionalInterface
public interface CheckedConsumer<T> {

    void accept(T t) throws Throwable;

}
