package uk.co.probablyfine.bytemonkey;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

public class ByteMonkeyAgent {

  public static void premain(String agentArguments, Instrumentation instrumentation)
      throws UnmodifiableClassException {
    ByteMonkeyClassTransformer transformer = new ByteMonkeyClassTransformer(agentArguments);
    instrumentation.addTransformer(transformer);
  }

  /* Duplicate of premain(), needed for ea-agent-loader in tests */
  public static void agentmain(String agentArguments, Instrumentation instrumentation)
      throws UnmodifiableClassException {
    premain(agentArguments, instrumentation);
  }
}
