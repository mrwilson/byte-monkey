package uk.co.probablyfine.bytemonkey;

public class AgentArguments {
  private final long latency;
  private final double chanceOfFailure;
  private final int tcIndex;

  public AgentArguments(long latency, double activationRatio, int tcIndex) {
    this.latency = latency;
    this.chanceOfFailure = activationRatio;
    this.tcIndex = tcIndex;
  }

  public long latency() {
    return latency;
  }

  public double chanceOfFailure() {
    return chanceOfFailure;
  }

  public int tcIndex() {
    return tcIndex;
  }
}
