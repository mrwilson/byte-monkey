package uk.co.probablyfine.bytemonkey;

enum OperationMode {
    LATENCY,
    FAULT,
    NULLIFY;

    public static OperationMode fromLowerCase(String mode) {
        return OperationMode.valueOf(mode.toUpperCase());
    }
}
