# byte-monkey

[![Build Status](https://travis-ci.org/gluckzhang/byte-monkey.svg?branch=short-circuit-testing)](https://travis-ci.org/gluckzhang/byte-monkey)
[![Coverage Status](https://coveralls.io/repos/github/gluckzhang/byte-monkey/badge.svg?branch=short-circuit-testing)](https://coveralls.io/github/gluckzhang/byte-monkey?branch=short-circuit-testing)

Byte-Monkey is a small Java library for testing failure scenarios in JVM applications - it works by instrumenting application code on the fly to deliberately introduce faults like exceptions and latency. Original blogpost [here](http://blog.probablyfine.co.uk/2016/05/30/announcing-byte-monkey.html). And in this branch, we will add some features for byte-monkey: **throwing corresponding exceptions in the very beginning of try blocks**. It's some relevant work about short-circuit testing. What is short-circuit testing? What is this used for? You can read [this paper](https://hal.inria.fr/hal-01062969/document) or [my blog](http://blog.gluckzhang.com/archives/107/) first.

## Download

Original latest version: [1.0.0](https://github.com/mrwilson/byte-monkey/releases/download/1.0.0/byte-monkey.jar)

## How to use

```bash
java -javaagent:byte-monkey.jar -jar your-java-app.jar
```

## Supported Modes

 * **Fault**: Throw exceptions from methods that declare those exceptions
 * **Latency**: Introduce latency on method-calls
 * **Nullify**: Replace the first non-primitive argument to the method with *null*
 * **Short-circuit(working)**: Throw corresponding exceptions in the very beginning of try blocks

## Options

 * `mode`: What mode to run in - currently supports `fault`, `latency`, `nullify`, `scircuit`, and `analyzetc`. **Default is fault**
 * `rate`: Value between 0 and 1 - how often to activate the fault. **Default is 1, i.e. 100%**
 * `filter`: Only instrument packages or methods matching the (java-style) regex. **Default is .*, i.e. all methods**

byte-monkey is configured with a comma-separated key-value pair string of the options as the agent argument.

```bash
java -javaagent:byte-monkey.jar=mode:fault,rate:0.5,filter:uk/co/probablyfine/ -jar your-java-app.jar
```

The example above would run in fault mode, activating on 50% of eligible method calls, for anything in the package tree below `uk.co.probablyfine`

When you want to analyze the try-catch blocks info, you can also set byte-monkey into pom.xml, like this:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>2.19.1</version>
            <configuration>
                <!-- Java 7 introduced stricter verification and changed the class format a bit—to contain a stack map used to verify that code is correct. -->
                <!-- Java version or bytecode instrumentation could both be to blame. -->
                <!-- So we just close verify to make our short-circuit testing work -->
                <argLine>-noverify -javaagent:byte-monkey.jar=mode:analyzetc,filter:your/root/package/name</argLine>
            </configuration>
        </plugin>
    </plugins>
</build>
```

With this configuration, when you run `mvn test`, byte-monkey will detect all the try-catch blocks covered by test cases and inject a logging method into try block. So that you can get some logs to help your analysis. Logs are something like this:

```
----
INFO ByteMonkey try catch index 0 @ L1289479439, copyPropertiesWithExclude @ com.github.sworm.spojo.utils.SpojoUtils
INFO ByteMonkey testCase: copyInclude2ndLevelComplexProperty @ com.github.sworm.spojo.utils.SpojoUtilsMoreTest
----
```

`L1289479439` is a relative position of the try-catch block, because we analyze this from java byte-code, `index 0` indicates the number of this try block in the method. As for 1 try with n catch blocks, the index number will increase but the position stays the same. You can use [Spoon](http://spoon.gforge.inria.fr) together to do more analysis, like the percentage of try-catch covered by test cases. With this data, we might detect more bugs from those try-catch blocks which are not covered by test cases. We can also do the specific exception injection because we have obtained all the try-catch positions and exception types.

As for specific fault injection, you can use the following command:

```bash
java -javaagent:byte-monkey.jar=mode:scircuit,filter:package/path/ClassName/MethodName,tcindex=0 -jar your-java-app.jar
```

You can declare the exception type using `tcindex`, for example, the try block is corresponding to 2 catch blocks, then `tcindex 0` indicates the first type of exception in the catch block.

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

### Short-circuit

Throw corresponding exceptions in the very beginning of try blocks.
What is short-circuit testing? What is this used for? You can [read this paper][3] or [my article][4] first.

### Try-catch Analysis

Before we do short-circuit testing, we should analyze the application first, basically about how many try-catch blocks are covered by test cases. Then we can choose a specific one to inject exception and observe the results of all test cases.

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

### About the new mode to do short-circuit testing

Based on Instrumentation API and byte-monkey's codes, mainly update the `ByteMonkeyClassTransformer` to support new mode "scircuit", and add new code snippet into enum `OperationMode`.

- In `ByteMonkeyClassTransformer`: find the right position (beginning of try blocks) to inject codes

```java
switch (failureMode) {
    case SCIRCUIT:
        int tcIndex = arguments.tcIndex();
        if (tcIndex < 0) {
            cn.methods.stream()
                    .filter(method -> !method.name.startsWith("<"))
                    .filter(method -> filter.matches(cn.name, method.name))
                    .filter(method -> method.tryCatchBlocks.size() > 0)
                    .forEach(method -> {
                        // inject an exception in each try-catch block
                        // take the first exception type in catch block
                        // for 1 try -> n catch, we should do different injections through params
                        LabelNode ln = method.tryCatchBlocks.get(0).start;
                        int i = 0;
                        for (TryCatchBlockNode tc : method.tryCatchBlocks) {
                            if (ln == tc.start && i > 0) {
                                // if two try-catch-block-nodes have the same "start", it indicates that it's one try block with multiple catch
                                // so we should only inject one exception each time
                                continue;
                            }
                            InsnList newInstructions = failureMode.generateByteCode(tc, tcIndex, arguments);
                            method.maxStack += newInstructions.size();
                            method.instructions.insert(tc.start, newInstructions);
                            ln = tc.start;
                            i++;
                        }
                    });
        } else {
            // should work together with filter, inject an exception into specific position
            ...
        }
        break;
    default:
        ...
}
```

- In `OperationMode`: generate exception throwing method. `DirectlyThrowException` is a method who throws an exception, the type of the exception is defined in its parameter.

```java
public InsnList generateByteCode(TryCatchBlockNode tryCatchBlock, int tcIndex, AgentArguments arguments) {
    InsnList list = new InsnList();

    list.add(new LdcInsnNode(tryCatchBlock.type));
    list.add(new MethodInsnNode(
        Opcodes.INVOKESTATIC,
        "uk/co/probablyfine/bytemonkey/DirectlyThrowException",
        "throwDirectly",
        "(Ljava/lang/String;)V",
        false // this is not a method on an interface
    ));
    
    return list;
}
```

## Questions

- How to handle try-catch nesting? Do we need to define a injection level?

## TODO

- `√` Handle one try with multiple catch blocks

> In this case, the test case should be excuted for more times, every time different exception is injected into the try block.
> Byte-monkey can support more parameters in short-circuit mode now, besides filter, we can use `tcindex` to declare the specific type of exception you want to inject.

- `√` Integrate with Maven/JUnit/TestNG... to do automatic testing

> This is mainly used for try-catch analysis, but the most important goal for us is chaos engineering, i.e. injecting kinds of exceptions into production environment and learning from the system's reaction, to help build the confidence of system behavior.

- Define a injection rate in short-circuit mode