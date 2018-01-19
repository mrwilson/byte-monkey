/**
 * 
 */
package com.github.sworm.spojo.data;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * 
 * Abstract Property of a hierarchy. <br>
 * This component can be a child and parent at the same time.
 * 
 * 
 * @author Vincent Palau
 * @Since Feb 27, 2011
 * 
 * @param <TYPE>
 */
public abstract class AbstractProperty<TYPE extends Comparable<TYPE>> implements Property<TYPE> {

	private Property<TYPE> parent = null;

	private String name = null;

	private String definition = null;

	private final Map<String, Property<TYPE>> childMap = new TreeMap<String, Property<TYPE>>();

	/**
	 * @param parent
	 *            the parent into the hierarchy
	 * @param definition
	 *            the definition of the property (name)
	 */
	public AbstractProperty(final Property<TYPE> parent, final String definition) {
		setDefinition(definition);
		setParent(parent);
	}

	/**
	 * @param ndefinition
	 *            the definition of the property (name)
	 */
	public AbstractProperty(final String definition) {
		setDefinition(definition);
	}

	/**
	 * @see com.github.sworm.spojo.data.Property#getName()
	 */
	public String getName() {
		return name;
	}

	/**
	 * @see com.github.sworm.spojo.data.Property#getParent()
	 */

	public Property<TYPE> getParent() {
		return parent;
	}

	/**
	 * @param parent
	 *            the parent to set
	 */

