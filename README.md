# byte-monkey

[![Build Status](https://travis-ci.org/mrwilson/byte-monkey.png?branch=master)](https://travis-ci.org/mrwilson/byte-monkey)

Bytecode-level fault injection for the JVM.

## How to use

```bash
java -javaagent:byte-monkey.jar -jar your-java-app.jar
```

## Supported Modes

 * **Fault**: Throw exceptions from methods that declare those exceptions
 * **Latency**: Introduce latency on method-calls

## Options

 * `mode`: What mode to run in - currently supports `fault` and `latency`. **Default is fault**
 * `rate`: Value between 0 and 1 - how often to activate the fault. **Default is 1, i.e. 100%**
 * `filter`: Only instrument packages or methods matching the (java-style) regex. **Default is .*, i.e. all methods**

byte-monkey is configured with a comma-separated key-value pair string of the options as the agent argument.

```bash
java -javaagent:byte-monkey.jar=mode:fault,rate:0.5,filter:uk/co/probablyfine/ -jar your-java-app.jar
```

The example above would run in fault mode, activating on 50% of eligible method calls, for anything in the package tree below `uk.co.probablyfine`

## TODO

 * Better docs
 * Unwrap exceptions
