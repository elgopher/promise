# Promises for Java 8
Promises for Java 8 strongly inspired by ECMAScript 6.0. Write asynchronous code the way JavaScript does.

[![Build status](https://travis-ci.org/jacekolszak/promises.svg?branch=master)](https://travis-ci.org/jacekolszak/promises)

## What is a Promise?
* **placeholder** for a value (or an exception) returned from an **asynchronous** operation
* internally a promise has three states: pending, resolved, rejected
* after a promise is resolved or rejected its state and value can never be changed
* asynchronous code can be written by chaining promises together
* exception thrown by a promise is propagated through promise chains

## Why would I use it?
* because it is better suited for writing asynchronous code than Java 8's CompletableFuture
    * API is much cleaner and easier to understand - just 2 instance methods and 4 static companion methods 
    * exceptions are properly caught and propagated to *catch* callbacks (no fear that some exception will be eaten up)
    * code written using Promises is more readable and easier to understand
    * exceptions can be handled similar way as they are normally handled in a blocking code

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
* Promises created using API have to be thread safe