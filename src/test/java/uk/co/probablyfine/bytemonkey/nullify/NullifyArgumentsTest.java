package uk.co.probablyfine.bytemonkey.nullify;

import com.ea.agentloader.AgentLoader;
import org.junit.Test;
import uk.co.probablyfine.bytemonkey.ByteMonkeyAgent;
import uk.co.probablyfine.bytemonkey.testfiles.NullabilityTestPojo;

import java.io.IOException;

import static org.junit.Assert.assertNull;

public class NullifyArgumentsTest {

    @Test
    public void shouldNullifyArguments() throws IOException {
        AgentLoader.loadAgentClass(
            ByteMonkeyAgent.class.getName(),
            "mode:nullify,rate:1,filter:uk/co/probablyfine/bytemonkey/testfiles/NullabilityTestPojo/setName1stArg"
        );

        NullabilityTestPojo pojo = new NullabilityTestPojo("foo");

        pojo.setName1stArg("bar");

        assertNull(pojo.getName());

    }

}