package com.github.sworm.spojo.config;

import static com.github.sworm.spojo.enums.RuleType.DISABLED;
import static com.github.sworm.spojo.enums.RuleType.EXCLUDE;
import static com.github.sworm.spojo.enums.RuleType.INCLUDE;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.junit.Test;

import com.github.sworm.spojo.BaseTestCase;
import com.github.sworm.spojo.annotations.Rule;
import com.github.sworm.spojo.annotations.Rules;
import com.github.sworm.spojo.data.BasicProperty;
import com.github.sworm.spojo.enums.RuleType;
import com.github.sworm.spojo.exceptions.RuleNotFoundException;
import com.github.sworm.spojo.exceptions.SpojoException;

public class SpojoConfigurationTest extends BaseTestCase {

	public static class TestClass1 {
		private String a, b, c;

		/**
		 * @return the a
		 */
		public String getA() {
			return a;
		}

		/**
		 * @param a
		 *            the a to set
		 */
		public void setA(final String a) {
			this.a = a;
		}

		/**
		 * @return the b
		 */
		public String getB() {
			return b;
		}

		/**
		 * @param b
		 *            the b to set
		 */
		public void setB(final String b) {
			this.b = b;
		}

		/**
		 * @return the c
		 */
		public String getC() {
			return c;
		}

		/**
		 * @param c
		 *            the c to set
		 */
		public void setC(final String c) {
			this.c = c;
		}
	}

	@Rules({// @formatter:off
		@Rule(name = "rule1", type = EXCLUDE, properties = { "a", "c" }),
		@Rule(name = "rule2", type = INCLUDE, properties = "c"),
		@Rule(name = "rule3", extendsFrom = "rule2", properties = { "b" })
	// @formatter:on
	})
	public static class TestClass2 extends TestClass1 {
	}

	@Rules({// @formatter:off
		@Rule(name = "zero"),
		@Rule(name = "one", type = INCLUDE, properties = "1"),
		@Rule(name = "two", extendsFrom = "one",  type = INCLUDE, properties = { "2" })
	// @formatter:on
	})
	public static class TestClass3 extends TestClass2 {
	}

	@Rule(name = "alpha", type = EXCLUDE, extendsFrom = "any", properties = { "x", "y", "z" })
	public static class TestClass4 extends TestClass3 {
	}

	public static class TestClass5 extends TestClass4 {

	}

	// due to an implementation of an Annotation for simplifying the test
	// we are forced to include the SuppressWarning
	@SuppressWarnings("all")
	public static class RuleAnn implements Rule {

		private String name = null;
		private RuleType type = DISABLED;
		private String[] properties = {};
		private String[] extendsFrom = {};

		private RuleAnn(final String name, final RuleType type, final String... properties) {
			this.name = name;
			this.type = type;
			this.properties = properties;
		}

		private RuleAnn(final String name, final RuleType type, final String[] extendsFrom, final String... properties) {
			this.name = name;
			this.type = type;
			this.properties = properties;
			this.extendsFrom = extendsFrom;
		}

		public Class<? extends Annotation> annotationType() {
			return Rule.class;
		}

		public String[] extendsFrom() {
			return extendsFrom;
		}

		public String name() {
			return name;
		}

		public String[] properties() {
			return properties;
		}

		public RuleType type() {
			return type;
		}

	}

	@Test(expected = RuleNotFoundException.class)
	public void getmetadata() {
		SpojoConfiguration configuration = new SpojoConfiguration();
		configuration.getRuleMetadata("pp");
	}

	@Test
	public void getmetadata2() {
		SpojoConfiguration configuration = new SpojoConfiguration();
		RuleMetadata metadata1 = new RuleMetadataImpl("c1", INCLUDE, new String[] { "c4" }, new String[] { "Z1", "B1", "C1" });

		Map<String, RuleMetadata> ruleNameMap = new HashMap<String, RuleMetadata>();
		ruleNameMap.put(metadata1.getName(), metadata1);
		configuration.setRuleMapByName(ruleNameMap);

		assertNotNull(configuration.getRuleMetadata("c1"));
	}

