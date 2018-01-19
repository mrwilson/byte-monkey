/**
 * 
 */
package com.github.sworm.spojo.utils;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;
import com.github.sworm.spojo.BaseTestCase;
import com.github.sworm.spojo.data.BasicProperty;
import com.github.sworm.spojo.data.Property;
import org.springframework.beans.BeanInstantiationException;

import com.google.gson.Gson;

/**
 * @author albert
 * @Since 07/03/2011
 * 
 */
public class SpojoUtilsTest extends BaseTestCase {

	public static class Probe {
		String prop1;
		String prop2;
		Probe probe;
		List<String> listaStr;
		Set<String> setStr;
		ArrayList<String> arrayListStr;
		SortedSet<String> sortedSetStr;

		List<Probe> listaProbe;
		String[] arrStr;
		int[] arrInt;
		Probe[] arrProbe;

		public Probe() {
		}

		/**
		 * @return the prop1
		 */
		public String getProp1() {
			return prop1;
		}

		/**
		 * @param prop1
		 *            the prop1 to set
		 */
		public void setProp1(final String prop1) {
			this.prop1 = prop1;
		}

		/**
		 * @return the prop2
		 */
		public String getProp2() {
			return prop2;
		}

		/**
		 * @param prop2
		 *            the prop2 to set
		 */
		public void setProp2(final String prop2) {
			this.prop2 = prop2;
		}

		/**
		 * @return the probe
		 */
		public Probe getProbe() {
			return probe;
		}

		/**
		 * @param probe
		 *            the probe to set
		 */
		public void setProbe(final Probe probe) {
			this.probe = probe;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return new Gson().toJson(this);
		}

		/**
		 * @return the listaStr
		 */
		public List<String> getListaStr() {
			return listaStr;
		}

		/**
		 * @param listaStr
		 *            the listaStr to set
		 */
		public void setListaStr(final List<String> listaStr) {
			this.listaStr = listaStr;
		}

		/**
		 * @return the setStr
		 */
		public Set<String> getSetStr() {
			return setStr;
		}

		/**
		 * @param setStr
		 *            the setStr to set
		 */
		public void setSetStr(final Set<String> setStr) {
			this.setStr = setStr;
		}

		/**
		 * @return the sortedSetStr
		 */
		public SortedSet<String> getSortedSetStr() {
			return sortedSetStr;
		}

		/**
		 * @param sortedSetStr
		 *            the sortedSetStr to set
		 */
		public void setSortedSetStr(final SortedSet<String> sortedSetStr) {
			this.sortedSetStr = sortedSetStr;
		}

		/**
		 * @return the arrayListStr
		 */
		public ArrayList<String> getArrayListStr() {
			return arrayListStr;
		}

		/**
		 * @param arrayListStr
		 *            the arrayListStr to set
		 */
		public void setArrayListStr(final ArrayList<String> arrayListStr) {
			this.arrayListStr = arrayListStr;
		}

		/**
		 * @return the listaProbe
		 */
		public List<Probe> getListaProbe() {
			return listaProbe;
		}

		/**
		 * @param listaProbe
		 *            the listaProbe to set
		 */
		public void setListaProbe(final List<Probe> listaProbe) {
			this.listaProbe = listaProbe;
		}

		/**
		 * @return the arrStr
		 */
		public String[] getArrStr() {
			return arrStr;
		}

		/**
		 * @param arrStr
		 *            the arrStr to set
		 */
		public void setArrStr(final String[] arrStr) {
			this.arrStr = arrStr;
		}

		/**
		 * @return the arrInt
		 */
		public int[] getArrInt() {
			return arrInt;
		}

		/**
		 * @param arrInt
		 *            the arrInt to set
		 */
		public void setArrInt(final int[] arrInt) {
			this.arrInt = arrInt;
		}

		/**
		 * @return the arrProbe
		 */
		public Probe[] getArrProbe() {
			return arrProbe;
		}

		/**
		 * @param arrProbe
		 *            the arrProbe to set
		 */
		public void setArrProbe(final Probe[] arrProbe) {
			this.arrProbe = arrProbe;
		}

	}

	public static class Probe2 extends Probe {
		private Probe2() {
		}
	}

	public static class Probe3 extends Probe {
		String prop3;

		/**
		 * @return the prop3
		 */
		public String getProp3() {
			return prop3;
		}

		/**
		 * @param prop3
		 *            the prop3 to set
		 */
		public void setProp3(final String prop3) {
			this.prop3 = prop3;
		}
	}

