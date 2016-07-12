package com.github.jacekolszak.promises;

public class SuccessPromise<RESULT, NEW_RESULT> extends Promise<RESULT> {

    protected CheckedFunction<RESULT, NEW_RESULT> thenFunction;

    public SuccessPromise(CheckedFunction<RESULT, NEW_RESULT> thenFunction) {
        this.thenFunction = thenFunction;
    }

    @Override
    public void resolve(RESULT result) {
        try {
            NEW_RESULT newResult = this.thenFunction.apply(result);
            setResult(newResult);
        } catch (Throwable exception) {
            setException(exception);
        }
    }

}
