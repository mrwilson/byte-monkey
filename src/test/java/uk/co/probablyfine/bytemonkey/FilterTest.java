package uk.co.probablyfine.bytemonkey;

import com.ea.agentloader.AgentLoader;
import org.hamcrest.core.StringContains;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.co.probablyfine.bytemonkey.testfiles.TestObject;

import java.io.IOException;

public class FilterTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldOnlyInstrumentMethodsDeclaredInFilter() throws IOException {
        AgentLoader.loadAgentClass(
            ByteMonkeyAgent.class.getName(),
            "mode:fault,filter:uk/co/probablyfine/bytemonkey/testfiles/TestObject/printSomethingElse"
        );

        expectedException.expect(ByteMonkeyException.class);
        expectedException.expectMessage(StringContains.containsString("java/lang/IllegalStateException"));

        new TestObject().printSomething();
        new TestObject().printSomethingElse();
    }

}