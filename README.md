# Promises for Java 8
Promises API for Java 8 strongly inspired by ECMAScript 6.0

[![Build status](https://travis-ci.org/jacekolszak/promises.svg?branch=master)](https://travis-ci.org/jacekolszak/promises)

## What is a Promise?
* placeholder for a value (or an exception) returned from an asynchronous operation
* internally a promise has three states: pending, resolved, rejected
* after a promise is resolved or rejected its state and value can never be changed
* you can write asynchronous code by chaining promises together
* exceptions thrown by promises are propagated through promise chains

## Why would I use it?
* because it is a much more cleaner alternative to Java 8 CompletableFuture (TODO write here some proofs)

## When it should not be used?
* Promises are for one-shot operations, that is, you can execute some method and get a self-contained response, i.e. get some REST resource
* When you need to monitor progress of the execution or process stream of events then use RxJava instead

## Example

```java
httpGet("http://github.com").
        then(String::trim).
        then(String::toLowerCase).
        thenVoid(System.out::println).
        catchVoid(System.err::println);

private Promise<String> httpGet(String url) {
    return new Promise<>(p -> {
        // execute HTTP request asynchronously here (Netty etc.)
        p.resolve("Some RESPONSE from remote host " + url);
    });
}
```

## How to use with Gradle

Add repository to your build.gradle file:

```groovy
repositories {
    maven {
        url  "http://dl.bintray.com/jacekolszak/maven" 
    }    
}
```

And use the artifact like this:

```groovy
dependencies {
    compile  "com.github.jacekolszak:promises:0.5"
}
```