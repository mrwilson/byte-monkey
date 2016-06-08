package uk.co.probablyfine.bytemonkey;

public class AgentArguments {
    private final long latency;

    public AgentArguments(long latency) {
        this.latency = latency;
    }

    public long latency() {
        return latency;
    }
}
