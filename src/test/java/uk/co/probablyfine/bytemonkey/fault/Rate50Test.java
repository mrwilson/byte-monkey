package uk.co.probablyfine.bytemonkey.fault;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.co.probablyfine.bytemonkey.testfiles.FaultTestObject;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static uk.co.probablyfine.bytemonkey.TestUtils.installAgent;

public class Rate50Test {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldThrowExceptionWhenInstrumented_throwPercentageIsRoughlyHalf() throws IOException {
        installAgent("mode:fault,rate:0.5,filter:uk/co/probablyfine/bytemonkey/testfiles/FaultTestObject");

        final AtomicInteger counter = new AtomicInteger(0);

        IntStream.range(0, 10_000).forEach(x -> {
            try {
                new FaultTestObject().printSomething();
            } catch (Exception e) {
                if (IOException.class.isAssignableFrom(e.getClass())) {
                    counter.incrementAndGet();
                }
            }
        });

        double percentFailure = counter.get()/10_000f;

        assertThat(percentFailure, is(closeTo(0.5f, 0.1)));
    }

}