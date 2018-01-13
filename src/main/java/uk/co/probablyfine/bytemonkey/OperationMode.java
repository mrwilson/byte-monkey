package uk.co.probablyfine.bytemonkey;

import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;

import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.Type;
import jdk.internal.org.objectweb.asm.tree.InsnList;
import jdk.internal.org.objectweb.asm.tree.InsnNode;
import jdk.internal.org.objectweb.asm.tree.LdcInsnNode;
import jdk.internal.org.objectweb.asm.tree.MethodInsnNode;
import jdk.internal.org.objectweb.asm.tree.MethodNode;
import jdk.internal.org.objectweb.asm.tree.TryCatchBlockNode;
import jdk.internal.org.objectweb.asm.tree.TypeInsnNode;
import jdk.internal.org.objectweb.asm.tree.VarInsnNode;

public enum OperationMode {
	SCIRCUIT {
		@Override
        public InsnList generateByteCode(MethodNode method, AgentArguments arguments) {
			final List<TryCatchBlockNode> tryCatchBlocks = method.tryCatchBlocks;
			InsnList list = new InsnList();

            if (tryCatchBlocks.size() == 0) return list;
            
//            list.add(new LdcInsnNode(3000L));
//            list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Thread", "sleep", "(J)V", false));
//            
//            list.add(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
//            list.add(new LdcInsnNode("i know why now"));
//            list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false));
            
            list.add(new LdcInsnNode(tryCatchBlocks.get(0).type));
            list.add(new MethodInsnNode(
                Opcodes.INVOKESTATIC,
                "uk/co/probablyfine/bytemonkey/CreateAndThrowException",
                "throwDirectly",
                "(Ljava/lang/String;)V",
                false // this is not a method on an interface
            ));
//            list.add(new TypeInsnNode(Opcodes.NEW, "uk/co/probablyfine/bytemonkey/testfiles/MissingPropertyException"));
//            list.add(new InsnNode(Opcodes.DUP));
//	        list.add(new MethodInsnNode(
//	        	  Opcodes.INVOKESPECIAL,
//				  "uk/co/probablyfine/bytemonkey/testfiles/MissingPropertyException",
//				  "<init>",
//				  "()V",
//				  false // this is not a method on an interface
//	        ));
//            list.add(new InsnNode(Opcodes.ATHROW));
            
            return list;
        }
	},
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

            list.add(new LdcInsnNode(exceptionsThrown.get(0)));
            list.add(new MethodInsnNode(
                Opcodes.INVOKESTATIC,
                "uk/co/probablyfine/bytemonkey/CreateAndThrowException",
                "throwOrDefault",
                "(Ljava/lang/String;)Ljava/lang/Throwable;",
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

            final OptionalInt firstNonPrimitiveArgument = IntStream
                .range(0, argumentTypes.length)
                .filter(i -> argumentTypes[i].getSort() == Type.OBJECT)
                .findFirst();

            if (!firstNonPrimitiveArgument.isPresent()) return list;

            list.add(new InsnNode(Opcodes.ACONST_NULL));
            list.add(new VarInsnNode(Opcodes.ASTORE, firstNonPrimitiveArgument.getAsInt() + 1));

            return list;
        }
    };

    public static OperationMode fromLowerCase(String mode) {
        return OperationMode.valueOf(mode.toUpperCase());
    }

    public abstract InsnList generateByteCode(MethodNode method, AgentArguments arguments);
}
