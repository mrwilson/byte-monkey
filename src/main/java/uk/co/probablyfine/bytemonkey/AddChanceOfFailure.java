package uk.co.probablyfine.bytemonkey;

import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.*;

public class AddChanceOfFailure {

    public InsnList apply(InsnList newInstructions) {
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
}