	public static class Probe4 extends Probe {
		private String privateString;

		/**
		 * @return the privateString
		 */
		@SuppressWarnings("unused")
		private String getPrivateString() {
			return privateString;
		}

		/**
		 * @param privateString
		 *            the privateString to set
		 */
		@SuppressWarnings("unused")
		private void setPrivateString(final String privateString) {
			this.privateString = privateString;
		}

	}

	@Test
	public void instantiate1() {
		Probe probe = SpojoUtils.instantiate(SpojoUtilsTest.Probe.class);
		assertNotNull(probe);
	}

	@Test(expected = BeanInstantiationException.class)
	public void instantiate2() {
		Probe2 probe = SpojoUtils.instantiate(SpojoUtilsTest.Probe2.class);
		assertNotNull(probe);
	}

	private static Probe createProbe(final String def) {
		return new Gson().fromJson(def, Probe.class);
	}

	@Test
	public void copyPropertieswithExclude1() {
		Probe source = createProbe("{prop1:'propiedad1',prop2:'propiedad2'}");
		Probe target = createProbe("{}");
		BasicProperty root = new BasicProperty("root");
		root.add(new BasicProperty("prop1"));

		SpojoUtils.copyPropertiesWithExclude(source, target, root);

		assertEquals(null, target.getProp1());
		assertEquals("propiedad2", target.getProp2());

	}

	@Test
	public void copyPropertieswithExclude2() {
		Probe source = createProbe("{prop1:'propiedad1',prop2:'propiedad2'}");
		Probe target = createProbe("{}");
		Property<String> root = new BasicProperty("root").add("prop1,prop2");

		SpojoUtils.copyPropertiesWithExclude(source, target, root);
		assertEquals(null, target.getProp1());
		assertEquals(null, target.getProp2());

	}

	@Test
	public void copyPropertieswithExclude3() {
		Probe source = createProbe("{prop1:'propiedad1',prop2:'propiedad2'}");
		Probe target = createProbe("{prop1:'propiedad11',prop2:'propiedad22'}");

		SpojoUtils.copyPropertiesWithExclude(source, target, new BasicProperty("root"));
		assertEquals("propiedad1", target.getProp1());
		assertEquals("propiedad2", target.getProp2());

	}

	@Test
	public void copyPropertieswithExclude4() {
		Probe source = createProbe("{prop1:'propiedad1',prop2:'propiedad2'}");
		Probe target = createProbe("{}");

		Property<String> root = new BasicProperty("root").add("prop1222,prop22222");

		SpojoUtils.copyPropertiesWithExclude(source, target, root);
		assertEquals("propiedad1", target.getProp1());
		assertEquals("propiedad2", target.getProp2());

	}

	@Test
	public void copyPropertieswithExclude5() {
		Probe source = createProbe("{prop1:'propiedad1',prop2:'propiedad2'}");
		Probe target = createProbe("{}");

		SpojoUtils.copyPropertiesWithExclude(source, target, null);

		assertEquals("propiedad1", target.getProp1());
		assertEquals("propiedad2", target.getProp2());
	}

	@Test(expected = IllegalArgumentException.class)
	public void copyPropertieswithExclude6() {
		// Probe source = createProbe("{prop1:'propiedad1',prop2:'propiedad2'}");
		Probe target = createProbe("{}");

		SpojoUtils.copyPropertiesWithExclude(null, target, new BasicProperty("root"));

	}

	@Test(expected = IllegalArgumentException.class)
	public void copyPropertieswithExclude7() {
		Probe source = createProbe("{prop1:'propiedad1',prop2:'propiedad2'}");
		// Probe target = createProbe("{}");

		SpojoUtils.copyPropertiesWithExclude(source, null, new BasicProperty("root"));

	}

	@Test
	public void copyPropertiesWithInclude1() {
		Probe source = createProbe("{prop1:'propiedad1',prop2:'propiedad2'}");
		Probe target = createProbe("{prop1:'propiedad11',prop2:'propiedad22'}");

		Property<String> root = new BasicProperty("root").add("prop1");

		SpojoUtils.copyPropertiesWithInclude(source, target, root);
		assertEquals("propiedad1", target.getProp1());
		assertEquals("propiedad22", target.getProp2());

	}

	@Test
	public void copyPropertiesWithInclude2() {
		Probe source = createProbe("{prop1:'propiedad1',prop2:'propiedad2'}");
		Probe target = createProbe("{prop1:'propiedad11',prop2:'propiedad22'}");

		BasicProperty root = new BasicProperty("root");

		SpojoUtils.copyPropertiesWithInclude(source, target, root);
		assertEquals("propiedad11", target.getProp1());
		assertEquals("propiedad22", target.getProp2());

	}

