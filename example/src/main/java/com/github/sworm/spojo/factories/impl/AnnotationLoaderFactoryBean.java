/**
 *
 */
package com.github.sworm.spojo.factories.impl;

import static org.springframework.core.io.support.ResourcePatternResolver.*;

import java.io.IOException;

import com.github.sworm.spojo.annotations.Rule;
import com.github.sworm.spojo.annotations.Rules;
import com.github.sworm.spojo.config.SpojoConfiguration;
import com.github.sworm.spojo.exceptions.SpojoException;
import com.github.sworm.spojo.factories.LoaderFactoryFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;

/**
 * @author Vincent Palau
 * @Since Feb 27, 2011
 * 
 */
public class AnnotationLoaderFactoryBean implements LoaderFactoryFactory {

	private static final String RESOURCE_PATTERN = "/**/*.class";

	private String[] packagesToScan = null;

	private final ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

	// @formatter:off
	private final TypeFilter[] ruleFilters = new TypeFilter[] {
						new AnnotationTypeFilter(Rules.class, false),
						new AnnotationTypeFilter(Rule.class, false)};
	// @formatter:on

	private SpojoConfiguration spojoConfiguration = null;

	/**
	 * 
	 */
	public AnnotationLoaderFactoryBean() {
	}

	public SpojoConfiguration getConfiguration() {
		scanPackages();
		return getSpojoConfiguration();
	}

	protected void scanPackages() {
		if (packagesToScan != null) {

			SpojoConfiguration spojoConfiguration = getSpojoConfiguration();

			try {
				for (String pkg : packagesToScan) {
					String pattern = CLASSPATH_ALL_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(pkg) + RESOURCE_PATTERN;
					Resource[] resources = resourcePatternResolver.getResources(pattern);
					MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
					for (Resource resource : resources) {
						if (resource.isReadable()) {
							MetadataReader reader = readerFactory.getMetadataReader(resource);
							String className = reader.getClassMetadata().getClassName();

							if (matchesRuleFilter(reader, readerFactory)) {
								spojoConfiguration.addClass(resourcePatternResolver.getClassLoader().loadClass(className));
							}
						}
					}
				}
			} catch (IOException ex) {
				throw new SpojoException("Failed to scan classpath for unlisted classes", ex);
			} catch (ClassNotFoundException ex) {
				throw new SpojoException("Failed to load annotated classes from classpath", ex);
			}
		}
	}

	/**
	 * Check whether any of the configured Rule entity type filters matches the current class descriptor contained in the metadata
	 * reader.
	 */
	protected boolean matchesRuleFilter(final MetadataReader reader, final MetadataReaderFactory readerFactory)
			throws IOException {
		return matchesBasicFilter(reader, readerFactory, ruleFilters);
	}

	/**
	 * Check whether any of the configured entity type filters matches the current class descriptor contained in the metadata
	 * reader.
	 */
	protected boolean matchesBasicFilter(final MetadataReader reader, final MetadataReaderFactory readerFactory,
			final TypeFilter[] typeFilter) throws IOException {
		if (typeFilter != null) {
			for (TypeFilter filter : typeFilter) {
				if (filter.match(reader, readerFactory)) {
					return true;
				}
			}
		}
		return false;
	}

	public void setPackagesToScan(final String[] packagesToScan) {
		this.packagesToScan = packagesToScan;
	}

	/**
	 * @return the spojoConfiguration
	 */
	protected SpojoConfiguration getSpojoConfiguration() {
		if (spojoConfiguration == null) {
			throw new SpojoException("Configuration not initialized yet");
		}
		return spojoConfiguration;
	}

	public void setSpojoConfiguration(final SpojoConfiguration spojoConfiguration) {
		this.spojoConfiguration = spojoConfiguration;
	}
}
