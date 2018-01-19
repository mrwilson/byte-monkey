/**
 * 
 */
package com.github.sworm.spojo.exceptions;

/**
 * @author Vincent Palau
 * 
 * @Since Oct 11, 2011
 * 
 */
public class SpojoException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5238631631280738351L;

	/**
	 * @param message
	 */
	public SpojoException(final String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public SpojoException(final Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SpojoException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
