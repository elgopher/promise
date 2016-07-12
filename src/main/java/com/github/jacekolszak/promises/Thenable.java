package com.github.jacekolszak.promises;

import java.util.function.Consumer;
import java.util.function.Function;

public interface Thenable<RESULT> {

    <NEW_RESULT> Promise<NEW_RESULT> then(Function<RESULT, NEW_RESULT> then);

    <NEW_RESULT> Promise<NEW_RESULT> thenPromise(Function<RESULT, Promise<NEW_RESULT>> then);

    default Promise<Void> thenVoid(Consumer<RESULT> then) {
        Function<RESULT, Void> function = (r) -> {
            then.accept(r);
            return null;
        };
        return then(function);
    }

    <NEW_RESULT> Promise<NEW_RESULT> catchReturn(Function<Throwable, NEW_RESULT> then);

    default Promise<Void> catchVoid(Consumer<Throwable> then) {
        Function<Throwable, Void> function = (r) -> {
            then.accept(r);
            return null;
        };
        return catchReturn(function);
    }

}
