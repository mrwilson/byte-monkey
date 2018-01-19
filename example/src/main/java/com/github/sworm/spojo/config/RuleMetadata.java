/**
 * 
 */
package com.github.sworm.spojo.config;

import java.util.Collection;

import com.github.sworm.spojo.data.Property;
import com.github.sworm.spojo.enums.RuleType;

/**
 * @author Vincent Palau
 * @Since Feb 27, 2011
 * 
 */
public interface RuleMetadata {

	/**
	 * @return the extendsFrom
	 */
	Collection<String> getExtendsFrom();

	/**
	 * @return the name
	 */
	String getName();

	/**
	 * @return the type
	 */
	RuleType getType();

	/**
	 * true/false if it has inheritance
	 * 
	 * @return
	 */
	boolean hasInheritance();

	/**
	 * @return the property
	 */
	Property<String> getProperty();

	/**
	 * extends this metadata from the parameter metadata
	 * 
	 * @param ruleMetadata
	 */
	void extendFromMetadata(RuleMetadata ruleMetadata);

}