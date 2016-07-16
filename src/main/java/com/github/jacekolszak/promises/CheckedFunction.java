package com.github.jacekolszak.promises;

/**
 * Special kind of {@link java.util.function.Function} functional interface which throws exceptions
 */
@FunctionalInterface
public interface CheckedFunction<IN, OUT> {

    OUT apply(IN in) throws Throwable;

}
