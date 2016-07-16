package com.github.jacekolszak.promises;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

public class Timers {

    private static Timer timer = new Timer("Promise Timeout Timer");

    private static ExecutorService executorService = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors());

    private static Promise<Void> delay(long timeMillis, ExecutorService executor) {
        return new Promise<>(p -> timer.schedule(new TimerTask() {
            public void run() {
                executor.submit(() -> p.resolve(null));
            }
        }, timeMillis));
    }

    public static <RESULT> Thenable<RESULT> timeout(Thenable<RESULT> promise, long timeMillis,
                                                    ExecutorService executorService) {
        return (Thenable<RESULT>) Promise.race(
                promise,
                delay(timeMillis, executorService).thenVoid(v -> {
                    throw new TimeoutException("Timeout waiting for promise ");
                })
        );
    }

    public static <RESULT> Thenable<RESULT> timeout(Thenable<RESULT> promise, long timeMillis) {
        return timeout(promise, timeMillis, executorService);
    }
}
