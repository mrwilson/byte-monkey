package uk.co.probablyfine.bytemonkey;

public class ByteMonkeyException extends RuntimeException {
    public ByteMonkeyException(String exceptionName) {
        super("You've made a monkey out of me! Simulating throw of ["+exceptionName+"]");
    }
}