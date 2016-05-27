# byte-monkey

[![Build Status](https://travis-ci.org/mrwilson/byte-monkey.png?branch=master)](https://travis-ci.org/mrwilson/byte-monkey)

Bytecode-level fault injection for the JVM.

## How to use

```bash
java -javaagent:byte-monkey.jar -jar your-java-app.jar
```

## Supported Modes

 * **Exception**: Throw exceptions from methods that declare those exceptions
 * **Latency**: Introduce latency on method-calls

## TODO

 * Better docs
 * Arg processing
 * Unwrap exceptions
