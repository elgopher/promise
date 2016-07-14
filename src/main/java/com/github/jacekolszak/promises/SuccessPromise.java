package com.github.jacekolszak.promises;

class SuccessPromise<IN, OUT> extends Promise<IN> {

    private final CheckedFunction<IN, OUT> thenFunction;

    public SuccessPromise(CheckedFunction<IN, OUT> thenFunction) {
        this.thenFunction = thenFunction;
    }

    @Override
    synchronized void doResolve(IN in) {
        try {
            if (in instanceof Thenable) {
                doResolvePromise((Thenable<IN>) in);
            } else {
                OUT out = this.thenFunction.apply(in);
                setResult(out);
            }
        } catch (Throwable exception) {
            setException(exception);
        }
    }

}