	@Test
	public void copyPropertiesWithInclude3() {
		Probe source = createProbe("{prop1:'propiedad1',prop2:'propiedad2'}");
		Probe target = createProbe("{prop1:'propiedad11',prop2:'propiedad22'}");

		Property<String> root = new BasicProperty("root").add("prop111");

		SpojoUtils.copyPropertiesWithInclude(source, target, root);
		assertEquals("propiedad11", target.getProp1());
		assertEquals("propiedad22", target.getProp2());

	}

	@Test
	public void copyPropertieswithInclude4() {
		Probe source = createProbe("{prop1:'propiedad1',prop2:'propiedad2'}");
		Probe target = createProbe("{}");

		SpojoUtils.copyPropertiesWithInclude(source, target, null);

		assertEquals(null, target.getProp1());
		assertEquals(null, target.getProp2());
	}

	@Test
	public void copyPropertiesInner() {
		Probe source = createProbe("{prop1:'propiedad1',prop2:'propiedad2',probe:{prop1:'prop1',prop2:'prop2'}}");
		Probe target = createProbe("{}");

		SpojoUtils.copyPropertiesWithExclude(source, target, null);

		assertNotNull(target.getProbe());
		assertNotSame(source.getProbe(), target.getProbe());
		assertEquals("prop1", target.getProbe().getProp1());
		assertEquals("prop2", target.getProbe().getProp2());
	}

	@Test
	public void copyPropertiesInnerExclude() {
		Probe source = createProbe("{prop1:'propiedad1',prop2:'propiedad2',probe:{prop1:'prop1',prop2:'prop2'}}");
		Probe target = createProbe("{}");

		Property<String> root = new BasicProperty("root").add("probe.prop1");

		SpojoUtils.copyPropertiesWithExclude(source, target, root);

		assertNotNull(target.getProbe());
		assertNotSame(source.getProbe(), target.getProbe());
		assertEquals(null, target.getProbe().getProp1());
		assertEquals("prop2", target.getProbe().getProp2());
	}

	@Test
	public void copyInclude2ndLevel1Property() {
		Probe source = createProbe("{prop1:'propiedad1',prop2:'propiedad2',probe:{prop1:'prop1',prop2:'prop2'}}");
		Probe target = createProbe("{}");
		Property<String> root = new BasicProperty("root").add("probe.prop1");

		SpojoUtils.copyPropertiesWithInclude(source, target, root);

		assertEquals(null, target.getProp1());
		assertEquals(null, target.getProp2());
		assertNotNull(target.getProbe());
		assertNotSame(source.getProbe(), target.getProbe());
		assertEquals("prop1", target.getProbe().getProp1());
		assertEquals(null, target.getProbe().getProp2());
	}

	@Test
	public void copyInclude2ndLevel2Property() {
		Probe source = createProbe("{prop1:'propiedad1',prop2:'propiedad2',probe:{prop1:'prop1',prop2:'prop2'}}");
		Probe target = createProbe("{}");
		Property<String> root = new BasicProperty("root").add("probe.prop1,probe.prop2");

		SpojoUtils.copyPropertiesWithInclude(source, target, root);

		assertEquals(null, target.getProp1());
		assertEquals(null, target.getProp2());
		assertNotNull(target.getProbe());
		assertNotSame(source.getProbe(), target.getProbe());
		assertEquals("prop1", target.getProbe().getProp1());
		assertEquals("prop2", target.getProbe().getProp2());
	}

	@Test
	public void copyInclude2ndLevelComplexProperty() {
		Probe source = createProbe("{prop1:'propiedad1',prop2:'propiedad2',probe:{prop1:'prop1',prop2:'prop2'}}");
		Probe target = createProbe("{}");
		Property<String> root = new BasicProperty("root").add("probe");

		SpojoUtils.copyProperties(source, target, root, true);

		assertEquals(null, target.getProp1());
		assertEquals(null, target.getProp2());
		assertNotNull(target.getProbe());
		assertNotSame(source.getProbe(), target.getProbe());
		assertEquals("prop1", target.getProbe().getProp1());
		assertEquals("prop2", target.getProbe().getProp2());

	}

