# Promises for Java 8
Promises for Java 8 strongly inspired by [ECMAScript 6.0](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise). Write asynchronous code the way JavaScript does.

[![Build status](https://travis-ci.org/jacekolszak/promises.svg?branch=master)](https://travis-ci.org/jacekolszak/promises)

## What is a Promise?
* **placeholder** for a value (or an exception) returned from an **asynchronous** operation
* internally a promise has three states: pending, resolved, rejected
* after a promise is resolved or rejected its state and value can never be changed
* asynchronous code can be written by chaining promises together
* exception thrown by a promise is propagated through promise chains

## Why would I use it?
* because it is better suited for writing asynchronous code than Java 8's CompletableFuture
    * API is much cleaner and easier to understand - just 2 instance method types (_thenXXX_, _catchXXX_) and 4 static companion methods (_all_, _race_, _resolve_, _reject_) 
    * exceptions are properly caught and propagated to *catch* callbacks (no fear that some exception will be eaten up)
    * exceptions can be handled similar way as they are normally handled in a blocking code
    * code written using Promises is more readable and easier to understand

## When it should not be used?
* Promises are for one-shot operations, that is, you can execute some method and get a self-contained response (or error), i.e. get some REST resource
* When you need to monitor progress of the execution or process a stream of events then use something like [RxJava](https://github.com/ReactiveX/RxJava) instead

## Examples

```java
public void loadFakeJSON() {
    getJSON("http://github.com").
            then(json -> json.get("someProperty")).
            then(String::toLowerCase).
            thenVoid(System.out::println).
            catchVoid(Throwable::printStackTrace);
}

public void loadJSONSequentially() {
    getJSON("http://github.com").
            thenPromise(json -> getJSON(json.get("otherURL"))).
            thenVoid(System.out::println).
            catchVoid(Throwable::printStackTrace);
}

public void catchFallback() {
    getJSON("http://unreliable-url.com").
            catchReturn(json -> {
                Map<String, String> fallbackJSON = new HashMap<>();
                fallbackJSON.put("someProperty", "defaultValue");
                fallbackJSON.put("otherURL", "http://default-url.com");
                return fallbackJSON;
            }).
            thenVoid(System.out::println).
            catchVoid(Throwable::printStackTrace);
}

public void all() {
    Promise.all(
            getJSON("https://fake-url.com/resources/1"),
            getJSON("https://fake-url.com/resources/2"),
            getJSON("https://fake-url.com/resources/3")
    ).thenVoid(jsons -> {
        System.out.println(jsons[0]);
        System.out.println(jsons[1]);
        System.out.println(jsons[2]);
    });
}

public void race() {
    Promise.race(
            getJSON("https://fake-url.com/resources/1"),
            getJSON("https://fake-url.com/resources/2"),
            getJSON("https://fake-url.com/resources/3")
    ).thenVoid(System.out::println);
}


private Promise<Map<String, String>> getJSON(String url) {
    return new Promise<>(p -> {
        // execute HTTP request asynchronously here (some Netty based client etc.)
        Map<String, String> json = new HashMap<>();
        json.put("someProperty", "value");
        json.put("otherURL", "http://google.com");
        p.resolve(json);
    });
}
```

Source code: [PromiseSamples.java](src/test/java/com/github/jacekolszak/promises/samples/PromiseSamples.java)

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
    compile  "com.github.jacekolszak:promises:0.6"
}
```

## Project goals
* Make API looking and behaving exactly the same as ECMAScript 6.0 Promises
* Use all bleeding edge features of Java 8
* API should allow to write code which is concise and easy to reason about 
* API could be used with any other libraries and frameworks which executes code asynchronously (i.e. [Netty](https://github.com/netty/netty))
* Promises created using API have to be thread safe