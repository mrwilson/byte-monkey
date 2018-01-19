/**
 * 
 */
package com.github.sworm.spojo.model;

import java.util.Date;

/**
 * @author Vincent Palau
 * @Since Feb 27, 2011
 * 
 */
public class Chef extends Person {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4588804265363705802L;

	/**
	 * 
	 */
	public Chef() {
	}

	/**
	 * @param name
	 * @param gender
	 * @param birthday
	 * @param street
	 */
	public Chef(final String name, final Gender gender, final Date birthday, final String street) {
		super(name, gender, birthday, street);
	}

}