	@Test
	public void copyExclude2ndLevelAllProperty() {
		Probe source = createProbe("{prop1:'propiedad1',prop2:'propiedad2',probe:{prop1:'prop1',prop2:'prop2'}}");
		Probe target = createProbe("{}");
		BasicProperty root = new BasicProperty("root");

		SpojoUtils.copyPropertiesWithExclude(source, target, root);

		assertEquals("propiedad1", target.getProp1());
		assertEquals("propiedad2", target.getProp2());
		assertNotNull(target.getProbe());
		assertNotSame(source.getProbe(), target.getProbe());
		assertEquals("prop1", target.getProbe().getProp1());
		assertEquals("prop2", target.getProbe().getProp2());
	}

	@Test
	public void inmutableClass() {
		assertTrue(SpojoUtils.isImmutable(""));
		assertTrue(SpojoUtils.isImmutable(new Date()));
		assertTrue(SpojoUtils.isImmutable(new java.sql.Date(new Date().getTime())));
		assertTrue(SpojoUtils.isImmutable(new Long(1)));
		assertTrue(SpojoUtils.isImmutable('a'));
		assertTrue(SpojoUtils.isImmutable(1));
		assertTrue(SpojoUtils.isImmutable((byte) 1));
		assertTrue(SpojoUtils.isImmutable(new BigInteger("1")));
		assertTrue(SpojoUtils.isImmutable(new BigDecimal("1")));
		assertTrue(SpojoUtils.isImmutable(null));

		assertFalse(SpojoUtils.isImmutable(new Probe()));
		SpojoUtils.addImmutableClass(Probe.class);
		assertTrue(SpojoUtils.isImmutable(new Probe()));

		SpojoUtils.defaultImmutableClasses.remove(Probe.class); // to avoid collateral effects in tests
		assertFalse("Removing an immutable classe don't work!", SpojoUtils.isImmutable(new Probe()));
	}

	@Test
	public void copyExcludeList() {

		List<Probe> source = new ArrayList<SpojoUtilsTest.Probe>();
		source.add(createProbe("{prop1:'propiedad1',prop2:'propiedad2',probe:{prop1:'prop1',prop2:'prop2'}}"));
		source.add(createProbe("{prop1:'2propiedad1',prop2:'2propiedad2',probe:{prop1:'2prop1',prop2:'2prop2'}}"));
		List<Probe> target = new ArrayList<SpojoUtilsTest.Probe>();

		BasicProperty root = new BasicProperty("root");

		SpojoUtils.copyPropertiesWithExclude(source, target, root);

		assertEquals(2, target.size());
		for (int i = 0; i < target.size(); i++) {
			Probe src = source.get(i);
			Probe tar = target.get(i);
			assertNotSame(src, tar);
		}
	}

	@Test
	public void copyExcludePropertiesListStr() {
		Probe source = createProbe("{listaStr:['s1','s2','s3']}");
		Probe target = createProbe("{}");

		BasicProperty root = new BasicProperty("root");
		SpojoUtils.copyPropertiesWithExclude(source, target, root);

		assertNotNull(target.getListaStr());
		assertEquals(3, target.getListaStr().size());

	}

	@Test
	public void copyExcludePropertiesListProbe() {
		Probe source = createProbe("{}");
		List<Probe> lista = new ArrayList<Probe>();
		lista.add(createProbe("{prop1:'prop1'}"));
		lista.add(createProbe("{prop2:'prop2'}"));
		source.setListaProbe(lista);
		Probe target = createProbe("{}");

		BasicProperty root = new BasicProperty("root");
		SpojoUtils.copyPropertiesWithExclude(source, target, root);

		assertNotNull(target.getListaProbe());
		assertEquals(lista.size(), target.getListaProbe().size());
		assertNotSame(lista, target.getListaProbe());

	}

	@Test
	public void copyExcludePropArrayStr() {
		Probe source = createProbe("{arrStr:['s1','s2','s3']}");
		Probe target = createProbe("{}");

		BasicProperty root = new BasicProperty("root");
		SpojoUtils.copyPropertiesWithExclude(source, target, root);

		assertNotNull(target.getArrStr());
		assertNotSame(source.getArrStr(), target.getArrStr());
		assertEquals(source.getArrStr().length, target.getArrStr().length);
		assertArrayEquals(source.getArrStr(), target.getArrStr());
	}

	@Test
	public void copyExcludePropArrayInt() {
		Probe source = createProbe("{arrInt:[1,2,3]}");
		Probe target = createProbe("{}");

		BasicProperty root = new BasicProperty("root");
		SpojoUtils.copyPropertiesWithExclude(source, target, root);

		assertNotNull(target.getArrInt());
		assertNotSame(source.getArrInt(), target.getArrInt());
		assertEquals(source.getArrInt().length, target.getArrInt().length);
		assertArrayEquals(source.getArrInt(), target.getArrInt());
	}

