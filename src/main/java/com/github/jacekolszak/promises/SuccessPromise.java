package com.github.jacekolszak.promises;

import java.util.function.Function;

public class SuccessPromise<RESULT, NEW_RESULT> extends Promise<RESULT> {

    protected Function<RESULT, NEW_RESULT> thenFunction;

    public SuccessPromise(Function<RESULT, NEW_RESULT> thenFunction) {
        this.thenFunction = thenFunction;
    }

    @Override
    public void resolve(RESULT result) {
        try {
            NEW_RESULT newResult = this.thenFunction.apply(result);
            setResult(newResult);
            fire((RESULT) newResult);
        } catch (Throwable exception) {
            reject(exception);
        }
    }

}
