/**
 * 
 */
package com.github.sworm.spojo.model;

import static com.github.sworm.spojo.enums.RuleType.*;

import java.util.Date;

import com.github.sworm.spojo.annotations.Rule;
import com.github.sworm.spojo.annotations.Rules;

/**
 * @author Vincent Palau
 * @Since Feb 27, 2011
 * 
 */
// @formatter:off
@Rules(
	@Rule(name="nameGender", type=INCLUDE, properties ={"name","gender"} )
)
//@formatter:on
public class Client extends Person {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6138591825277201413L;

	/**
	 * 
	 */
	public Client() {
	}

	/**
	 * @param name
	 * @param gender
	 * @param birthday
	 * @param street
	 */
	public Client(final String name, final Gender gender, final Date birthday, final String street) {
		super(name, gender, birthday, street);
	}

}
