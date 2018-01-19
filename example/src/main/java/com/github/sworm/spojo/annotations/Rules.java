/**
 *
 */
package com.github.sworm.spojo.annotations;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Rule Definitions
 * 
 * TODO[vpalau]
 * 
 * @author Vincent Palau
 * @Since Feb 27, 2011
 * 
 */

@Target(TYPE)
@Retention(RUNTIME)
public @interface Rules {

	/**
	 * (Required) An array of <code>Rule</code> annotations.
	 **/
	Rule[] value();
}
