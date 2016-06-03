package uk.co.probablyfine.bytemonkey.nullify;

import com.ea.agentloader.AgentLoader;
import org.junit.Test;
import uk.co.probablyfine.bytemonkey.ByteMonkeyAgent;
import uk.co.probablyfine.bytemonkey.testfiles.NullabilityTestPojo;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DoNothingWithEmptyParamMethodsTest {

    @Test
    public void shouldOnlyNullifyObjects() throws IOException {
        AgentLoader.loadAgentClass(
            ByteMonkeyAgent.class.getName(),
            "mode:nullify,rate:1,filter:uk/co/probablyfine/bytemonkey/testfiles/NullabilityTestPojo/setNameNoArgs"
        );

        NullabilityTestPojo pojo = new NullabilityTestPojo("foo");

        pojo.setNameNoArgs();

        assertThat(pojo.getName(), is("zap"));
    }
}