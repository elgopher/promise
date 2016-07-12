package com.github.jacekolszak.promises;

/**
 * wrapper zeby rozrozniac null od braku odpowiedzi
 */
public class Result<RESULT> {

    public final RESULT result;

    public Result(RESULT result) {
        this.result = result;
    }

}
