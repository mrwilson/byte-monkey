/**
 *
 */
package com.github.sworm.spojo.exceptions;

/**
 * @author Vincent Palau
 * @Since Feb 27, 2011
 * 
 */
public class RuleNotFoundException extends SpojoException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7352862258052973608L;

	/**
	 * @param filterName
	 */
	public RuleNotFoundException(final String filterName) {
		super(String.format("the Rule definition is not found with filterName: [%s]", filterName));
	}

}
