package uk.co.probablyfine.bytemonkey;

import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.tree.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ByteMonkeyClassTransformer implements ClassFileTransformer {

    private final OperationMode failureMode;
    private static Double activationRatio = 1.0;
    private static Random random = new Random();
    private final Pattern filter;
    private final long latency;
    private final AddChanceOfFailure addChanceOfFailure = new AddChanceOfFailure();

    public ByteMonkeyClassTransformer(String args) {
        Map<String, String> configuration = Arrays
            .stream(args.split(","))
            .map(line -> line.split(":"))
            .filter(line -> line.length == 2)
            .collect(Collectors.toMap(
                keyValue -> keyValue[0],
                keyValue -> keyValue[1])
            );

        this.failureMode = OperationMode.fromLowerCase(configuration.getOrDefault("mode", OperationMode.FAULT.name()));
        this.filter = Pattern.compile(configuration.getOrDefault("filter", ".*"));
        this.latency = Long.valueOf(configuration.getOrDefault("latency","100"));
        activationRatio = Double.valueOf(configuration.getOrDefault("rate","1"));
    }

    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
        ProtectionDomain protectionDomain, byte[] classFileBuffer
    ) throws IllegalClassFormatException {
        return meddle(classFileBuffer);
    }

    private byte[] meddle(byte[] classFileBuffer) {
        ClassNode cn = new ClassNode();

        new ClassReader(classFileBuffer).accept(cn, 0);

        if (cn.name.startsWith("java/") || cn.name.startsWith("sun/") || cn.name.contains("$")) return classFileBuffer;

        cn.methods.stream()
            .filter(method -> !method.name.startsWith("<"))
            .filter(method -> matchesPackageAndMethodName(method.name, cn.name))
            .forEach(method -> {
                createNewInstructions(method).ifPresent(newInstructions -> {
                    method.maxStack += newInstructions.size();
                    method.instructions.insertBefore(
                        method.instructions.getFirst(),
                        newInstructions
                    );
                });
            });

        final ClassWriter cw = new ClassWriter(0);
        cn.accept(cw);
        return cw.toByteArray();
    }

    private boolean matchesPackageAndMethodName(String methodName, String className) {
        String fullName = className + "/" + methodName;

        return this.filter.matcher(fullName).find();
    }

    private Optional<InsnList> createNewInstructions(MethodNode method) {
        AgentArguments arguments = new AgentArguments(this.latency);

        return Optional.ofNullable(
            addChanceOfFailure.apply(failureMode.generateByteCode(method, arguments))
        );
    }

    public static boolean shouldActivate() {
        return random.nextDouble() < activationRatio;
    }
}