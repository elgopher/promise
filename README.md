# Promises for Java 8
Promises API for Java 8 strongly inspired by ECMAScript 6.0

# Goals
* Make API looking and behaving as ECMAScript 6.0 Promises
* Use all bleeding edge features of Java 8
* API should allow to write code which is concise and easy to reason about 
* API could be used with any other libraries and frameworks which executes code asynchronously such as Netty
* Promises created using API should be thread safe

# Why?
* Because I wanted to have an alternative to writing code with CompletableFutures
* Because JavaScript's Promises are simple, elegant and powerful

# When it should not be used?
* Promises are for one-shot requests, that is, you can send the request and get a self-contained response, no events in the middle
* When you need to monitor progress or map stream of events then use RxJava instead

# Example

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