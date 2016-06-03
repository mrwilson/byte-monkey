package uk.co.probablyfine.bytemonkey.nullify;

import com.ea.agentloader.AgentLoader;
import org.junit.Test;
import uk.co.probablyfine.bytemonkey.ByteMonkeyAgent;
import uk.co.probablyfine.bytemonkey.testfiles.NullabilityTestPojo;

import java.io.IOException;

import static org.junit.Assert.assertNull;

public class OnlyNullifyNonPrimitiveArgumentsTest {

    @Test
    public void shouldOnlyNullifyObjects() throws IOException {
        AgentLoader.loadAgentClass(
            ByteMonkeyAgent.class.getName(),
            "mode:nullify,rate:1,filter:uk/co/probablyfine/bytemonkey/testfiles/NullabilityTestPojo/setName2ndArg"
        );

        NullabilityTestPojo pojo = new NullabilityTestPojo("foo");

        pojo.setName2ndArg(1, "bar");

        assertNull(pojo.getName());
    }

}