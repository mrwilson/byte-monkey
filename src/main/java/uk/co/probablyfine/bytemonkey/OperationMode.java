package uk.co.probablyfine.bytemonkey;

enum OperationMode {
    LATENCY,
    FAULT;

    public static OperationMode fromLowerCase(String mode) {
        return OperationMode.valueOf(mode.toUpperCase());
    }
}
