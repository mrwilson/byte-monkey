package uk.co.probablyfine.bytemonkey;

import java.util.Random;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class AddChanceOfFailure {

  private static final Random random = new Random();

  public InsnList apply(InsnList newInstructions, double chanceOfFailure) {
    final InsnList list = new InsnList();

    final LabelNode originalCodeLabel = new LabelNode();

    list.add(new LdcInsnNode(chanceOfFailure));
    list.add(
        new MethodInsnNode(
            Opcodes.INVOKESTATIC,
            "uk/co/probablyfine/bytemonkey/AddChanceOfFailure",
            "shouldActivate",
            "(D)Z",
            false // this is not a method on an interface
            ));

    list.add(new JumpInsnNode(Opcodes.IFEQ, originalCodeLabel));

    list.add(newInstructions);

    list.add(
        new FrameNode(
            Opcodes.F_APPEND, // append to the last stack frame
            0,
            new Object[] {}, // no local variables here
            0,
            new Object[] {} // no stack either!
            ));

    list.add(originalCodeLabel);

    return list;
  }

  public static boolean shouldActivate(double chanceOfFailure) {
    return random.nextDouble() < chanceOfFailure;
  }
}
