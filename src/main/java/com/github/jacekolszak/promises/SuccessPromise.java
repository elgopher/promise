package com.github.jacekolszak.promises;

import java.util.Optional;

class SuccessPromise<IN, OUT> extends Promise<IN> {

    private final Optional<CheckedFunction<IN, OUT>> thenFunction;

    public SuccessPromise(CheckedFunction<IN, OUT> thenFunction) {
        this.thenFunction = Optional.ofNullable(thenFunction);
    }

    @Override
    synchronized void doResolve(IN in) {
        try {
            if (in instanceof Thenable) {
                doResolvePromise((Thenable<IN>) in);
            } else {
                OUT out = this.thenFunction.isPresent() ? this.thenFunction.get().apply(in) : (OUT) in;
                setResult(out);
            }
        } catch (Throwable exception) {
            setException(exception);
        }
    }

}
