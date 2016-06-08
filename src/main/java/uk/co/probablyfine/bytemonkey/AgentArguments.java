package uk.co.probablyfine.bytemonkey;

public class AgentArguments {
    private final long latency;
    private final double chanceOfFailure;

    public AgentArguments(long latency, double activationRatio) {
        this.latency = latency;
        this.chanceOfFailure = activationRatio;
    }

    public long latency() {
        return latency;
    }

    public double chanceOfFailure() {
        return chanceOfFailure;
    }
}
