package uk.co.probablyfine.bytemonkey;

import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.Type;
import jdk.internal.org.objectweb.asm.tree.*;

import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;

public enum OperationMode {
	SCIRCUIT {
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
        
		@Override
		public InsnList generateByteCode(MethodNode method, AgentArguments arguments) {
			// won't use this method
			return null;
		}
	},
    ANALYZETC {
        public InsnList generateByteCode(TryCatchBlockNode tryCatchBlock, int tcIndex, AgentArguments arguments) {
            InsnList list = new InsnList();

            list.add(new LdcInsnNode(tcIndex + " @ " + tryCatchBlock.start.getLabel().toString()));
            list.add(new MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "uk/co/probablyfine/bytemonkey/LogTryCatchInfo",
                    "printInfo",
                    "(Ljava/lang/String;)V",
                    false // this is not a method on an interface
            ));

            return list;
        }

        @Override
        public InsnList generateByteCode(MethodNode method, AgentArguments arguments) {
            // won't use this method
            return null;
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
        @Override
        public InsnList generateByteCode(TryCatchBlockNode tryCatchBlock, int tcIndex, AgentArguments arguments) {
        	// won't use this method
        	return null;
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
        @Override
        public InsnList generateByteCode(TryCatchBlockNode tryCatchBlock, int tcIndex, AgentArguments arguments) {
        	// won't use this method
        	return null;
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
        @Override
        public InsnList generateByteCode(TryCatchBlockNode tryCatchBlock, int tcIndex, AgentArguments arguments) {
        	// won't use this method
        	return null;
        }
    };

    public static OperationMode fromLowerCase(String mode) {
        return OperationMode.valueOf(mode.toUpperCase());
    }

    public abstract InsnList generateByteCode(MethodNode method, AgentArguments arguments);
    public abstract InsnList generateByteCode(TryCatchBlockNode tryCatchBlock, int tcIndex, AgentArguments arguments);
}
