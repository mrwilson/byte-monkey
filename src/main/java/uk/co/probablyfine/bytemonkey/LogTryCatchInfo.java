package uk.co.probablyfine.bytemonkey;

public class LogTryCatchInfo {
    public static void printInfo(String tcPosition) {
        int stackLengh = Thread.currentThread().getStackTrace().length;
        String executedClassName = Thread.currentThread().getStackTrace()[2].getClassName();
        String executedMethodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        String testClassInfo[] = getTestClassStackIndex();
        String testClassName = testClassInfo[0];
        String testMethodName = testClassInfo[1];

        // TryCatch Info
        System.out.println(String.format("INFO ByteMonkey try catch @ %s, %s, %s", tcPosition, executedMethodName, executedClassName));
        // Testcase Info
        System.out.println(String.format("INFO ByteMonkey testCase: %s @ %s", testMethodName, testClassName));
        System.out.println("----");
    }

    public static String[] getTestClassStackIndex() {
        String result[] = {"NOT TEST CLASS", "KNOWN METHOD"};
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int stackLength = stackTrace.length;
        String className = null;

        for (int i = stackLength - 1; i > 0; i--) {
            className = stackTrace[i].getClassName();
            String slices[] = className.split("/");
            className = slices[slices.length - 1];
            if (className.contains("Test")) {
                result[0] = className;
                result[1] = stackTrace[i].getMethodName();
                break;
            }
        }

        return result;
    }
}