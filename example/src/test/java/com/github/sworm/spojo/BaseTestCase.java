package com.github.sworm.spojo;

/**
 *
 */

import static org.junit.Assert.*;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Vincent Palau
 * @Since Feb 27, 2011
 * 
 */

public abstract class BaseTestCase {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	public static void assertCollectionEquals(final Collection<?> expectedCollection, final Collection<?> actualCollection) {
		assertNotNull(expectedCollection);
		assertNotNull(actualCollection);
		assertEquals(expectedCollection.size(), actualCollection.size());
		assertTrue(expectedCollection.containsAll(actualCollection));
		assertTrue(actualCollection.containsAll(expectedCollection));
	}
}
