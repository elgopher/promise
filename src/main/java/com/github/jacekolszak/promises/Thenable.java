package com.github.jacekolszak.promises;

public interface Thenable<RESULT> {

    /**
     * Run the callback when Promise is resolved (on success).
     *
     * @param callback Callback executed at most one time even if Promise was resolved multiple times by many
     *                 threads. Value returned by callback is passed to the next Promise in the chain. When callback
     *                 returns a Promise it is first resolved before passing to the next Promise in the chain.
     */
    <NEW_RESULT> Thenable<NEW_RESULT> then(CheckedFunction<RESULT, NEW_RESULT> callback);

    /**
     * Run the callback when Promise is resolved (on success). This is a special case of Thenable#then(CheckedFunction)
     * method for callbacks returning Promises. Please note that this method is just a syntactic sugar, created to
     * avoid casting Promises in your code. The same result could be achieved using Thenable#then(CheckedFunction)
     *
     * @see Thenable#then(CheckedFunction)
     */
    default <NEW_RESULT> Thenable<NEW_RESULT> thenPromise(CheckedFunction<RESULT, Thenable<NEW_RESULT>> callback) {
        return then((CheckedFunction<RESULT, NEW_RESULT>) callback);
    }

    /**
     * Run the callback when Promise is resolved (on success). This is a special case of Thenable#then(CheckedFunction)
     * method for callbacks that don't return anything. Please note that this method is just a syntactic sugar,
     * created to avoid putting return statements in your code. The same result could be achieved using
     * Thenable#then(CheckedFunction) and returning a null value.
     *
     * @see Thenable#then(CheckedFunction)
     */
    default Thenable<Void> thenVoid(CheckedConsumer<RESULT> then) {
        CheckedFunction<RESULT, Void> function = (r) -> {
            then.accept(r);
            return null;
        };
        return then(function);
    }

    /**
     * Run the callback when Promise is rejected (on error).
     *
     * @param callback Executed at most one time even if Promise was rejected multiple times by many threads.
     *                 Value returned from callback is passed to the next Promise in the chain. When callback
     *                 returns a Promise it is first resolved before passing to the next Promise in the chain.
     */
    <NEW_RESULT> Thenable<NEW_RESULT> catchReturn(CheckedFunction<Throwable, NEW_RESULT> callback);

    /**
     * Run the callback when Promise is rejected (on error). This is a special case of
     * Thenable#catchReturn(CheckedFunction) method for callbacks that don't return anything. Please note that this
     * method is just a syntactic sugar, created to avoid putting return statements in your code.
     * The same result could be achieved using Thenable#catchReturn(CheckedFunction) and returning a null value.
     *
     * @see Thenable#catchReturn(CheckedFunction)
     */
    default Thenable<Void> catchVoid(CheckedConsumer<Throwable> callback) {
        CheckedFunction<Throwable, Void> function = (r) -> {
            callback.accept(r);
            return null;
        };
        return catchReturn(function);
    }

}