	@Test
	public void testAddClass1() {
		SpojoConfiguration configuration = new SpojoConfiguration();
		configuration.addClass(TestClass1.class);

		assertTrue(configuration.getRuleMapByName().isEmpty());
	}

	// @Test
	// public void testAddClass2() {
	// SpojoConfiguration configuration = new SpojoConfiguration();
	// configuration.addClass(TestClass2.class);
	//
	// Map<String, RuleMetadata> map = configuration.getRuleMapByName();
	// assertNotNull(map);
	// assertFalse(map.isEmpty());
	// assertEquals(3, map.size());
	//
	// Collection<String> extendsFromSet = new TreeSet<String>();
	//
	// String w1 = "rule1";
	// RuleMetadata expected = map.get(w1);
	// assertEquals(w1, expected.getName());
	// assertCollectionEquals(extendsFromSet, expected.getExtendsFrom());
	// assertEquals(new BasicProperty("rule1").add("a,c"), expected.getProperty());
	// assertEquals(EXCLUDE, expected.getType());
	//
	// String w2 = "rule2";
	// expected = map.get(w2);
	// assertEquals(w2, expected.getName());
	// assertCollectionEquals(extendsFromSet, expected.getExtendsFrom());
	// assertEquals(new BasicProperty("rule2").add("c"), expected.getProperty());
	// assertEquals(INCLUDE, expected.getType());
	//
	// extendsFromSet.add(w2);
	//
	// String w3 = "rule3";
	// expected = map.get(w3);
	// assertEquals(w3, expected.getName());
	// assertCollectionEquals(extendsFromSet, expected.getExtendsFrom());
	// assertEquals(new BasicProperty("rule3").add("b"), expected.getProperty());
	// assertEquals(DISABLED, expected.getType());
	// }
	//
	// @Test
	// public void testAddClass3() {
	// SpojoConfiguration configuration = new SpojoConfiguration();
	// configuration.addClass(TestClass3.class);
	//
	// Map<String, RuleMetadata> map = configuration.getRuleMapByName();
	//
	// assertNotNull(map);
	// assertFalse(map.isEmpty());
	// assertEquals(3, map.size());
	//
	// TreeSet<String> extendsFromSet = new TreeSet<String>();
	// TreeSet<String> propSet = new TreeSet<String>();
	//
	// String name = "zero";
	// RuleMetadata expected = map.get(name);
	// assertEquals(name, expected.getName());
	// assertCollectionEquals(extendsFromSet, expected.getExtendsFrom());
	// assertEquals(new BasicProperty("zero"), expected.getProperty());
	// assertEquals(DISABLED, expected.getType());
	//
	// propSet.add("1");
	//
	// String n1 = "one";
	// expected = map.get(n1);
	// assertEquals(n1, expected.getName());
	// assertCollectionEquals(extendsFromSet, expected.getExtendsFrom());
	// assertEquals(new BasicProperty("one").add("1"), expected.getProperty());
	// assertEquals(INCLUDE, expected.getType());
	//
	// extendsFromSet.add(n1);
	// propSet.add("2");
	//
	// String n2 = "two";
	// expected = map.get(n2);
	// assertEquals(n2, expected.getName());
	// assertCollectionEquals(extendsFromSet, expected.getExtendsFrom());
	// assertEquals(new BasicProperty("two").add("1,2"), expected.getProperty());
	// assertEquals(INCLUDE, expected.getType());
	//
	// }

