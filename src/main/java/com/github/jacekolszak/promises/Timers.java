package com.github.jacekolszak.promises;

import static java.lang.Runtime.*;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

/**
 * Helper methods not related to Promises API, but still useful in many situations.
 */
public class Timers {

    private static Timer timer = new Timer("Promise Timeout Timer");

    private static ExecutorService defaultExecutorService = Executors.newFixedThreadPool(
            getRuntime().availableProcessors());

    /**
     * Create a Promise which resolves after specified delay. "Then" callback of created Promise will be executed
     * in a thread pool with the size of available processors.
     *
     * @param delay Time in millis
     */
    public static Promise<Void> delay(long delay) {
        return delay(delay, defaultExecutorService);
    }

    /**
     * Overloaded {@link Timers#delay(long)} method using passed {@link ExecutorService} instead of default one.
     * This method can be used to control the size and behaviour of the thread pool.
     *
     * @see Timers#delay(long)
     */
    public static Promise<Void> delay(long delay, ExecutorService executorService) {
        if (delay < 0) throw new IllegalArgumentException("Delay cannot be negative");
        ExecutorService executor = executorService != null ? executorService : Timers.defaultExecutorService;
        return new Promise<>(p -> timer.schedule(new TimerTask() {
            public void run() {
                executor.submit(() -> p.resolve(null));
            }
        }, delay));
    }

    /**
     * Overloaded {@link Timers#timeout(Thenable, long)} method using passed
     * {@link ExecutorService} instead of default one.
     * This method can be used to control the size and behaviour of the thread pool.
     *
     * @see Timers#timeout(Thenable, long)
     */
    public static <RESULT> Thenable<RESULT> timeout(Thenable<RESULT> promise, long delay,
                                                    ExecutorService executorService) {
        if (promise == null) throw new IllegalArgumentException("Promise cannot be null");
        return (Thenable<RESULT>) Promise.race(
                promise,
                delay(delay, executorService).thenVoid(v -> {
                    throw new TimeoutException("Timeout waiting for promise ");
                })
        );
    }

    /**
     * Create a Promise which will be rejected after specific timeout or resolved when promise passed as an argument
     * is resolved. "Catch" callback of created Promise will be executed in a thread pool with the size of available
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
