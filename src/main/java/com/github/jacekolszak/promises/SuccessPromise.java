package com.github.jacekolszak.promises;

class SuccessPromise<RESULT, NEW_RESULT> extends Promise<RESULT> {

    protected CheckedFunction<RESULT, NEW_RESULT> thenFunction;

    public SuccessPromise(CheckedFunction<RESULT, NEW_RESULT> thenFunction) {
        this.thenFunction = thenFunction;
    }

    @Override
    void resolve(RESULT result) {
        try {
            NEW_RESULT newResult = this.thenFunction.apply(result);
            setResult(newResult);
        } catch (Throwable exception) {
            setException(exception);
        }
    }

}
