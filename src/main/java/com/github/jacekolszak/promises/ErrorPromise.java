package com.github.jacekolszak.promises;

import java.util.function.Function;

public class ErrorPromise<RESULT, NEW_RESULT> extends Promise<RESULT> {

    private Function<Throwable, NEW_RESULT> caughtFunction;

    public ErrorPromise(Function<Throwable, NEW_RESULT> caughtFunction) {
        this.caughtFunction = caughtFunction;
    }

    @Override
    public void reject(Throwable exception) {
        try {
            NEW_RESULT newResult = caughtFunction.apply(exception);
            setResult(newResult);
            setException(null);
            fire((RESULT) newResult);
        } catch (Throwable e) {
            setException(e);
            fireError(exception);
        }
    }

}
