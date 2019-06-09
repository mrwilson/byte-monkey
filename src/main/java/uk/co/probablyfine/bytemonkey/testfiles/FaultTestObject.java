package uk.co.probablyfine.bytemonkey.testfiles;

import java.io.IOException;

public class FaultTestObject {
  public void printSomething() throws IOException {
    System.out.println("Hello!");
  }

  public void printSomethingElse() throws IllegalStateException {
    System.out.println("Goodbye!");
  }

  public void printAndThrowNonPublicException() throws ExceptionWithNoPublicConstructor {
    System.out.println("Uh-oh!");
  }

  public void safePrint() {
    System.out.println("Hi!");
  }

  public static class ExceptionWithNoPublicConstructor extends RuntimeException {
    private ExceptionWithNoPublicConstructor() {} // No constructor for you!
  }
}
