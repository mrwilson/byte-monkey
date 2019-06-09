package uk.co.probablyfine.bytemonkey;

import static java.util.Optional.ofNullable;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

public class ByteMonkeyClassTransformer implements ClassFileTransformer {

  private final AddChanceOfFailure addChanceOfFailure = new AddChanceOfFailure();

  private final OperationMode failureMode;
  private final AgentArguments arguments;
  private final FilterByClassAndMethodName filter;

  public ByteMonkeyClassTransformer(String args) {
    Map<String, String> configuration = argumentMap(args == null ? "" : args);

    long latency = Long.valueOf(configuration.getOrDefault("latency", "100"));
    double activationRatio = Double.valueOf(configuration.getOrDefault("rate", "1"));
    int tcIndex = Integer.valueOf(configuration.getOrDefault("tcindex", "-1"));

    this.arguments = new AgentArguments(latency, activationRatio, tcIndex);
    this.failureMode =
        OperationMode.fromLowerCase(configuration.getOrDefault("mode", OperationMode.FAULT.name()));
    this.filter = new FilterByClassAndMethodName(configuration.getOrDefault("filter", ".*"));
  }

  private Map<String, String> argumentMap(String args) {
    return Arrays.stream(args.split(","))
        .map(line -> line.split(":"))
        .filter(line -> line.length == 2)
        .collect(Collectors.toMap(keyValue -> keyValue[0], keyValue -> keyValue[1]));
  }

  public byte[] transform(
      ClassLoader loader,
      String className,
      Class<?> classBeingRedefined,
      ProtectionDomain protectionDomain,
      byte[] classFileBuffer)
      throws IllegalClassFormatException {
    return meddle(classFileBuffer);
  }

  private byte[] meddle(byte[] classFileBuffer) {
    ClassNode cn = new ClassNode();
    new ClassReader(classFileBuffer).accept(cn, 0);

    if (cn.name.startsWith("java/") || cn.name.startsWith("sun/") || cn.name.contains("$"))
      return classFileBuffer;

    switch (failureMode) {
      case SCIRCUIT:
        int tcIndex = arguments.tcIndex();
        if (tcIndex < 0) {
          cn.methods
              .stream()
              .filter(method -> !method.name.startsWith("<"))
              .filter(method -> filter.matches(cn.name, method.name))
              .filter(method -> method.tryCatchBlocks.size() > 0)
              .forEach(
                  method -> {
                    // inject an exception in each try-catch block
                    // take the first exception type in catch block
                    // for 1 try -> n catch, we should do different injections through params
                    // TODO: these codes really need to be beautified
                    LabelNode ln = method.tryCatchBlocks.get(0).start;
                    int i = 0;
                    for (TryCatchBlockNode tc : method.tryCatchBlocks) {
                      if (ln == tc.start && i > 0) {
                        // if two try-catch-block-nodes have the same "start", it indicates that
                        // it's one try block with multiple catch
                        // so we should only inject one exception each time
                        continue;
                      }
                      InsnList newInstructions =
                          failureMode.generateByteCode(tc, tcIndex, arguments);
                      method.maxStack += newInstructions.size();
                      method.instructions.insert(tc.start, newInstructions);
                      ln = tc.start;
                      i++;
                    }
                  });
        } else {
          // should work together with filter
          cn.methods
              .stream()
              .filter(method -> !method.name.startsWith("<"))
              .filter(method -> filter.matches(cn.name, method.name))
              .filter(method -> method.tryCatchBlocks.size() > 0)
              .forEach(
                  method -> {
                    int index = 0;
                    for (TryCatchBlockNode tc : method.tryCatchBlocks) {
                      if (index == tcIndex) {
                        InsnList newInstructions =
                            failureMode.generateByteCode(tc, tcIndex, arguments);
                        method.maxStack += newInstructions.size();
                        method.instructions.insert(tc.start, newInstructions);
                        break;
                      } else {
                        index++;
                      }
                    }
                  });
        }
        break;
      default:
        cn.methods
            .stream()
            .filter(method -> !method.name.startsWith("<"))
            .filter(method -> filter.matches(cn.name, method.name))
            .forEach(
                method -> {
                  createNewInstructions(method)
                      .ifPresent(
                          newInstructions -> {
                            method.maxStack += newInstructions.size();
                            method.instructions.insertBefore(
                                method.instructions.getFirst(), newInstructions);
                          });
                });
        break;
    }

    final ClassWriter cw = new ClassWriter(0);
    cn.accept(cw);
    return cw.toByteArray();
  }

  private Optional<InsnList> createNewInstructions(MethodNode method) {
    InsnList newInstructions = failureMode.generateByteCode(method, arguments);

    return ofNullable(addChanceOfFailure.apply(newInstructions, arguments.chanceOfFailure()));
  }
}
