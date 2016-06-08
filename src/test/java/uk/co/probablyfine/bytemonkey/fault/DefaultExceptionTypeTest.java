package uk.co.probablyfine.bytemonkey.fault;

import com.ea.agentloader.AgentLoader;
import org.hamcrest.core.StringContains;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.co.probablyfine.bytemonkey.ByteMonkeyAgent;
import uk.co.probablyfine.bytemonkey.ByteMonkeyException;
import uk.co.probablyfine.bytemonkey.testfiles.FaultTestObject;

import java.io.IOException;

import static org.hamcrest.core.StringContains.containsString;

public class DefaultExceptionTypeTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldThrowSomethingIDK() throws IOException {
        AgentLoader.loadAgentClass(
            ByteMonkeyAgent.class.getName(),
            "mode:fault,filter:uk/co/probablyfine/bytemonkey/testfiles/FaultTestObject/printAndThrowNonPublicException"
        );

        expectedException.expect(ByteMonkeyException.class);
        expectedException.expectMessage(containsString("FaultTestObject$ExceptionWithNoPublicConstructor"));

        new FaultTestObject().printAndThrowNonPublicException();
    }
}
