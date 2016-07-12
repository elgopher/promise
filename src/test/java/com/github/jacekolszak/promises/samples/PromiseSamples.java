package com.github.jacekolszak.promises.samples;

import org.junit.Test;

import com.github.jacekolszak.promises.Promise;

public class PromiseSamples {

    @Test
    public void loadFakeRestResource() {
        httpGet("http://github.com").
                then(String::trim).
                then(String::toLowerCase).
                thenVoid(System.out::println).
                catchVoid(System.err::println);
    }

    private Promise<String> httpGet(String url) {
        return new Promise<>(p -> {
            // execute HTTP request asynchronously here (Netty etc.)
            p.resolve("Some RESPONSE from remote host " + url);
        });
    }

}