	@Test
	public void testAddRule1() {
		List<Rule> ruleList = new ArrayList<Rule>();

		Rule rule1 = new RuleAnn("n1", INCLUDE, "a", "b", "c");
		ruleList.add(rule1);

		SpojoConfiguration configuration = new SpojoConfiguration();
		configuration.addRule(ruleList);
		Map<String, RuleMetadata> map = configuration.getRuleMapByName();

		assertNotNull(map);
		assertFalse(map.isEmpty());
		assertEquals(1, map.size());

		TreeSet<String> extendsFromSet = new TreeSet<String>(Arrays.asList(rule1.extendsFrom()));

		RuleMetadata expected = map.get(rule1.name());
		assertEquals(rule1.name(), expected.getName());
		assertCollectionEquals(extendsFromSet, expected.getExtendsFrom());
		assertEquals(new BasicProperty("n1").add("a,b,c"), expected.getProperty());
		assertEquals(rule1.type(), expected.getType());
	}

	@Test(expected = SpojoException.class)
	public void testAddRuleNull() {
		SpojoConfiguration configuration = new SpojoConfiguration();
		configuration.addRule(null);
	}

	@Test(expected = SpojoException.class)
	public void testAddRuleEmpty() {
		SpojoConfiguration configuration = new SpojoConfiguration();
		configuration.addRule(new ArrayList<Rule>());
	}

	@Test(expected = SpojoException.class)
	public void testAddClassNull() {
		SpojoConfiguration configuration = new SpojoConfiguration();
		configuration.addClass(null);
	}

	@Test
	public void testAddClass5WithOneAnnotation() {
		SpojoConfiguration configuration = new SpojoConfiguration();
		configuration.addClass(TestClass4.class);

		Map<String, RuleMetadata> map = configuration.getRuleMapByName();

		assertNotNull(map);
		assertFalse(map.isEmpty());
		assertEquals(1, map.size());
		// @Rule(name = "alpha", type = EXCLUDE, extendsFrom = "any", properties = { "x", "y", "z" })
		TreeSet<String> extendsFromSet = new TreeSet<String>();
		extendsFromSet.add("any");

		String name = "alpha";
		RuleMetadata expected = map.get(name);
		assertEquals(name, expected.getName());
		assertCollectionEquals(extendsFromSet, expected.getExtendsFrom());
		assertEquals(new BasicProperty("alpha").add("x,y,z"), expected.getProperty());
		assertEquals(EXCLUDE, expected.getType());
	}

	@Test(expected = SpojoException.class)
	public void testAddRuleMetadata1() {
		SpojoConfiguration configuration = new SpojoConfiguration();
		configuration.addRuleMetadata((RuleMetadata) null);
	}

	@Test(expected = SpojoException.class)
	public void testAddRuleMetadata2() {
		SpojoConfiguration configuration = new SpojoConfiguration();
		configuration.addRuleMetadata((List<RuleMetadata>) null);
	}

	@Test
	public void testAddRuleMetadata5() {
		SpojoConfiguration configuration = new SpojoConfiguration();
		RuleMetadata metadata1 = new RuleMetadataImpl("c1", INCLUDE, new String[] { "c4" }, new String[] { "Z1", "B1", "C1" });
		configuration.addRuleMetadata(metadata1);

		Map<String, RuleMetadata> map = configuration.getRuleMapByName();

		assertNotNull(map);
		assertFalse(map.isEmpty());
		assertEquals(1, map.size());

		TreeSet<String> extendsFromSet = new TreeSet<String>();
		extendsFromSet.add("c4");

		String name = "c1";
		RuleMetadata expected = map.get(name);
		assertEquals(name, expected.getName());
		assertCollectionEquals(extendsFromSet, expected.getExtendsFrom());
		assertEquals(new BasicProperty(name).add("Z1,B1,C1"), expected.getProperty());
		assertEquals(INCLUDE, expected.getType());

	}

