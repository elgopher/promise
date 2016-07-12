package com.github.jacekolszak.promises;

import java.util.function.Function;

public class SuccessPromisePromise<RESULT, NEW_RESULT> extends SuccessPromise<RESULT, Promise<NEW_RESULT>> {

    public SuccessPromisePromise(Function thenFunction) {
        super(thenFunction);
    }

    @Override
    public void resolve(RESULT result) {
        try {
            Promise<NEW_RESULT> newResult = this.thenFunction.apply(result);
            newResult.then((r) -> {
                setResult(r);
                fire((RESULT) newResult);
                return r;
            });
        } catch (Throwable exception) {
            reject(exception);
        }
    }

}
