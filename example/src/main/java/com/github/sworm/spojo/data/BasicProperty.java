/**
 * 
 */
package com.github.sworm.spojo.data;

/**
 * Concrete definition of a String property into the Hierarchy.
 * 
 * @author Vincent Palau
 * @Since Feb 27, 2011
 * 
 */
public class BasicProperty extends AbstractProperty<String> {

	/**
	 * @param parent
	 * @param name
	 */
	public BasicProperty(final Property<String> parent, final String name) {
		super(parent, name);
	}

	/**
	 * @param name
	 */
	public BasicProperty(final String name) {
		super(name);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.github.sworm.spojo.data.AbstractProperty#createProperty(java.lang.String)
	 */
	@Override
	protected Property<String> createProperty(final String propertyDefinition) {
		return new BasicProperty(propertyDefinition);
	}

}
