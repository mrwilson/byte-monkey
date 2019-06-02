package uk.co.probablyfine.bytemonkey.latency;

import org.junit.Test;
import uk.co.probablyfine.bytemonkey.testfiles.FaultTestObject;

import java.io.IOException;
import java.lang.instrument.UnmodifiableClassException;

import static org.junit.Assert.assertTrue;
import static uk.co.probablyfine.bytemonkey.TestUtils.installAgent;

public class Rate100Test {

    @Test
    public void shouldAddLatency() throws IOException, UnmodifiableClassException {
        installAgent("mode:latency,rate:1,latency:200,filter:uk/co/probablyfine/bytemonkey/testfiles");

        long timeTaken = timed(new FaultTestObject()::safePrint);

        assertTrue("Actually took "+timeTaken+"ms", timeTaken >= 200 && timeTaken < 500);
    }

    public static long timed(Runnable runnable) {
        long startTime = System.currentTimeMillis();

        runnable.run();

        return System.currentTimeMillis() - startTime;
    }

}