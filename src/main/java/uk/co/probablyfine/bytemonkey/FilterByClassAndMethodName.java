package uk.co.probablyfine.bytemonkey;

import java.util.regex.Pattern;

public class FilterByClassAndMethodName {

    private final Pattern pattern;

    public FilterByClassAndMethodName(String regex) {
        this.pattern = Pattern.compile(regex);
    }

    public boolean matches(String className, String methodName) {
        String fullName = className + "/" + methodName;

        return this.pattern.matcher(fullName).find();
    }
}
