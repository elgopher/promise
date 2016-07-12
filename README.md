# promises
JavaScript like Promises for Java 8

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