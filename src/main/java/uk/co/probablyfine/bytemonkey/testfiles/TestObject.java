package uk.co.probablyfine.bytemonkey.testfiles;

import java.io.IOException;

public class TestObject {
    public void printSomething() throws IOException {
        System.out.println("Hello!");
    }

    public void printSomethingElse() throws IllegalStateException {
        System.out.println("Goodbye!");
    }

    public void safePrint() {
        System.out.println("Hi!");
    }
}