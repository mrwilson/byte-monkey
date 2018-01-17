package uk.co.probablyfine.bytemonkey.testfiles;

import java.io.IOException;
import java.util.Random;

public class TryCatchTestObject {
	public void multipleTryCatch() {
		System.out.println("in try right away!!");
		try {
			System.out.println("first line in try!!");
			String arg = getArgument();
			String key = format(arg);
		} catch (MissingPropertyException e) {
			System.out.println("MissingPropertyException occured in sourceIndependentTryCatch");
		} catch (IOException e) {
			System.out.println("IOException occured in sourceIndependentTryCatch");
		}
		
		try {
			System.out.println("first line in 2nd try!!");
			String arg = getArgument();
			String key = format(arg);
		} catch (MissingPropertyException e) {
			System.out.println("MissingPropertyException occured in sourceIndependentTryCatch");
		} catch (IOException e) {
			System.out.println("IOException occured in sourceIndependentTryCatch");
		}
	}
	
	public String sourceIndependentTryCatch() {
		Boolean isCacheActivated = false;
		System.out.println("in try right away!!");
		try {
			System.out.println("first line in try!!");
			String arg = getArgument();
			String key = format(arg);
			return getProperty(key, isCacheActivated);
		} catch (MissingPropertyException e) {
			System.out.println("MissingPropertyException occured in sourceIndependentTryCatch");
			return "missing property";
		} catch (IOException e) {
			System.out.println("IOException occured in sourceIndependentTryCatch");
			return "get argument failed";
		}
	}
	
	public String sourceDependentTryCatch() {
		Boolean isCacheActivated = true;
		String arg = "str_arg";
		String key = "str_key";
		try {
			isCacheActivated = getCacheAvailability();
			return getProperty(key, isCacheActivated);
		} catch (MissingPropertyException e) {
			System.out.println("MissingPropertyException occured in sourceDependentTryCatch");
			if (isCacheActivated) {
				return "missing property";
			} else {
				throw new CacheDisableException();
			}
		}
	}
	
	public String purelyResilientTryCatch() {
		String key = "str_key";
		try {
			return getPropertyFromCache(key);
		} catch (MissingPropertyException e) {
			System.out.println("MissingPropertyException occured in purelyResilientTryCatch");
			return getPropertyFromFile(key);
		}
	}

	private String getProperty(String key, Boolean isCacheActivated) throws MissingPropertyException {
		return null;
	}

	private String format(String arg) throws MissingPropertyException {
		return null;
	}

	private String getArgument() throws IOException {
		return null;
	}
	
	private boolean getCacheAvailability() {
		Random random = new Random();
		return random.nextDouble() < 0.5;
	}
	
	private String getPropertyFromFile(String key) {
		return "property_from_file";
	}

	private String getPropertyFromCache(String key) throws MissingPropertyException {
		return "property_from_cache";
	}
	
    public static class CacheDisableException extends RuntimeException {
    }
    
    public static void main(String[] args) {
        TryCatchTestObject tcTest = new TryCatchTestObject();
        
    	tcTest.multipleTryCatch();
        tcTest.sourceIndependentTryCatch();
        tcTest.sourceDependentTryCatch();
        tcTest.purelyResilientTryCatch();
    }
}
