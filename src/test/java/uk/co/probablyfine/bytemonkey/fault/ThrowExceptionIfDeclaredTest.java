package uk.co.probablyfine.bytemonkey.fault;

import static uk.co.probablyfine.bytemonkey.TestUtils.installAgent;

import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.co.probablyfine.bytemonkey.testfiles.FaultTestObject;

public class ThrowExceptionIfDeclaredTest {

  @Rule public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void shouldThrowCorrectException() throws IOException {
    installAgent(
        "mode:fault,filter:uk/co/probablyfine/bytemonkey/testfiles/FaultTestObject/printSomethingElse");

    expectedException.expect(IllegalStateException.class);

    new FaultTestObject().printSomething();
    new FaultTestObject().printSomethingElse();
  }
}
