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
 * **Nullify**: Replace the first non-primitive argument to the method with *null*

## Options

 * `mode`: What mode to run in - currently supports `fault`, `latency`, and `nullify`. **Default is fault**
 * `rate`: Value between 0 and 1 - how often to activate the fault. **Default is 1, i.e. 100%**
 * `filter`: Only instrument packages or methods matching the (java-style) regex. **Default is .*, i.e. all methods**

byte-monkey is configured with a comma-separated key-value pair string of the options as the agent argument.

```bash
java -javaagent:byte-monkey.jar=mode:fault,rate:0.5,filter:uk/co/probablyfine/ -jar your-java-app.jar
```

The example above would run in fault mode, activating on 50% of eligible method calls, for anything in the package tree below `uk.co.probablyfine`

## Modes

### Fault

Running byte-monkey in `fault` mode will cause the first declared exception in a method signature to be thrown.

**CAVEAT**: Byte-Monkey can only create Exceptions that expose a public default constructor as a result of how it instantiates them. If such a constructor doesn't exist, it falls back to a `ByteMonkeyException` instead.

### Latency

Running byte-monkey in `latency` mode will cause the method to sleep before executing further instructions.

There is a configuration option available only during this mode:

 * `latency`: Duration (in millis) to wait on method calls, only valid when running in **Latency** mode. **Default is 100ms**

Example: `java -javaagent:byte-monkey.jar=mode:latency,rate:0.5,latency:150 -jar your-java-app.jar`

### Nullify

Running byte-monkey in `nullify` mode will replace the first non-primitive argument to the method call with a null value.

Methods with only primitive arguments or no arguments at all will not be affected by the agent in this mode.

## Implementation Details

Byte-Monkey uses the JVM [Instrumentation API](https://docs.oracle.com/javase/8/docs/api/java/lang/instrument/package-summary.html). Implementing the API enables you to register transformers that can iterate over (and transform) class files as they are loaded by the JVM. Byte-Monkey uses [Objectweb ASM](http://asm.ow2.org/) which comes packaged with the JDK to chance the underlying bytecode of loaded classes

### Injecting Failure

The bytecode of a simple "Hello, World!" method prior to having an exception injected looks like this:

```
  public void printSomething() throws java.io.IOException;
    Code:
       0: getstatic     #2                  // Field java/lang/System.out:Ljava/io/PrintStream;
       3: ldc           #3                  // String Hello!
       5: invokevirtual #4                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
       8: return
```

After being transformed by the Byte-Monkey, it instead looks like this:
```
  public void printSomething() throws java.io.IOException;
    Code:
       0: ldc2_w        #18                 // double 0.5d
       3: invokestatic  #25                 // Method uk/co/probablyfine/bytemonkey/AddChanceOfFailure.shouldActivate:(D)Z
       6: ifeq          15
       9: ldc           #26                 // String java/io/IOException
      11: invokestatic  #32                 // Method uk/co/probablyfine/bytemonkey/CreateAndThrowException.throwOrDefault:(Ljava/lang/String;)Ljava/lang/Throwable;
      14: athrow
      15: getstatic     #38                 // Field java/lang/System.out:Ljava/io/PrintStream;
      18: ldc           #40                 // String Hello!
      20: invokevirtual #46                 // Method java/io/PrintStream.println:(Ljava/lang/String;)V
      23: return
```

This is the core of how Byte-Monkey works:

 * **0:** Load the failure injection rate onto the stack
 * **3:** A call to `AddChanceOfFailure.shouldActivate` which returns true/false depending on the rate
 * **6:** If `shouldActivate` was false, we jump straight to instruction 15 - the beginning of the original code.
 * **9:** Load the name of the exception that would be thrown (here an IOException)
 * **11:** Create the exception if it has a default constructor, or create a wrapper exception
 * **14:** Throw the exception

For modes other than `fault`, instructions 9 to 14 are replaced with mode-specific instructions.
