package uk.co.probablyfine.bytemonkey;

public class CreateAndThrowException {

  public static void throwDirectly(String name) throws Throwable {
    String dotSeparatedClassName = name.replace("/", ".");
    Class<?> p = Class.forName(dotSeparatedClassName, false, ClassLoader.getSystemClassLoader());
    if (Throwable.class.isAssignableFrom(p)) {
      throw (Throwable) p.newInstance();
    } else {
      throw new ByteMonkeyException(name);
    }
  }

  public static Throwable throwOrDefault(String name) {
    String dotSeparatedClassName = name.replace("/", ".");

    try {
      Class<?> p = Class.forName(dotSeparatedClassName, false, ClassLoader.getSystemClassLoader());

      if (Throwable.class.isAssignableFrom(p)) {
        return (Throwable) p.getDeclaredConstructor().newInstance();
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