	public void setParent(final Property<TYPE> parent) {
		if (this.parent == parent) {
			return;
		}
		final Property<TYPE> currParent = this.parent;
		this.parent = parent;
		if (this.parent != null) {
			this.parent.add(this);
		}
		if (currParent != null) {
			currParent.removeProperty(this);
		}
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */

	public int compareTo(final Property<TYPE> anotherComparable) {
		return getName().compareTo(anotherComparable.getName());
	}

	/**
	 * creates a hierarchy property <br>
	 * The expression is a dot based expression :"prop1.prop2.prop3" and comma-delimited to separate different properties
	 * 
	 * @param expression
	 *            the property expression
	 */
	protected void createPropertyHierachy(final String expression) {
		final String[] childDefinitions = expression.split(",");
		for (final String childDefinition : childDefinitions) {
			final String[] definitions = childDefinition.split("\\.");
			Property<TYPE> parent = this;
			for (final String definitionParam : definitions) {
				// create and link the property
				Property<TYPE> childProperty = parent.getChild(this.extractNameFromDefinition(definitionParam));
				if (childProperty == null) {
					childProperty = createProperty(definitionParam);
					parent.add(childProperty);
				}
				parent = childProperty;
			}

		}
	}

	/**
	 * To implement by specific classes
	 * 
	 * @param propertyDefinition
	 * @return
	 */
	protected abstract Property<TYPE> createProperty(final String propertyDefinition);

	/**
	 * @see com.github.sworm.spojo.data.Property#getCanonicalName()
	 */
	public String getCanonicalName() {
		final StringBuilder sb = new StringBuilder();
		if (getParent() != null) {
			sb.append(getParent().getCanonicalName()).append('.');
		}
		sb.append(getName());
		return sb.toString();
	}

	/**
	 * 
	 * @see com.github.sworm.spojo.data.Property#hasParent()
	 */
	public boolean hasParent() {
		return getParent() != null;
	}

	/**
	 * @see com.github.sworm.spojo.data.Property#add(com.github.sworm.spojo.data.Property)
	 */
	public Property<TYPE> add(final Property<TYPE> property) {
		property.setParent(this);
		getChildMap().put(property.getName(), property);
		return this;
	}

	/**
	 * @see com.github.sworm.spojo.data.Property#add(java.util.Collection)
	 */
	public Property<TYPE> add(final Collection<String> propertyExpressionCollection) {
		if (propertyExpressionCollection != null) {
			for (String propertyExpression : propertyExpressionCollection) {
				this.add(propertyExpression);
			}
		}
		return this;
	}

	/**
	 * @see com.github.sworm.spojo.data.Property#add(java.lang.String)
	 */
	public Property<TYPE> add(final String propertyExpression) {
		this.createPropertyHierachy(propertyExpression);
		return this;
	}

	/**
	 * 
	 * @see com.github.sworm.spojo.data.Property#merge(com.github.sworm.spojo.data.Property)
	 */
	public Property<TYPE> merge(final Property<TYPE> propertyToMerge) {
		for (Property<TYPE> childToMerge : propertyToMerge) {
			Property<TYPE> childDest = this.getChild(childToMerge.getName());
			if (childDest == null) {
				// add it from definition
				this.add(childToMerge.getDefinition());
				childDest = this.getChild(childToMerge.getName());
			}
			// do the merge
			childDest.merge(childToMerge);
		}
		return this;
	}

	/**
	 * @see com.github.sworm.spojo.data.Property#clear()
	 */

	public void clear() {
		// before clear, we must remove the reference of the children
		final Iterator<Property<TYPE>> iterator = iterator();
		while (iterator.hasNext()) {
			final Property<TYPE> elem = iterator.next();
			iterator.remove();
			elem.setParent(null);
		}
	}

	/**
	 * @param key
	 * @return
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	public boolean containsChild(final String name) {
		return getChildMap().containsKey(name);
	}

	/**
	 * @param key
	 * @return
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public Property<TYPE> getChild(final String name) {
		return getChildMap().get(name);
	}

	/**
	 * @see com.github.sworm.spojo.data.Property#getProperties()
	 */
	public Collection<Property<TYPE>> getProperties() {
		final Collection<Property<TYPE>> values = getChildMap().values();
		return Collections.unmodifiableCollection(values);
	}

	/**
	 * @see com.github.sworm.spojo.data.Property#isEmpty()
	 */
	public boolean isEmpty() {
		return getChildMap().isEmpty();
	}

	/**
	 * @see com.github.sworm.spojo.data.Property#iterator()
	 */
	public Iterator<Property<TYPE>> iterator() {
		return getChildMap().values().iterator();
	}

	/**
	 * @see com.github.sworm.spojo.data.Property#remove(java.lang.Object)
	 */
	public void removeProperty(final Property<TYPE> property) {
		// obtain the property by name, not by instance
		final Property<TYPE> currentProp = getChildMap().get(property.getName());
		if (currentProp != null) {
			getChildMap().remove(currentProp.getName());
			if (currentProp.getParent() == this) {
				currentProp.setParent(null);
			}
		}
	}

	/**
	 * @see com.github.sworm.spojo.data.Property#size()
	 */
	public int size() {
		return getChildMap().size();
	}

	/**
	 * @see com.github.sworm.spojo.data.Property#toArray()
	 */
	public Object[] toArray() {
		return getProperties().toArray();
	}

	/**
	 * @see com.github.sworm.spojo.data.Property#toArray(T[])
	 */
	public <T> T[] toArray(final T[] a) {
		return getProperties().toArray(a);
	}

	/**
	 * @see com.github.sworm.spojo.data.Property#isComplexType()
	 */

	public boolean isComplexType() {
		return !isEmpty();
	}

	/**
	 * @return the childMap
	 */
	public Map<String, Property<TYPE>> getChildMap() {
		return childMap;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return generate2String(this);
	}

	/**
	 * @param property
	 * @return
	 */
	private String generate2String(final Property<TYPE> property) {
		final StringBuilder builder = new StringBuilder();
		builder.append(property.getName());

		builder.append('@').append(property.getName()).append('{');

		// children information
		builder.append('[');
		for (final Property<TYPE> element : property) {
			if (element.isComplexType()) {
				builder.append(element);
			} else {
				builder.append(element.getName());
			}
			builder.append(",");
		}
		// hack to remove last comma
		if (property.isComplexType()) {
			final int length = builder.length();
			builder.delete(length - 1, length);
		}
		builder.append(']');

		// parent information
		builder.append(",parent=").append((property.hasParent() ? property.getParent().getName() : "null"));

		builder.append('}');
		return builder.toString();
	}

	/**
	 * @see com.github.sworm.spojo.data.Property#getParentRoot()
	 */
	public Property<TYPE> getParentRoot() {
		return this.hasParent() ? getParent().getParentRoot() : this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof AbstractProperty<?>)) {
			return false;
		}
		@SuppressWarnings("unchecked")
		final AbstractProperty<TYPE> other = (AbstractProperty<TYPE>) obj;

		if (name == null) {
			if (other.getName() != null) {
				return false;
			}
		} else if (!name.equals(other.getName())) {
			return false;
		}

		if (definition == null) {
			if (other.getDefinition() != null) {
				return false;
			}
		} else if (!definition.equals(other.getDefinition())) {
			return false;
		}
		return (childMap != null) ? childMap.equals(other.childMap) : (other.childMap == null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((childMap == null) ? 0 : childMap.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((definition == null) ? 0 : definition.hashCode());
		return result;
	}

	/**
	 * @return the definition
	 */
	public String getDefinition() {
		return definition;
	}

	/**
	 * @param definition
	 *            the definition to set
	 */
	protected void setDefinition(final String definition) {
		this.definition = definition;
		// parse the definition and create rest of the parameters
		this.name = this.extractNameFromDefinition(definition);
	}

	/**
	 * extracts the name from the definition <br>
	 * By default, definition is equaLS to name
	 * 
	 * @param definition
	 * @return the name
	 */
	protected String extractNameFromDefinition(final String definition) {
		return definition;
	}

}
