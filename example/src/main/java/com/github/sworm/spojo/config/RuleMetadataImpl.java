package com.github.sworm.spojo.config;

import java.util.ArrayList;
import java.util.Collection;

import com.github.sworm.spojo.data.BasicProperty;
import com.github.sworm.spojo.data.Property;
import com.github.sworm.spojo.enums.RuleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represent the configuration of the fields to copy
 * 
 * 
 * @author Vincent Palau
 * @Since Feb 27, 2011
 * 
 */
public class RuleMetadataImpl implements RuleMetadata {

	private final static Logger logger = LoggerFactory.getLogger(RuleMetadataImpl.class);

	private RuleType type = null;

	private Collection<String> extendsFrom = null;

	private Property<String> property = null;

	/**
	 * @param name
	 * @param type
	 * @param extendsFrom
	 * @param properties
	 */
	public RuleMetadataImpl(final String name, final RuleType type, final String[] extendsFrom, final String[] properties) {
		this.property = new BasicProperty(name);
		setType(type);
		setExtendsFrom(makeFilteredCollection(extendsFrom, "extendsFrom"));
		this.property.add(makeFilteredCollection(properties, "properties"));
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.github.sworm.spojo.config.RuleMetadata#extendFromMetadata(com.github.sworm.spojo.config.RuleMetadata)
	 */
	public void extendFromMetadata(final RuleMetadata ruleMetadata) {
		if (ruleMetadata != null) {
			this.property.merge(ruleMetadata.getProperty());
		}
	}

	/**
	 * Returns a Set cleaned up without null values.
	 * 
	 * @param rule
	 *            Annotation used for the name and type in logging purpose
	 * 
	 * @param array
	 *            values to filter
	 * @param string
	 * @return a Collection cleaned up without null values.
	 */
	private Collection<String> makeFilteredCollection(final String[] array, final String attributeName) {
		final Collection<String> list = new ArrayList<String>();

		if (array != null) {
			for (final String element : array) {
				if (element == null) {
					logger.warn("The Rule [{}] with type [{}] have null {} value setted. Modify the configuration, please.",
							new Object[] { getName(), getType(), attributeName });
				} else {
					list.add(element);
				}
			}
		}

		return list;
	}

	/**
	 * @return the extendsFrom
	 */
	public Collection<String> getExtendsFrom() {
		return extendsFrom;
	}

	/**
	 * @param extendsFrom
	 *            the extendsFrom to set
	 */
	void setExtendsFrom(final Collection<String> extendsFrom) {
		this.extendsFrom = extendsFrom;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return property.getName();
	}

	/**
	 * @return the type
	 */
	public RuleType getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	void setType(final RuleType type) {
		this.type = type;
	}

	public boolean hasInheritance() {
		return extendsFrom != null && !extendsFrom.isEmpty();
	}

	/**
	 * @return the property
	 */
	public Property<String> getProperty() {
		return property;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("RuleMetadataImpl [name=%s, type=%s, extendsFrom=%s, property=%s]", getName(), type, extendsFrom,
				property.toString());
	}

}
