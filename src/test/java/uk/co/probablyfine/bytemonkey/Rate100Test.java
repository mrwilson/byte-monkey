package uk.co.probablyfine.bytemonkey;

import com.ea.agentloader.AgentLoader;
import org.hamcrest.core.StringContains;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.co.probablyfine.bytemonkey.testfiles.TestObject;

import java.io.IOException;

public class Rate100Test {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldThrowExceptionWhenInstrumented_throwPercentageIs100() throws IOException {
        AgentLoader.loadAgentClass(ByteMonkeyAgent.class.getName(), "mode:fault,rate:1");

        expectedException.expect(ByteMonkeyException.class);
        expectedException.expectMessage(StringContains.containsString("java/io/IOException"));

        new TestObject().printSomething();
    }

}