	@Test
	public void copyExcludePropArrayProbe() {
		Probe source = createProbe("{arrProbe:[{prop1:'prop1'},{prop2:'prop2'}]}");
		Probe target = createProbe("{}");

		BasicProperty root = new BasicProperty("root");
		SpojoUtils.copyPropertiesWithExclude(source, target, root);

		assertNotNull(target.getArrProbe());
		assertNotSame(source.getArrProbe(), target.getArrProbe());
		assertEquals(source.getArrProbe().length, target.getArrProbe().length);
		for (int i = 0; i < source.getArrProbe().length; i++) {
			Probe src = source.getArrProbe()[i];
			assertEquals(src.toString(), target.getArrProbe()[i].toString());
		}
	}

	@Test
	public void copyPropArrayStr() {
		String[] source = { "1", "2", "3" };

		String[] target = new String[3];

		BasicProperty root = new BasicProperty("root");
		SpojoUtils.copyPropertiesWithExclude(source, target, root);

		assertArrayEquals(source, target);
	}

	@Test
	public void copyPropArrayInt() {
		int[] source = { 1, 2, 3 };

		int[] target = new int[source.length];

		BasicProperty root = new BasicProperty("root");
		SpojoUtils.copyPropertiesWithExclude(source, target, root);

		assertArrayEquals(source, target);
	}

	@Test
	public void copyPropArrayProbe() {
		Probe[] source = { createProbe("{}"), createProbe("{}"), createProbe("{}") };

		Probe[] target = new Probe[source.length];

		BasicProperty root = new BasicProperty("root");
		SpojoUtils.copyProperties(source, target, root, false);

		assertEquals(source.length, target.length);
	}

	@Test
	public void instantiateArray() {
		String[] arr = (String[]) SpojoUtils.instantiateArray(String.class, 3);
		assertNotNull(arr);
		assertEquals(3, arr.length);

		int[] arrint = (int[]) SpojoUtils.instantiateArray(int.class, 3);
		assertNotNull(arrint);
		assertEquals(3, arrint.length);

		Probe[] arrProbe = (Probe[]) SpojoUtils.instantiateArray(Probe.class, 3);
		assertNotNull(arrProbe);
		assertEquals(3, arrProbe.length);
	}

	@Test
	public void findClass() throws SecurityException, NoSuchMethodException {
		Method writeMethod = Probe.class.getMethod("setListaStr", List.class);
		Class<?> clazz = SpojoUtils.findClazztype2instantiate(writeMethod, null);
		assertEquals(ArrayList.class, clazz);

		Method writeMethod2 = Probe.class.getMethod("setSetStr", Set.class);
		Class<?> clazz2 = SpojoUtils.findClazztype2instantiate(writeMethod2, null);
		assertEquals(LinkedHashSet.class, clazz2);

		Method writeMethod3 = Probe.class.getMethod("setSortedSetStr", SortedSet.class);
		Class<?> clazz3 = SpojoUtils.findClazztype2instantiate(writeMethod3, null);
		assertEquals(TreeSet.class, clazz3);
	}

	@Test
	public void deriveClass() {
		Probe source = new Probe();
		Class<?> ret = SpojoUtils.deriveClassFromSource(source);
		assertEquals(Probe.class, ret);

		Probe[] arr = new Probe[0];
		ret = SpojoUtils.deriveClassFromSource(arr);
		assertEquals(Probe.class, ret);

		int[] arr2 = new int[0];
		ret = SpojoUtils.deriveClassFromSource(arr2);
		assertEquals(int.class, ret);

		List<String> listempty = new ArrayList<String>();
		ret = SpojoUtils.deriveClassFromSource(listempty);
		assertEquals(null, ret);

		List<String> list = new ArrayList<String>();
		list.add("22");
		ret = SpojoUtils.deriveClassFromSource(list);
		assertEquals(String.class, ret);
	}

	@Test
	public void makeMethodAccessible() throws SecurityException, NoSuchMethodException {
		Probe4 probe = new Probe4();
		Method expectedGetterMethod = probe.getClass().getDeclaredMethod("getPrivateString");
		Method actualGetterMethod = SpojoUtils.makeMethodAccessible(expectedGetterMethod);
		assertTrue(actualGetterMethod.isAccessible());
	}
}
