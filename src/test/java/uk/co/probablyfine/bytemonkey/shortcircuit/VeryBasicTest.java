package uk.co.probablyfine.bytemonkey.shortcircuit;

import java.io.IOException;

import com.ea.agentloader.AgentLoader;

import uk.co.probablyfine.bytemonkey.ByteMonkeyAgent;
import uk.co.probablyfine.bytemonkey.testfiles.TryCatchTestObject;

public class VeryBasicTest {
	public static void main(String[] args) throws IOException {
        AgentLoader.loadAgentClass(ByteMonkeyAgent.class.getName(), "mode:scircuit");

        TryCatchTestObject tcTest = new TryCatchTestObject();
        tcTest.sourceIndependentTryCatch();
        tcTest.sourceDependentTryCatch();
        tcTest.purelyResilientTryCatch();
	}
}
