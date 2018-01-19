package uk.co.probablyfine.bytemonkey.shortcircuit;

import com.ea.agentloader.AgentLoader;
import uk.co.probablyfine.bytemonkey.ByteMonkeyAgent;
import uk.co.probablyfine.bytemonkey.testfiles.TryCatchTestObject;

public class AnalyzeTCModelTest {
    public static void main(String[] args) {
        AgentLoader.loadAgentClass(ByteMonkeyAgent.class.getName(), "mode:analyzetc");

        TryCatchTestObject tcTest = new TryCatchTestObject();
        System.out.println(tcTest.multipleTryCatch());
    }
}