package com.github.sworm.spojo.data;

import java.util.Collection;

/**
 * 
 * Property of a Hierarchy Structure. <br>
 * This component can be a child and parent at the same time.
 * 
 * @author Vincent Palau
 * @Since Feb 27, 2011
 * 
 * @param <TYPE>
 */
public interface Property<TYPE extends Comparable<TYPE>> extends Comparable<Property<TYPE>>, Iterable<Property<TYPE>> {

	/**
	 * @return the name of the property
	 */
	String getName();

	/**
	 * @return the parent into the hierarchy
	 */
	Property<TYPE> getParent();

	/**
	 * return is this property has a parent
	 * 
	 * @return
	 */
	boolean hasParent();

	/**
	 * Associates the specified property with the specified key in this map
	 * 
	 * @param property
	 * 
	 * @return this (fluent coding)
	 */
	Property<TYPE> add(Property<TYPE> property);

	/**
	 * Associates and creates the specified property with this property
	 * 
	 * @param propertyExpression
	 * 
	 * @return this (fluent coding)
	 */
	Property<TYPE> add(String propertyExpression);

	/**
	 * Associates and creates the specified property with this property
	 * 
	 * @param propertyExpressionCollection
	 * 
	 * @return this (fluent coding)
	 */
	Property<TYPE> add(Collection<String> propertyExpressionCollection);

	/**
	 * 
	 * @see java.util.Set#clear()
	 */
	void clear();

	/**
	 * @return the properties
	 */
	Collection<Property<TYPE>> getProperties();

	/**
	 * @return
	 * @see java.util.Set#isEmpty()
	 */
	boolean isEmpty();

	/**
	 * @param o
	 * @see java.util.Set#remove(java.lang.Object)
	 */
	void removeProperty(Property<TYPE> property);

	/**
	 * Returns the number of child's.
	 * 
	 * @return the number of child's in this property
	 */
	int size();

	/**
	 * @return an array of child properties
	 * @see java.util.Set#toArray()
	 */
	Object[] toArray();

	/**
	 * @param <T>
	 * @param a
	 *            the array into which the elements of this set are to be stored, if it is big enough; otherwise, a new array of
	 *            the same runtime type is allocated for this purpose.
	 * @return an array of child properties
	 * @see java.util.Set#toArray(Object[])
	 */
	<T> T[] toArray(T[] a);

	/**
	 * @return <code>true</code> if have child's
	 */
	boolean isComplexType();

	/**
	 * Returns the canonical pathname string of this abstract pathname.
	 * 
	 * <p>
	 * A canonical pathname is both absolute and unique. The precise definition of canonical form is simply to go up the hierarchy
	 * levels appending dots <tt>"."</tt> at each levels.
	 * <p>
	 * 
	 * @return The canonical pathname
	 */
	String getCanonicalName();

	/**
	 * Update the parent of the property
	 * 
	 * @param parent
	 *            the parent to set
	 */
	void setParent(Property<TYPE> parent);

	/**
	 * @param name
	 *            the child property name to look for
	 * @return <code>true</code> if a child property exist with this given name
	 */
	boolean containsChild(String name);

	/**
	 * Returns the property to which the specified name is mapped, or null if this child name don't exist.
	 * 
	 * @param name
	 *            the child property name to look for
	 * @return the property to which the specified name is mapped, or null if this child name don't exist
	 */
	Property<TYPE> getChild(String name);

	/**
	 * Returns the root parent property of this property. Navigate on all parents until find the last one.<br>
	 * If this property has not parent, then return this
	 * 
	 * @return the root parent of this property
	 */
	Property<TYPE> getParentRoot();

	/**
	 * Returns the property definition
	 * 
	 * @return
	 */
	String getDefinition();

	/**
	 * makes a merge between the parameter property and this<br>
	 * Only merges the children
	 * 
	 * @param propertyToMerge
	 * @return this
	 */
	Property<TYPE> merge(final Property<TYPE> propertyToMerge);

}