	@Test
	public void testAddRuleMetadata6() {
		SpojoConfiguration configuration = new SpojoConfiguration();

		List<RuleMetadata> list = new ArrayList<RuleMetadata>();
		RuleMetadata metadata1 = new RuleMetadataImpl("c1", INCLUDE, new String[] { "c4" }, new String[] { "Z1", "B1", "C1" });
		RuleMetadata metadata2 = new RuleMetadataImpl("c2", INCLUDE, new String[] { "c1" }, new String[] { "X2", "Y2" });
		RuleMetadata metadata3 = new RuleMetadataImpl("c3", INCLUDE, new String[] { "c1", "c2" }, new String[] { "P3" });
		RuleMetadata metadata4 = new RuleMetadataImpl("c4", INCLUDE, new String[] { "c3" }, new String[] { "Q3" });

		list.add(metadata1);
		list.add(metadata2);
		list.add(metadata3);
		list.add(metadata4);

		configuration.addRuleMetadata(list);

		Map<String, RuleMetadata> map = configuration.getRuleMapByName();

		assertNotNull(map);
		assertFalse(map.isEmpty());
		assertEquals(4, map.size());

		TreeSet<String> extendsFromSet = new TreeSet<String>();
		extendsFromSet.add("c4");

		String name = "c1";
		RuleMetadata expected = map.get(name);
		assertEquals(name, expected.getName());
		assertCollectionEquals(extendsFromSet, expected.getExtendsFrom());
		assertEquals(new BasicProperty(name).add("B1,C1,P3,Q3,X2,Y2,Z1"), expected.getProperty());
		assertEquals(INCLUDE, expected.getType());

		extendsFromSet.clear();
		extendsFromSet.add("c1");

		name = "c2";
		expected = map.get(name);
		assertEquals(name, expected.getName());
		assertCollectionEquals(extendsFromSet, expected.getExtendsFrom());
		assertEquals(new BasicProperty(name).add("B1,C1,P3,Q3,X2,Y2,Z1"), expected.getProperty());
		assertEquals(INCLUDE, expected.getType());

		extendsFromSet.add("c2");

		name = "c3";
		expected = map.get(name);
		assertEquals(name, expected.getName());
		assertCollectionEquals(extendsFromSet, expected.getExtendsFrom());
		assertEquals(new BasicProperty(name).add("B1,C1,P3,Q3,X2,Y2,Z1"), expected.getProperty());

		assertEquals(INCLUDE, expected.getType());

		extendsFromSet.clear();
		extendsFromSet.add("c3");

		name = "c4";
		expected = map.get(name);
		assertEquals(name, expected.getName());
		assertCollectionEquals(extendsFromSet, expected.getExtendsFrom());
		assertEquals(new BasicProperty(name).add("X2,Y2,Z1,B1,C1,P3,Q3"), expected.getProperty());
		assertEquals(INCLUDE, expected.getType());

	}

	@Test
	public void testConvertAnnotation2Impl() {
		SpojoConfiguration configuration = new SpojoConfiguration();
		Rule rule1 = new RuleAnn("n1", INCLUDE, new String[] { "a", "b", "c" }, new String[] { "n2,n3" });
		RuleMetadata metadata1 = configuration.convertAnnotation2Impl(rule1);

		assertNotSame(rule1, metadata1);
		assertEquals(rule1.name(), metadata1.getName());
		assertEquals(rule1.type(), metadata1.getType());
		// assertArrayEquals(rule1.properties(), metadata1.getProperties().toArray());
		assertNotNull(metadata1.getProperty());
		assertTrue(metadata1.hasInheritance());
		assertArrayEquals(rule1.extendsFrom(), metadata1.getExtendsFrom().toArray());

		Rule rule2 = new RuleAnn("n2", EXCLUDE, new String[] { "b" });
		RuleMetadata metadata2 = configuration.convertAnnotation2Impl(rule2);

		assertNotSame(rule2, metadata1);
		assertEquals(rule2.name(), metadata2.getName());
		assertEquals(rule2.type(), metadata2.getType());
		assertNotNull(metadata2.getProperty());
		assertFalse(metadata2.hasInheritance());
		assertArrayEquals(rule2.extendsFrom(), metadata2.getExtendsFrom().toArray());

	}

	@Test(expected = RuleNotFoundException.class)
	public void testGetRuleMetadata1() {
		SpojoConfiguration configuration = new SpojoConfiguration();
		configuration.getRuleMetadata(null);
		fail("an Exception must be thrown before");
	}

