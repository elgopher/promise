package com.github.jacekolszak.promises;

class NestedPromise<RESULT, NEW_RESULT> extends SuccessPromise<RESULT, Promise<NEW_RESULT>> {

    public NestedPromise(CheckedFunction thenFunction) {
        super(thenFunction);
    }

    @Override
    void resolve(RESULT result) {
        try {
            Promise<NEW_RESULT> newResult = this.thenFunction.apply(result);
            newResult.then((r) -> {
                setResult(r);
                return r;
            });
            newResult.catchVoid(this::reject);
        } catch (Throwable exception) {
            reject(exception);
        }
    }

}
