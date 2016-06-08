package uk.co.probablyfine.bytemonkey;

public class CreateAndThrowException {

    public static Throwable throwOrDefault(String name) {
        String dotSeparatedClassName = name.replace("/", ".");

        try {
            Class<?> p = Class.forName(dotSeparatedClassName, false, ClassLoader.getSystemClassLoader());

            if (Throwable.class.isAssignableFrom(p)) {
                return (Throwable) p.newInstance();
            } else {
                return new ByteMonkeyException(name);
            }
        } catch (IllegalAccessException e) {
            return new ByteMonkeyException(name);
        } catch (Exception e) {
            return new RuntimeException(name);
        }
    }
}
