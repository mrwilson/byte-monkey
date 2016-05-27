package uk.co.probablyfine.bytemonkey;

import com.ea.agentloader.AgentLoader;
import org.hamcrest.core.StringContains;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.co.probablyfine.bytemonkey.testfiles.TestObject;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.number.IsCloseTo.closeTo;

public class AgentTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldThrowExceptionWhenInstrumented_throwPercentageIs100() throws IOException {
        AgentLoader.loadAgentClass(ByteMonkeyAgent.class.getName(), "mode:fault,rate:1");

        expectedException.expect(ByteMonkeyException.class);
        expectedException.expectMessage(StringContains.containsString("java/io/IOException"));

        new TestObject().printSomething();
    }

    @Test
    public void shouldThrowNotExceptionWhenInstrumented_throwPercentageIs0() throws IOException {
        AgentLoader.loadAgentClass(ByteMonkeyAgent.class.getName(), "mode:fault,rate:0");

        new TestObject().printSomething();
    }

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

        assertThat(percentFailure, is(closeTo(percentFailure, 0.1)));
    }
}