package uk.co.probablyfine.bytemonkey;

public class DirectlyThrowException {
    public static void throwDirectly(String name) throws Throwable {
        String dotSeparatedClassName = name.replace("/", ".");
        Class<?> p = Class.forName(dotSeparatedClassName, false, ClassLoader.getSystemClassLoader());
        if (Throwable.class.isAssignableFrom(p)) {
    	  	throw (Throwable) p.newInstance();
      	} else {
      		throw new ByteMonkeyException(name);
      	}
    }
}
