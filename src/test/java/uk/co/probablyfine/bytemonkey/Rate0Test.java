package uk.co.probablyfine.bytemonkey;

import com.ea.agentloader.AgentLoader;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.co.probablyfine.bytemonkey.testfiles.TestObject;

import java.io.IOException;

public class Rate0Test {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldThrowNotExceptionWhenInstrumented_throwPercentageIs0() throws IOException {
        AgentLoader.loadAgentClass(ByteMonkeyAgent.class.getName(), "mode:fault,rate:0");

        new TestObject().printSomething();
    }

}