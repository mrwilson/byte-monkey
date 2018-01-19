package uk.co.probablyfine.bytemonkey;

public class LogTryCatchInfo {
    public static void printInfo(String tcPosition) {
        int stackLengh = Thread.currentThread().getStackTrace().length;
        String executedClassName = Thread.currentThread().getStackTrace()[2].getClassName();
        String executedMethodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        String testClassName = Thread.currentThread().getStackTrace()[stackLengh - 1].getClassName();
        String testMethodName = Thread.currentThread().getStackTrace()[stackLengh - 1].getMethodName();

        // TryCatch Info
        System.out.println(String.format("INFO ByteMonkey try catch @ %s, %s, %s", tcPosition, executedMethodName, executedClassName));
        // Testcase Info
        System.out.println(String.format("INFO ByteMonkey testCase: %s @ %s", testMethodName, testClassName));
        System.out.println("----");
    }
}