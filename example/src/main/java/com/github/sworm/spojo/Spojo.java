/**
 *
 */
package com.github.sworm.spojo;

import com.github.sworm.spojo.config.RuleMetadata;
import com.github.sworm.spojo.config.SpojoConfiguration;
import com.github.sworm.spojo.exceptions.RuleException;
import com.github.sworm.spojo.exceptions.SpojoException;
import com.github.sworm.spojo.utils.SpojoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeansException;

/**
 * Performs a processes using the configuration. <br>
 * The generic process is to reduce the given source object applying the Rule filter definition.
 * 
 * @author Vincent Palau
 * @Since Feb 27, 2011
 * @see SpojoConfiguration
 */
public class Spojo {

	private final Logger logger = LoggerFactory.getLogger(Spojo.class);

	private SpojoConfiguration configuration = null;

	/**
	 * @param configuration
	 */
	public Spojo(final SpojoConfiguration configuration) {
		setConfiguration(configuration);
	}

	public void checkConfiguration() throws SpojoException {
		if (getConfiguration() == null) {
			throw new SpojoException("Configuration not initialized yet");
		}
	}

	/**
	 * Reduce the source object applying the given Rule filter definition.
	 * 
	 * @param <T>
	 * 
	 * @param source
	 *            the source object to shrink
	 * @param name
	 *            the Rule definition to apply
	 * 
	 * @return a shrunken object
	 * @throws SpojoException
	 *             if the instantiation or the copying process failed.
	 * @throws RuleException
	 *             if the Rule definition is <code>DISABLED</code> or unknown
	 */
	public <T> T shrink(final T source, final String name) throws RuleException, SpojoException {
		T target = null;

		if (source != null) {
			checkConfiguration();
			Class<? extends Object> clazz = SpojoUtils.deriveClassFromSource(source);
			if (clazz == null) {
				logger.warn("Class for {} can't be obtained. Returning the same object.", source);
				return source;
			}
			final RuleMetadata ruleMetadata = getConfiguration().getRuleMetadata(name);
			target = this.internalShrink(source, ruleMetadata);
		}
		return target == null ? source : target;
	}

	/**
	 * Reduce the source object applying the given Rule filter definition over the target object.
	 * 
	 * @param <T>
	 * @param <E>
	 * 
	 * @param source
	 *            the source object to shrink
	 * @param target
	 *            the target object to copy properties
	 * @param name
	 *            the Rule definition to apply
	 * 
	 * @return a shrunken object
	 * @throws SpojoException
	 *             if the instantiation or the copying process failed.
	 * @throws RuleException
	 *             if the Rule definition is <code>DISABLED</code> or unknown
	 */
	public <T, E> E shrink(final T source, final E target, final String name) throws RuleException, SpojoException {

		if (source != null && target != null) {
			checkConfiguration();
			Class<? extends Object> clazz = SpojoUtils.deriveClassFromSource(source);
			if (clazz == null) {
				logger.warn("Class Type of {} can't be obtained. Returning the target object.", source.getClass());
				return target;
			}
			final RuleMetadata ruleMetadata = getConfiguration().getRuleMetadata(name);
			this.internalShrink(source, target, ruleMetadata);
		}
		return target;
	}

	/**
	 * First instantiate the target bean and then, copy the property values defined by given rule definition of the given source
	 * bean into the given target bean.
	 * 
	 * @param <T>
	 * @param source
	 *            the source object to shrink
	 * @param ruleMetadata
	 *            the Rule definition to apply
	 * @return a shrunken object
	 * @throws SpojoException
	 *             if the instantiation or the copying process failed.
	 * @throws RuleException
	 *             if the Rule definition is <code>DISABLED</code> or unknown
	 */
	@SuppressWarnings("unchecked")
	private <T> T internalShrink(final T source, final RuleMetadata ruleMetadata) throws RuleException, SpojoException {
		try {
			// create new target object
			T target = (T) SpojoUtils.instantiateAs(source);
			this.internalShrink(source, target, ruleMetadata);
			return target;
		} catch (BeanInstantiationException e) {
			throw new SpojoException("the bean couldn't be instanciated", e);
		}
	}

	/**
	 * First instantiate the target bean and then, copy the property values defined by given rule definition of the given source
	 * bean into the given target bean.
	 * 
	 * @param <T>
	 * @param source
	 *            the source object to shrink
	 * @param ruleMetadata
	 *            the Rule definition to apply
	 * @return a shrunken object
	 * @throws SpojoException
	 *             if the instantiation or the copying process failed.
	 * @throws RuleException
	 *             if the Rule definition is <code>DISABLED</code> or unknown
	 */
	private <T, E> void internalShrink(final T source, final E target, final RuleMetadata ruleMetadata) throws RuleException,
			SpojoException {
		try {
			// copy the members
			switch (ruleMetadata.getType()) {
			case EXCLUDE:

				SpojoUtils.copyProperties(source, target, ruleMetadata.getProperty(), false);
				break;

			case INCLUDE:
				SpojoUtils.copyProperties(source, target, ruleMetadata.getProperty(), true);
				break;

			case DISABLED:
				logger.warn("the Rule [{}] is disabled ", ruleMetadata.getName());
				SpojoUtils.copyProperties(source, target, null, false);
			default:
				logger.error("the Rule [{}] is an unexpected type {} ", ruleMetadata.getName(), ruleMetadata.getType());
				throw new RuleException(String.format("the Rule [%s] is an unexpected type %s ", ruleMetadata.getName(),
						ruleMetadata.getType()));
			}
		} catch (BeanInstantiationException e) {
			throw new SpojoException("the bean couldn't be instanciated", e);
		} catch (BeansException e) {
			throw new SpojoException("the copying process failed", e);
		}
	}

	/**
	 * @return configuration
	 */
	protected SpojoConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 * @param configuration
	 *            the configuration to set
	 */
	public void setConfiguration(final SpojoConfiguration configuration) {
		this.configuration = configuration;
	}

}
