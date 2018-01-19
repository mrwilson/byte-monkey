/**
 * 
 */
package com.github.sworm.spojo.factories;

import com.github.sworm.spojo.config.SpojoConfiguration;

/**
 * @author Vincent Palau
 * @Since Feb 27, 2011
 * 
 */
public interface LoaderFactoryFactory {

	/**
	 * @return get the SpojoConfiguration filled
	 */
	SpojoConfiguration getConfiguration();

	/**
	 * @param packagesToScan
	 *            the packagesToScan to set
	 */
	void setPackagesToScan(final String[] packagesToScan);

	/**
	 * @param spojoConfiguration
	 *            the SpojoConfiguration to set
	 */
	void setSpojoConfiguration(final SpojoConfiguration spojoConfiguration);

}