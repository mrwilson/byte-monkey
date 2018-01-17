package uk.co.probablyfine.bytemonkey.shortcircuit;

import com.ea.agentloader.AgentLoader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.co.probablyfine.bytemonkey.ByteMonkeyAgent;
import uk.co.probablyfine.bytemonkey.testfiles.TryCatchTestObject;

public class TryCatchObjectSCTest {
	
    @Before
    public void loadAgent() {
        AgentLoader.loadAgentClass(ByteMonkeyAgent.class.getName(), "mode:scircuit");
    }

    @Test
    public void scMultipleTryCatchTest() {
        // this time, we do short-circuit testing, exceptions will be injected into the beginning of every try block
        // hence "_1st line in xxx tc" should not appear in the return value
        TryCatchTestObject tcTest = new TryCatchTestObject();
        Assert.assertEquals(tcTest.multipleTryCatch(), "_mpe in 1st tc_mpe in 2nd tc");
    }

    public static void main(String[] args) {
    	AgentLoader.loadAgentClass(ByteMonkeyAgent.class.getName(), "mode:scircuit");

        TryCatchTestObject tcTest = new TryCatchTestObject();
        System.out.println(tcTest.multipleTryCatch());
    }
}
