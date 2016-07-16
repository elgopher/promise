package com.github.jacekolszak.promises;

import static java.lang.Runtime.*;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

public class Timers {

    private static Timer timer = new Timer("Promise Timeout Timer");

    private static ExecutorService defaultExecutorService = Executors.newFixedThreadPool(
            getRuntime().availableProcessors());

    private static Promise<Void> delay(long delay, ExecutorService executor) {
        return new Promise<>(p -> timer.schedule(new TimerTask() {
            public void run() {
                executor.submit(() -> p.resolve(null));
            }
        }, delay));
    }

    /**
     * Overloaded @{link Timers#timeout} method using passed {@link ExecutorService} instead of default one.
     * This method can be used to control the size and behaviour of the thread pool.
     *
     * @see Timers#timeout(Thenable, long)
     */
    public static <RESULT> Thenable<RESULT> timeout(Thenable<RESULT> promise, long delay,
                                                    ExecutorService executorService) {
        if (promise == null) throw new IllegalArgumentException("Promise cannot be null");
        if (delay < 0) throw new IllegalArgumentException("Delay cannot be negative");
        ExecutorService executor = executorService != null ? executorService : Timers.defaultExecutorService;
        return (Thenable<RESULT>) Promise.race(
                promise,
                delay(delay, executor).thenVoid(v -> {
                    throw new TimeoutException("Timeout waiting for promise ");
                })
        );
    }

    /**
     * Create a Promise which will be rejected after specific timeout or resolved when promise passed as an argument
     * is resolved. "Catch" callbacks of created Promise will be executed in a thread pool with the size of available
     * processors.
     *
     * @param promise When promise resolves the created Timeout promise also resolves. If promise rejects before
     *                timeout
     *                then Timeout promise also rejects.
     * @param delay   Delay in milliseconds
     */
    public static <RESULT> Thenable<RESULT> timeout(Thenable<RESULT> promise, long delay) {
        return timeout(promise, delay, defaultExecutorService);
    }
}
