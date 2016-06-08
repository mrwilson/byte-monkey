package uk.co.probablyfine.bytemonkey.fault;

import com.ea.agentloader.AgentLoader;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.co.probablyfine.bytemonkey.ByteMonkeyAgent;
import uk.co.probablyfine.bytemonkey.testfiles.FaultTestObject;

import java.io.IOException;

public class ThrowExceptionIfDeclaredTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldThrowCorrectException() throws IOException {
        AgentLoader.loadAgentClass(
            ByteMonkeyAgent.class.getName(),
            "mode:fault,filter:uk/co/probablyfine/bytemonkey/testfiles/FaultTestObject/printSomethingElse"
        );

        expectedException.expect(IllegalStateException.class);

        new FaultTestObject().printSomething();
        new FaultTestObject().printSomethingElse();
    }

}