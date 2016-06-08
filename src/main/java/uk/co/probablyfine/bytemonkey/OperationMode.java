package uk.co.probablyfine.bytemonkey;

import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.Type;
import jdk.internal.org.objectweb.asm.tree.*;

import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;

public enum OperationMode {
    LATENCY {
        @Override
        public InsnList generateByteCode(MethodNode method, AgentArguments arguments) {
            final InsnList list = new InsnList();

            list.add(new LdcInsnNode(arguments.latency()));
            list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Thread", "sleep", "(J)V", false));

            return list;
        }
    },
    FAULT {
        @Override
        public InsnList generateByteCode(MethodNode method, AgentArguments arguments) {
            final List<String> exceptionsThrown = method.exceptions;

            InsnList list = new InsnList();

            if (exceptionsThrown.size() == 0) return list;

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

            return list;
        }
    },
    NULLIFY {
        @Override
        public InsnList generateByteCode(MethodNode method, AgentArguments arguments) {
            final InsnList list = new InsnList();

            final Type[] argumentTypes = Type.getArgumentTypes(method.desc);

            OptionalInt first = IntStream
                .range(0, argumentTypes.length)
                .filter(i -> argumentTypes[i].getSort() == Type.OBJECT)
                .findFirst();

            if (!first.isPresent()) return list;

            list.add(new InsnNode(Opcodes.ACONST_NULL));
            list.add(new VarInsnNode(Opcodes.ASTORE, first.getAsInt() + 1));

            return list;
        }
    };

    public static OperationMode fromLowerCase(String mode) {
        return OperationMode.valueOf(mode.toUpperCase());
    }

    public abstract InsnList generateByteCode(MethodNode method, AgentArguments arguments);
}
