package com.github.sworm.spojo.config;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.github.sworm.spojo.Spojo;
import com.github.sworm.spojo.annotations.Rule;
import com.github.sworm.spojo.annotations.Rules;
import com.github.sworm.spojo.exceptions.RuleNotFoundException;
import com.github.sworm.spojo.exceptions.SpojoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An instance of <tt>SpojoConfiguration</tt> allows the application to specify a Rule name, properties to be used when reducing
 * by <tt>Spojo</tt>. <br>
 * Usually an application will create a single instance <tt>SpojoConfiguration</tt>.<br>
 * 
 * @author Vincent Palau
 * @Since Feb 27, 2011
 * @see Spojo
 * @see Rules
 * @see Rule
 */
public class SpojoConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(SpojoConfiguration.class);

	private Map<String, RuleMetadata> ruleMapByName = null;

	public SpojoConfiguration() throws SpojoException {
		this(new HashMap<String, RuleMetadata>());
	}

	/**
	 * @param ruleMapByName
	 * @throws SpojoException
	 *             if parameter is null
	 */
	public SpojoConfiguration(final Map<String, RuleMetadata> ruleMapByName) throws SpojoException {
		setRuleMapByName(ruleMapByName);
	}

	/**
	 * @param ruleMetadataList
	 *            a list of filter definitions
	 * @throws SpojoException
	 *             if parameter is null
	 */
	public SpojoConfiguration(final List<RuleMetadata> ruleMetadataList) throws SpojoException {
		addRuleMetadata(ruleMetadataList);
	}

	/**
	 * @param ruleMetadata
	 *            a filter definition
	 * @throws SpojoException
	 *             if parameter is null
	 */
	public SpojoConfiguration(final RuleMetadata ruleMetadata) throws SpojoException {
		addRuleMetadata(ruleMetadata);
	}

	/**
	 * Read the annotation and update the reference map
	 * 
	 * @param clazz
	 *            the class annotated
	 */
	public void addClass(final Class<?> clazz) throws SpojoException {

		if (clazz == null) {
			throw new SpojoException("class parameter must not be null");
		}

		Annotation[] declaredAnnotations = clazz.getDeclaredAnnotations();

		List<Rule> ruleList = null;

		for (Annotation annotation : declaredAnnotations) {
			if (annotation instanceof Rules) {
				Rule[] rules = ((Rules) annotation).value();
				ruleList = Arrays.asList(rules);

			} else if (annotation instanceof Rule) {
				Rule rule = (Rule) annotation;
				ruleList = new ArrayList<Rule>();
				ruleList.add(rule);
			}
		}

		int counter = 0;
		if (ruleList != null && !ruleList.isEmpty()) {
			addRule(ruleList);
			counter = ruleList.size();
		}

		if (counter == 0) {
			logger.warn("There is no Rule annotation defined in class: {}, verify your configuration", clazz);
		} else {
			logger.debug("adding {} 'Rule' config to {}", counter, clazz);
		}
	}

	/**
	 * Associates the specified <code>RuleList</code> with the specified <code>clazz</code> in the ruleNameByClazzMap.<br>
	 * If the <code>clazz</code> doesn't have a current configuration, a new one is created. <br>
	 * If the map contain a mapping for the given name, the old value is replaced by the specified value.
	 * 
	 * @param clazz
	 *            the class annotated
	 * @param ruleList
	 *            the list of Rule definition
	 * @throws SpojoException
	 * @see Rule
	 */
	protected void addRule(final List<Rule> ruleList) throws SpojoException {

		if (ruleList == null) {
			throw new SpojoException("ruleList parameter must not be null");
		}
		if (ruleList.isEmpty()) {
			throw new SpojoException("ruleList parameter must not be empty");
		}

		// first pass: add Rules to map
		for (Rule rule : ruleList) {
			RuleMetadata ruleMetadataImpl = convertAnnotation2Impl(rule);
			getRuleMapByName().put(rule.name(), ruleMetadataImpl);
		}
		// secondPass
		processExtendedRules();
	}

	/**
	 * Adds a rulemetadata definition to the class.
	 * 
	 * @param clazz
	 *            the class to be mapped
	 * @param ruleMetadata
	 *            the metadata definition
	 * @return this
	 * @throws SpojoException
	 *             if some of the parameters is null
	 */
	public SpojoConfiguration addRuleMetadata(final RuleMetadata ruleMetadata) throws SpojoException {
		if (ruleMetadata == null) {
			throw new SpojoException("ruleMetadata parameter must not be null");
		}
		getRuleMapByName().put(ruleMetadata.getName(), ruleMetadata);
		// secondPass
		iterateExtendsFrom(ruleMetadata);
		return this;
	}

	/**
	 * Adds a list of rulemetadata definition to the class
	 * 
	 * @param clazz
	 *            the class to be mapped
	 * @param ruleMetadataList
	 *            the metadata list definition
	 * @return this
	 * @throws SpojoException
	 *             if some of the parameters is null
	 */
	public SpojoConfiguration addRuleMetadata(final List<RuleMetadata> ruleMetadataList) throws SpojoException {
		if (ruleMetadataList == null) {
			throw new SpojoException("ruleMetadataList parameter must not be null");
		}

		if (ruleMetadataList.isEmpty()) {
			throw new SpojoException("ruleMetadataList parameter must not be empty");
		}

		for (RuleMetadata ruleMetadata : ruleMetadataList) {
			getRuleMapByName().put(ruleMetadata.getName(), ruleMetadata);
		}
		// secondPass
		if (!ruleMetadataList.isEmpty()) {
			processExtendedRules();
		}
		return this;
	}

	/**
	 * Special Spring Purpose
	 * 
	 * @param rulesMetadataClassBean
	 * @return this
	 */
	public SpojoConfiguration setRuleMetadata(final List<RuleMetadata> ruleMetadataList) throws SpojoException {
		return addRuleMetadata(ruleMetadataList);
	}

	/**
	 * Instance an implementation of the Rule annotation to the correspondent Class.
	 * 
	 * @param rule
	 *            the given annotation.
	 * @return the Rule annotation converted to his Implementation.
	 */
	protected RuleMetadata convertAnnotation2Impl(final Rule rule) {
		return new RuleMetadataImpl(rule.name(), rule.type(), rule.extendsFrom(), rule.properties());
	}

	/**
	 * Append the property definitions to the property with inheritance.
	 */
	protected void processExtendedRules() throws SpojoException {
		processExtendedRules(getRuleMapByName());
	}

	/**
	 * Append the property definitions to the property with inheritance.
	 * 
	 * @param ruleNameMap
	 *            the map configuration content
	 */
	protected void processExtendedRules(final Map<String, RuleMetadata> ruleNameMap) throws SpojoException {

		if (ruleNameMap != null) {
			for (Entry<String, RuleMetadata> entry : ruleNameMap.entrySet()) {
				final RuleMetadata ruleMetadata = entry.getValue();

				if (ruleMetadata == null) {
					logger.warn("The RuleMetada with name [{}] is null , for this reason, no propertie is added",
							new Object[] { entry.getKey() });

				} else if (ruleMetadata.hasInheritance()) {
					iterateExtendsFrom(ruleMetadata, ruleNameMap, new HashSet<String>());
				}
			}
		}
	}

	/**
	 * Appends extended properties to the given <code>ruleMetadata</code> if their type match.
	 * 
	 * @param ruleMetadata
	 */
	protected void iterateExtendsFrom(final RuleMetadata ruleMetadata) throws SpojoException {
		iterateExtendsFrom(ruleMetadata, getRuleMapByName(), new HashSet<String>());
	}

	/**
	 * Appends extended properties to the given <code>ruleNameMap</code> if their type match.
	 * 
	 * @param ruleMetadata
	 *            the given ruleMetadata definition
	 * @param ruleNameMap
	 *            the map configuration content
	 * @throws SpojoException
	 */
	protected void iterateExtendsFrom(final RuleMetadata ruleMetadata, final Map<String, RuleMetadata> ruleNameMap,
			final Set<String> processing) throws SpojoException {
		if (ruleMetadata == null) {
			logger.warn("The Rule definition is null");
		} else if (ruleMetadata.getExtendsFrom().isEmpty()) {
			logger.warn("The Rule [{}] doesn't extends from another metadata, for this reason, no properties are added",
					new Object[] { ruleMetadata.getName() });
		} else {
			for (String name : ruleMetadata.getExtendsFrom()) {
				if (ruleNameMap == null) {
					throw new SpojoException("ruleNameMap parameter must not be null");
				}

				final RuleMetadata ruleMetadataToExtend = ruleNameMap.get(name);
				if (processing == null) {
					throw new SpojoException("processing parameter must not be null");
				}
				if (ruleMetadata.getName().equals(name)) {
					logger.warn(
							"Cycle detected: the type Rule[{}] cannot extend itself, for this reason, no properties are added",
							ruleMetadata.getName());

					// to avoid infinite looping over items already iterated
				} else if (!processing.contains(name)) {
					if (ruleMetadataToExtend == null) {
						logger.warn(
								"The Rule [{}] extendsFrom [{}] and this Rule don't exist, for this reason, no properties are added",
								new Object[] { ruleMetadata.getName(), name });
					} else if (ruleMetadata.getType() == ruleMetadataToExtend.getType()) {
						processing.add(name);
						// they have the same type
						if (ruleMetadataToExtend.hasInheritance()) {
							iterateExtendsFrom(ruleMetadataToExtend, ruleNameMap, processing);
						}
						// copy the properties definition
						ruleMetadata.extendFromMetadata(ruleMetadataToExtend);
					} else {
						logger.warn(
								"The Rule [{}] with type [{}] doesn't match type with [{}], for this reason, no properties are added",
								new Object[] { ruleMetadata.getName(), ruleMetadata.getType(), ruleMetadataToExtend.getName() });
					}
				} else {
					logger.warn("There is a loop definition with extendsFrom[{}] in the Rule [{}]. Rules processed [{}]",
							new Object[] { name, ruleMetadata.getName(), processing });
				}
			}
		}
	}

	/**
	 * @param name
	 *            the Rule Name
	 * @return Rule definition for the given parameters
	 * @throws RuleNotFoundException
	 *             if the class or the <code>name</code> doesn't have Rule definition
	 * @see Rules
	 * @see Rule
	 */
	public RuleMetadata getRuleMetadata(final String name) throws RuleNotFoundException {
		RuleMetadata ruleMetadata = null;

		// get definition from the map by name
		ruleMetadata = getRuleMapByName().get(name);
		if (ruleMetadata == null) {
			RuleNotFoundException ruleNotFoundException = new RuleNotFoundException(name);
			logger.warn(ruleNotFoundException.getMessage());
			throw ruleNotFoundException;
		}
		logger.debug("the Rule definition with name: [{}] is: [{}]", new Object[] { name, ruleMetadata });
		return ruleMetadata;
	}

	/**
	 * @return the ruleMapByName
	 */
	protected Map<String, RuleMetadata> getRuleMapByName() {
		return ruleMapByName;
	}

	/**
	 * Sets a new configuration for the spojo.
	 * 
	 * @param ruleMapByName
	 *            the ruleMapByName to set
	 * @throws SpojoException
	 *             if parameter is null
	 */
	protected void setRuleMapByName(final Map<String, RuleMetadata> ruleMapByName) {
		if (ruleMapByName == null) {
			throw new SpojoException("ruleMapByName can not be null!!");
		}
		this.ruleMapByName = ruleMapByName;
		processExtendedRules(this.ruleMapByName);
	}

}