	@Test
	public void testGetRuleMetadata2() {
		SpojoConfiguration configuration = new SpojoConfiguration();
		RuleMetadata metadata1 = new RuleMetadataImpl("c1", EXCLUDE, new String[] { "c9" }, new String[] { "Z1", "B1", "C1" });
		RuleMetadata metadata9 = new RuleMetadataImpl("c9", EXCLUDE, new String[] { "c9" }, new String[] { "E1", "R1", "T1" });
		Map<String, RuleMetadata> ruleNameMap = new HashMap<String, RuleMetadata>();
		ruleNameMap.put(metadata1.getName(), metadata1);
		ruleNameMap.put(metadata9.getName(), metadata9);

		configuration.setRuleMapByName(ruleNameMap);

		assertEquals(metadata1, configuration.getRuleMetadata("c1"));
		assertEquals(metadata9, configuration.getRuleMetadata("c9"));
	}

	@Test(expected = SpojoException.class)
	public void testGetRuleMetadata3() {
		SpojoConfiguration configuration = new SpojoConfiguration();
		configuration.setRuleMapByName(null);
		configuration.getRuleMetadata(null);
		fail("an Exception must be thrown before");
	}

	@Test
	public void testIterateExtendsFrom1() {
		SpojoConfiguration configuration = new SpojoConfiguration();
		configuration.iterateExtendsFrom(null, null, null);
	}

	@Test
	public void testIterateExtendsFrom2() {
		SpojoConfiguration configuration = new SpojoConfiguration();
		RuleMetadata metadata1 = new RuleMetadataImpl("c1", EXCLUDE, new String[] {}, new String[] { "Z1", "B1", "C1" });
		configuration.iterateExtendsFrom(metadata1, null, null);
	}

	@Test(expected = SpojoException.class)
	public void testIterateExtendsFrom3() {
		SpojoConfiguration configuration = new SpojoConfiguration();
		RuleMetadata metadata1 = new RuleMetadataImpl("c1", EXCLUDE, new String[] { "c9" }, new String[] { "Z1", "B1", "C1" });
		configuration.iterateExtendsFrom(metadata1, null, null);
	}

	@Test(expected = SpojoException.class)
	public void testIterateExtendsFrom4() {
		SpojoConfiguration configuration = new SpojoConfiguration();
		RuleMetadata metadata1 = new RuleMetadataImpl("c1", EXCLUDE, new String[] { "c9" }, new String[] { "Z1", "B1", "C1" });
		Map<String, RuleMetadata> ruleNameMap = new HashMap<String, RuleMetadata>();
		ruleNameMap.put(metadata1.getName(), metadata1);
		configuration.iterateExtendsFrom(metadata1, ruleNameMap, null);
	}

	@Test
	public void testIterateExtendsFrom5() {
		SpojoConfiguration configuration = new SpojoConfiguration();
		RuleMetadata metadata1 = new RuleMetadataImpl("c1", EXCLUDE, new String[] { "c9" }, new String[] { "Z1", "B1", "C1" });
		RuleMetadata metadata9 = new RuleMetadataImpl("c9", EXCLUDE, new String[] { "c9" }, new String[] { "E1", "R1", "T1" });
		Map<String, RuleMetadata> ruleNameMap = new HashMap<String, RuleMetadata>();
		ruleNameMap.put(metadata1.getName(), metadata1);
		ruleNameMap.put(metadata9.getName(), metadata9);

		HashSet<String> processing = new HashSet<String>();
		TreeSet<String> extendsFromSet = new TreeSet<String>();
		extendsFromSet.add("c9");

		configuration.iterateExtendsFrom(metadata1, ruleNameMap, processing);
		assertCollectionEquals(extendsFromSet, processing);

		RuleMetadata tmpRuleMetadata = ruleNameMap.get(metadata1.getName());
		assertCollectionEquals(extendsFromSet, tmpRuleMetadata.getExtendsFrom());
		assertEquals(new BasicProperty("c1").add("Z1,B1,C1,E1,R1,T1"), tmpRuleMetadata.getProperty());
		assertTrue(tmpRuleMetadata.hasInheritance());
		assertEquals(EXCLUDE, tmpRuleMetadata.getType());

		extendsFromSet.clear();
		processing = new HashSet<String>();
		configuration.iterateExtendsFrom(metadata9, ruleNameMap, processing);
		assertCollectionEquals(extendsFromSet, processing);

		extendsFromSet.add("c9");
		tmpRuleMetadata = ruleNameMap.get(metadata9.getName());
		assertCollectionEquals(extendsFromSet, tmpRuleMetadata.getExtendsFrom());
		assertEquals(new BasicProperty("c9").add("E1,R1,T1"), tmpRuleMetadata.getProperty());
		assertTrue(tmpRuleMetadata.hasInheritance());
		assertEquals(EXCLUDE, tmpRuleMetadata.getType());

	}

