package uk.co.probablyfine.bytemonkey.fault;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.co.probablyfine.bytemonkey.testfiles.FaultTestObject;

import java.io.IOException;

import static uk.co.probablyfine.bytemonkey.TestUtils.installAgent;

public class Rate100Test {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldThrowExceptionWhenInstrumented_throwPercentageIs100() throws IOException {
        installAgent("mode:fault,rate:1,filter:uk/co/probablyfine/bytemonkey/testfiles/FaultTestObject");

        expectedException.expect(IOException.class);

        new FaultTestObject().printSomething();
    }

}