package com.github.jacekolszak.promises;

@FunctionalInterface
public interface Executor<RESULT> {

    void run(ExecutorParam<RESULT> p) throws Throwable;

}
