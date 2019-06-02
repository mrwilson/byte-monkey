package uk.co.probablyfine.bytemonkey.shortcircuit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.co.probablyfine.bytemonkey.testfiles.TryCatchTestObject;

import static uk.co.probablyfine.bytemonkey.TestUtils.installAgent;

public class TryCatchObjectSCWithParamTest {
    @Before
    public void loadAgent() {
        installAgent("mode:scircuit,tcindex:2,filter:uk/co/probablyfine/bytemonkey/testfiles/TryCatchTestObject/multipleTryCatch");
    }

    @Test
    public void scMultipleTryCatchWithParamTest() {
        TryCatchTestObject tcTest = new TryCatchTestObject();
        Assert.assertEquals("_1st line in 1st tc_mpe in 2nd tc", tcTest.multipleTryCatch());
    }
}