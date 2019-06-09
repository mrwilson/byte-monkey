package uk.co.probablyfine.bytemonkey.nullify;

import static org.junit.Assert.assertNull;
import static uk.co.probablyfine.bytemonkey.TestUtils.installAgent;

import java.io.IOException;
import org.junit.Test;
import uk.co.probablyfine.bytemonkey.testfiles.NullabilityTestPojo;

public class NullifyArgumentsTest {

  @Test
  public void shouldNullifyArguments() throws IOException {
    installAgent(
        "mode:nullify,rate:1,filter:uk/co/probablyfine/bytemonkey/testfiles/NullabilityTestPojo/setName1stArg");

    NullabilityTestPojo pojo = new NullabilityTestPojo("foo");

    pojo.setName1stArg("bar");

    assertNull(pojo.getName());
  }
}