	@Test
	public void testProcessExtendedRules1() {
		SpojoConfiguration configuration = new SpojoConfiguration();
		configuration.processExtendedRules(null);
	}

	@Test
	public void testProcessExtendedRules2() {
		SpojoConfiguration configuration = new SpojoConfiguration();
		Map<String, RuleMetadata> ruleNameMap = new HashMap<String, RuleMetadata>();
		configuration.processExtendedRules(ruleNameMap);

		assertTrue(ruleNameMap.isEmpty());
	}

	@Test
	public void testProcessExtendedRules3() {
		SpojoConfiguration configuration = new SpojoConfiguration();
		RuleMetadata metadata1 = new RuleMetadataImpl("c1", EXCLUDE, new String[] {}, new String[] { "Z1", "B1", "C1" });
		RuleMetadata metadata2 = new RuleMetadataImpl("c2", INCLUDE, new String[] { "c1" }, new String[] { "X2", "Y2" });
		RuleMetadata metadata3 = new RuleMetadataImpl("c3", DISABLED, new String[] { "c1", "c2" }, new String[] { "P3" });
		RuleMetadata metadata4 = new RuleMetadataImpl("c4", DISABLED, new String[] { "c1", "c3" }, new String[] { "P4" });

		Map<String, RuleMetadata> ruleNameMap = new HashMap<String, RuleMetadata>();
		ruleNameMap.put(metadata1.getName(), metadata1);
		ruleNameMap.put(metadata2.getName(), metadata2);
		ruleNameMap.put(metadata3.getName(), metadata3);
		ruleNameMap.put(metadata4.getName(), metadata4);

		configuration.processExtendedRules(ruleNameMap);

		TreeSet<String> extendsFromSet = new TreeSet<String>();

		RuleMetadata tmpRuleMetadata = ruleNameMap.get(metadata1.getName());
		assertCollectionEquals(extendsFromSet, tmpRuleMetadata.getExtendsFrom());
		assertEquals(new BasicProperty("c1").add("Z1,B1,C1"), tmpRuleMetadata.getProperty());
		assertFalse(tmpRuleMetadata.hasInheritance());
		assertEquals(EXCLUDE, tmpRuleMetadata.getType());

		extendsFromSet.add("c1");
		tmpRuleMetadata = ruleNameMap.get(metadata2.getName());
		assertCollectionEquals(extendsFromSet, tmpRuleMetadata.getExtendsFrom());
		assertEquals(new BasicProperty("c2").add("X2,Y2"), tmpRuleMetadata.getProperty());
		assertTrue(tmpRuleMetadata.hasInheritance());
		assertEquals(INCLUDE, tmpRuleMetadata.getType());

		extendsFromSet.add("c2");
		tmpRuleMetadata = ruleNameMap.get(metadata3.getName());
		assertCollectionEquals(extendsFromSet, tmpRuleMetadata.getExtendsFrom());
		assertEquals(new BasicProperty("c3").add("P3"), tmpRuleMetadata.getProperty());
		assertTrue(tmpRuleMetadata.hasInheritance());
		assertEquals(DISABLED, tmpRuleMetadata.getType());

		extendsFromSet.clear();
		extendsFromSet.add("c1");
		extendsFromSet.add("c3");
		tmpRuleMetadata = ruleNameMap.get(metadata4.getName());
		assertCollectionEquals(extendsFromSet, tmpRuleMetadata.getExtendsFrom());
		assertEquals(new BasicProperty("c4").add("P3,P4"), tmpRuleMetadata.getProperty());
		assertTrue(tmpRuleMetadata.hasInheritance());
		assertEquals(DISABLED, tmpRuleMetadata.getType());

	}

