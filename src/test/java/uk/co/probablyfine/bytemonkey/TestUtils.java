package uk.co.probablyfine.bytemonkey;

import org.avaje.agentloader.AgentLoader;

public class TestUtils {

  public static void installAgent(String arguments) {
    AgentLoader.loadAgent("target/byte-monkey.jar", arguments);
  }
}
