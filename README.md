# Promises for Java 8
Promises for Java 8 strongly inspired by ECMAScript 6.0

[![Build status](https://travis-ci.org/jacekolszak/promises.svg?branch=master)](https://travis-ci.org/jacekolszak/promises)

## What is a Promise?
* placeholder for a value (or an exception) returned from an asynchronous operation
* internally a promise has three states: pending, resolved, rejected
* after a promise is resolved or rejected its state and value can never be changed
* asynchronous code can be written by chaining promises together
* exception thrown by a promise is propagated through promise chains

## Why would I use it?
* because it is a much more cleaner alternative to Java 8 CompletableFuture (TODO write here some proofs)

## When it should not be used?
* Promises are for one-shot operations, that is, you can execute some method and get a self-contained response, i.e. get some REST resource
* When you need to monitor progress of the execution or process a stream of events then use RxJava instead

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

## Project goals
* Make API looking and behaving exactly the same as ECMAScript 6.0 Promises
* Use all bleeding edge features of Java 8
* API should allow to write code which is concise and easy to reason about 
* API could be used with any other libraries and frameworks which executes code asynchronously (i.e. Netty)
* Promises created using API should be thread safe