	@Test
	public void testProcessExtendsRules() {
		SpojoConfiguration configuration = new SpojoConfiguration();
		RuleMetadata metadata1 = new RuleMetadataImpl("c1", INCLUDE, new String[] {}, new String[] { "Z1", "B1", "C1" });
		RuleMetadata metadata2 = new RuleMetadataImpl("c2", INCLUDE, new String[] { "c1" }, new String[] { "X2", "Y2" });
		RuleMetadata metadata3 = new RuleMetadataImpl("c3", INCLUDE, new String[] { "c1", "c2" }, new String[] { "P3" });
		RuleMetadata metadata4 = new RuleMetadataImpl("c4", EXCLUDE, new String[] { "c3" }, new String[] { "T4" });

		Map<String, RuleMetadata> ruleNameMap = new HashMap<String, RuleMetadata>();
		ruleNameMap.put(metadata1.getName(), metadata1);
		ruleNameMap.put(metadata2.getName(), metadata2);
		ruleNameMap.put(metadata3.getName(), metadata3);
		ruleNameMap.put(metadata4.getName(), null);

		configuration.processExtendedRules(ruleNameMap);

		assertEquals(3, metadata1.getProperty().getProperties().size());
		assertEquals(5, metadata2.getProperty().getProperties().size());
		assertEquals(6, metadata3.getProperty().getProperties().size());
		assertEquals(1, metadata4.getProperty().getProperties().size());
	}

	@Test
	public void testProcessExtendsRules2() {
		SpojoConfiguration configuration = new SpojoConfiguration();
		RuleMetadata metadata1 = new RuleMetadataImpl("c1", INCLUDE, new String[] { "c4" }, new String[] { "Z1", "B1", "C1" });
		RuleMetadata metadata2 = new RuleMetadataImpl("c2", INCLUDE, new String[] { "c1" }, new String[] { "X2", "Y2" });
		RuleMetadata metadata3 = new RuleMetadataImpl("c3", INCLUDE, new String[] { "c1", "c2" }, new String[] { "P3" });
		RuleMetadata metadata4 = new RuleMetadataImpl("c4", INCLUDE, new String[] { "c3" }, new String[] { "Q3" });

		Map<String, RuleMetadata> ruleNameMap = new HashMap<String, RuleMetadata>();
		ruleNameMap.put(metadata1.getName(), metadata1);
		ruleNameMap.put(metadata2.getName(), metadata2);
		ruleNameMap.put(metadata3.getName(), metadata3);
		ruleNameMap.put(metadata4.getName(), metadata4);

		configuration.processExtendedRules(ruleNameMap);

		assertEquals(7, metadata1.getProperty().getProperties().size());
		assertEquals(7, metadata2.getProperty().getProperties().size());
		assertEquals(7, metadata3.getProperty().getProperties().size());
		assertEquals(7, metadata4.getProperty().getProperties().size());
	}

	@Test
	public void testSetRuleNameByClazzMap() {
		SpojoConfiguration configuration = new SpojoConfiguration();
		Map<String, RuleMetadata> ruleByName = new HashMap<String, RuleMetadata>();
		configuration.setRuleMapByName(ruleByName);
		assertEquals(ruleByName, configuration.getRuleMapByName());
	}
}
