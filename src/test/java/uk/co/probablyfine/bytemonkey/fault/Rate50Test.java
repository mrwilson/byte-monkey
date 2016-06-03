package uk.co.probablyfine.bytemonkey.fault;

import com.ea.agentloader.AgentLoader;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.co.probablyfine.bytemonkey.ByteMonkeyAgent;
import uk.co.probablyfine.bytemonkey.ByteMonkeyException;
import uk.co.probablyfine.bytemonkey.testfiles.TestObject;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.number.IsCloseTo.closeTo;

public class Rate50Test {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldThrowExceptionWhenInstrumented_throwPercentageIsRoughlyHalf() throws IOException {
        AgentLoader.loadAgentClass(ByteMonkeyAgent.class.getName(), "mode:fault,rate:0.5");

        final AtomicInteger counter = new AtomicInteger(0);

        IntStream.range(0, 10_000).forEach(x -> {
            try {
                new TestObject().printSomething();
            } catch (Exception e) {
                if (ByteMonkeyException.class.isAssignableFrom(e.getClass())) {
                    counter.incrementAndGet();
                }
            }
        });

        double percentFailure = counter.get()/10_000f;

        assertThat(percentFailure, is(closeTo(0.5f, 0.1)));
    }

}