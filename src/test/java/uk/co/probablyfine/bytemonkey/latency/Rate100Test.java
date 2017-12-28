package uk.co.probablyfine.bytemonkey.latency;

import com.ea.agentloader.AgentLoader;
import org.junit.Test;
import uk.co.probablyfine.bytemonkey.ByteMonkeyAgent;
import uk.co.probablyfine.bytemonkey.testfiles.FaultTestObject;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class Rate100Test {

    @Test
    public void shouldAddLatency() throws IOException {
        AgentLoader.loadAgentClass(
            ByteMonkeyAgent.class.getName(),
            "mode:latency,rate:1,latency:200,filter:uk/co/probablyfine/bytemonkey/testfiles"
        );

        long timeTaken = timed(new FaultTestObject()::safePrint);

        assertTrue("Actually took "+timeTaken+"ms", timeTaken >= 200 && timeTaken < 500);
    }

    public static long timed(Runnable runnable) {
        long startTime = System.currentTimeMillis();

        runnable.run();

        return System.currentTimeMillis() - startTime;
    }

}