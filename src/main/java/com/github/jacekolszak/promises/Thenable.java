package com.github.jacekolszak.promises;

public interface Thenable<RESULT> {

    <NEW_RESULT> Thenable<NEW_RESULT> then(CheckedFunction<RESULT, NEW_RESULT> then);

    default <NEW_RESULT> Thenable<NEW_RESULT> thenPromise(CheckedFunction<RESULT, Thenable<NEW_RESULT>> then) {
        return then((CheckedFunction<RESULT, NEW_RESULT>) then);
    }

    default Thenable<Void> thenVoid(CheckedConsumer<RESULT> then) {
        CheckedFunction<RESULT, Void> function = (r) -> {
            then.accept(r);
            return null;
        };
        return then(function);
    }

    <NEW_RESULT> Thenable<NEW_RESULT> catchReturn(CheckedFunction<Throwable, NEW_RESULT> then);

    default Thenable<Void> catchVoid(CheckedConsumer<Throwable> then) {
        CheckedFunction<Throwable, Void> function = (r) -> {
            then.accept(r);
            return null;
        };
        return catchReturn(function);
    }

}
