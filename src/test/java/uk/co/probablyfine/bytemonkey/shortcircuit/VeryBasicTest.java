package uk.co.probablyfine.bytemonkey.shortcircuit;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.ea.agentloader.AgentLoader;

import uk.co.probablyfine.bytemonkey.ByteMonkeyAgent;
import uk.co.probablyfine.bytemonkey.testfiles.TryCatchTestObject;

public class VeryBasicTest {
	
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void veryBasicTestAboutShortcircuit() throws IOException {
        AgentLoader.loadAgentClass(ByteMonkeyAgent.class.getName(), "mode:scircuit");

        TryCatchTestObject tcTest = new TryCatchTestObject();
        tcTest.sourceIndependentTryCatch();
        tcTest.sourceDependentTryCatch();
        tcTest.purelyResilientTryCatch();
    }
}
