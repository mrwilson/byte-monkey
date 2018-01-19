package com.github.sworm.spojo.enums;

/**
 * Rules Type is used by Rule executor.
 * 
 * 
 * @author Vincent Palau
 * @Since Feb 27, 2011
 */
public enum RuleType {
	/**
	 * Specifies that the rule executor has to <b>include</b> the associated configuration.
	 */
	INCLUDE,
	/**
	 * Specifies that the rule executor has to <b>exclude</b> the associated configuration.
	 */
	EXCLUDE,

	/**
	 * The rule is not active, Specifies that the rule executor has to <b>ignore</b> the associated configuration.
	 */
	DISABLED

}
