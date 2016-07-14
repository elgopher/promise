package com.github.jacekolszak.promises.samples;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.github.jacekolszak.promises.Promise;

public class PromiseSamples {

    @Test
    public void loadFakeJSON() {
        getJSON("http://github.com").
                then(resp -> resp.get("someProperty")).
                then(String::toLowerCase).
                thenVoid(System.out::println).
                catchVoid(Throwable::printStackTrace);
    }

    @Test
    public void loadJSONSequentially() {
        getJSON("http://github.com").
                thenPromise(resp -> getJSON(resp.get("otherURL"))).
                thenVoid(System.out::println).
                catchVoid(Throwable::printStackTrace);
    }

    private Promise<Map<String, String>> getJSON(String url) {
        return new Promise<>(p -> {
            // execute HTTP request asynchronously here (Netty etc.)
            Map<String, String> json = new HashMap<>();
            json.put("someProperty", "value");
            json.put("otherURL", "http://google.com");
            p.resolve(json);
        });
    }

}
