package com.github.jacekolszak.promises;

public class PromiseTest {

    static  Object general, project;

    public static void main(String[] args) {
        Promise promise =
                new Promise<String>(p -> p.resolve("AAA")).
                        then(s -> s.substring(0, 1)).
                        thenPromise(s ->
                                new Promise<>(p -> {
                                    throw new RuntimeException("1");
                                })
                        ).
                        then(s -> {
                            throw new RuntimeException("2");
                        }).
                        thenVoid(System.out::println).
                        catchReturn(e -> {
                            System.err.println(e);
                            return "dummy";
                        }).
                        thenVoid(System.out::println).
                        then(v -> {
                            throw new RuntimeException("3");
                        }).
                        catchVoid(Throwable::printStackTrace);

    }

}
