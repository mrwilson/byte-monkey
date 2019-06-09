package uk.co.probablyfine.bytemonkey.nullify;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static uk.co.probablyfine.bytemonkey.TestUtils.installAgent;

import java.io.IOException;
import org.junit.Test;
import uk.co.probablyfine.bytemonkey.testfiles.NullabilityTestPojo;

public class DoNothingWithNoObjectParamMethodsTest {

  @Test
  public void shouldOnlyNullifyObjects() throws IOException {
    installAgent(
        "mode:nullify,rate:1,filter:uk/co/probablyfine/bytemonkey/testfiles/NullabilityTestPojo/setNamePrimitiveArgs");

    NullabilityTestPojo pojo = new NullabilityTestPojo("foo");

    pojo.setNamePrimitiveArgs(1, 2);

    assertThat(pojo.getName(), is("zoom"));
  }
}
