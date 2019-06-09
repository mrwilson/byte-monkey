package uk.co.probablyfine.bytemonkey.fault;

import static uk.co.probablyfine.bytemonkey.TestUtils.installAgent;

import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.co.probablyfine.bytemonkey.testfiles.FaultTestObject;

public class Rate0Test {

  @Rule public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void shouldThrowNotExceptionWhenInstrumented_throwPercentageIs0() throws IOException {
    installAgent(
        "mode:fault,rate:0,filter:uk/co/probablyfine/bytemonkey/testfiles/FaultTestObject");

    new FaultTestObject().printSomething();
  }
}
