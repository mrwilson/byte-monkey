/**
 *
 */
package com.github.sworm.spojo.annotations;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;
import static com.github.sworm.spojo.enums.RuleType.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.github.sworm.spojo.Spojo;
import com.github.sworm.spojo.enums.RuleType;

/**
 * 
 * Rule Definition
 * 
 * TODO[vpalau]
 * 
 * @author Vincent Palau
 * @Since Feb 27, 2011
 * 
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface Rule {

	/**
	 * (Required) The name used to refer to the process that the {@link Spojo} executes.
	 */
	String name();

	/**
	 * (Optional) The type to use in rule execution. If a <code>type = RuleType.DISABLED</code> is specified, the rule is not
	 * active
	 */
	RuleType type() default DISABLED;

	/**
	 * (Required) properties to used on performing with the rule condition
	 */
	String[] properties() default {};

	/**
	 * (Optional) The names used to append other properties to the current properties.<br>
	 * <b>The name must have the same type</b>.
	 */
	String[] extendsFrom() default {};

}
