package uk.co.probablyfine.bytemonkey;

import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.Type;
import jdk.internal.org.objectweb.asm.tree.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ByteMonkeyClassTransformer implements ClassFileTransformer {

    private final OperationMode mode;
    private static Double activationRatio = 1.0;
    private static Random random = new Random();
    private final Pattern filter;
    private final long latency;

    public ByteMonkeyClassTransformer(String args) {
        Map<String, String> configuration = Arrays
            .stream(args.split(","))
            .map(line -> line.split(":"))
            .filter(line -> line.length == 2)
            .collect(Collectors.toMap(
                keyValue -> keyValue[0],
                keyValue -> keyValue[1])
            );

        this.mode = OperationMode.fromLowerCase(configuration.getOrDefault("mode", OperationMode.FAULT.name()));
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
        switch (mode) {
            case LATENCY: return createLatency();
            case FAULT:   return throwException(method.exceptions);
            case NULLIFY: return nullifyParams(method);
            default:      return Optional.empty();
        }
    }

    private Optional<InsnList> nullifyParams(MethodNode method) {
        final InsnList list = new InsnList();

        final Type[] argumentTypes = Type.getArgumentTypes(method.desc);

        OptionalInt first = IntStream
            .range(0, argumentTypes.length)
            .filter(i -> argumentTypes[i].getSort() == Type.OBJECT)
            .findFirst();

        if (!first.isPresent()) return Optional.empty();

        list.add(new InsnNode(Opcodes.ACONST_NULL));
        list.add(new VarInsnNode(Opcodes.ASTORE, first.getAsInt() + 1));

        return Optional.of(addRandomChanceOfFailure(list));
    }

    private Optional<InsnList> createLatency() {
        final InsnList list = new InsnList();

        list.add(new LdcInsnNode(this.latency));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Thread", "sleep", "(J)V", false));

        return Optional.of(addRandomChanceOfFailure(list));
    }

    private Optional<InsnList> throwException(List<String> exceptionsThrown) {
        if (exceptionsThrown.size() == 0) return Optional.empty();

        InsnList list = new InsnList();

        list.add(new TypeInsnNode(Opcodes.NEW, ByteMonkeyException.typeName()));
        list.add(new InsnNode(Opcodes.DUP));
        list.add(new LdcInsnNode(exceptionsThrown.get(0)));

        list.add(new MethodInsnNode(
            Opcodes.INVOKESPECIAL,
            ByteMonkeyException.typeName(),
            "<init>",
            "(Ljava/lang/String;)V",
            false // this is not a method on an interface
        ));

        list.add(new InsnNode(Opcodes.ATHROW));

        return Optional.of(addRandomChanceOfFailure(list));
    }

    private InsnList addRandomChanceOfFailure(final InsnList newInstructions) {
        final InsnList list = new InsnList();

        final LabelNode originalCodeLabel = new LabelNode();

        list.add(new MethodInsnNode(
            Opcodes.INVOKESTATIC,
            "uk/co/probablyfine/bytemonkey/ByteMonkeyClassTransformer",
            "shouldActivate",
            "()Z",
            false // this is not a method on an interface
        ));

        list.add(new JumpInsnNode(Opcodes.IFEQ, originalCodeLabel));

        list.add(newInstructions);

        list.add(new FrameNode(
            Opcodes.F_APPEND,   // append to the last stack frame
            0, new Object[] {}, // no local variables here
            0, new Object[] {}  // no stack either!
        ));

        list.add(originalCodeLabel);

        return list;
    }

    public static boolean shouldActivate() {
        return random.nextDouble() < activationRatio;
    }
}