/**
 * 
 */
package com.github.sworm.spojo.utils;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import com.github.sworm.spojo.BaseTestCase;
import com.github.sworm.spojo.data.BasicProperty;
import com.github.sworm.spojo.data.Property;

import com.google.gson.Gson;

/**
 * @author albert
 * @Since 07/03/2011
 * 
 */
public class SpojoUtilsMoreTest extends BaseTestCase {

	public static class Probe {
		String prop1;
		String prop2;
		Probe probe;
		List<String> listaStr;
		Set<String> setStr;
		ArrayList<String> arrayListStr;

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

	public static class Probe3 {
		String prop1;
		String prop2;
		Probe probe;
		List<String> listaStr;
		Set<String> setStr;
		ArrayList<String> arrayListStr;

		List<Probe> listaProbe;
		String[] arrStr;
		int[] arrInt;
		Probe[] arrProbe;

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

	private static Probe createProbe(final String def) {
		return new Gson().fromJson(def, Probe.class);
	}

	private static Probe3 createProbe3(final String def) {
		return new Gson().fromJson(def, Probe3.class);
	}

	@Test
	public void copyPropertieswithExclude1() {
		Probe source = createProbe("{prop1:'propiedad1',prop2:'propiedad2'}");
		Probe3 target = createProbe3("{}");
		BasicProperty root = new BasicProperty("root");
		root.add(new BasicProperty("prop1"));

		SpojoUtils.copyPropertiesWithExclude(source, target, root);

		assertEquals(null, target.getProp1());
		assertEquals("propiedad2", target.getProp2());

	}

	@Test
	public void copyPropertieswithExclude2() {
		Probe source = createProbe("{prop1:'propiedad1',prop2:'propiedad2'}");
		Probe3 target = createProbe3("{}");
		Property<String> root = new BasicProperty("root").add("prop1,prop2");

		SpojoUtils.copyPropertiesWithExclude(source, target, root);
		assertEquals(null, target.getProp1());
		assertEquals(null, target.getProp2());

	}

	@Test
	public void copyPropertieswithExclude3() {
		Probe source = createProbe("{prop1:'propiedad1',prop2:'propiedad2'}");
		Probe3 target = createProbe3("{prop1:'propiedad11',prop2:'propiedad22'}");

		SpojoUtils.copyPropertiesWithExclude(source, target, new BasicProperty("root"));
		assertEquals("propiedad1", target.getProp1());
		assertEquals("propiedad2", target.getProp2());

	}

	@Test
	public void copyPropertieswithExclude4() {
		Probe source = createProbe("{prop1:'propiedad1',prop2:'propiedad2'}");
		Probe3 target = createProbe3("{}");

		Property<String> root = new BasicProperty("root").add("prop1222,prop22222");

		SpojoUtils.copyPropertiesWithExclude(source, target, root);
		assertEquals("propiedad1", target.getProp1());
		assertEquals("propiedad2", target.getProp2());

	}

	@Test
	public void copyPropertieswithExclude5() {
		Probe source = createProbe("{prop1:'propiedad1',prop2:'propiedad2'}");
		Probe3 target = createProbe3("{}");

		SpojoUtils.copyPropertiesWithExclude(source, target, null);

		assertEquals("propiedad1", target.getProp1());
		assertEquals("propiedad2", target.getProp2());
	}

	@Test
	public void copyPropertiesWithInclude1() {
		Probe source = createProbe("{prop1:'propiedad1',prop2:'propiedad2'}");
		Probe3 target = createProbe3("{prop1:'propiedad11',prop2:'propiedad22'}");

		Property<String> root = new BasicProperty("root").add("prop1");

		SpojoUtils.copyPropertiesWithInclude(source, target, root);
		assertEquals("propiedad1", target.getProp1());
		assertEquals("propiedad22", target.getProp2());

	}

	@Test
	public void copyPropertiesWithInclude2() {
		Probe source = createProbe("{prop1:'propiedad1',prop2:'propiedad2'}");
		Probe3 target = createProbe3("{prop1:'propiedad11',prop2:'propiedad22'}");

		BasicProperty root = new BasicProperty("root");

		SpojoUtils.copyPropertiesWithInclude(source, target, root);
		assertEquals("propiedad11", target.getProp1());
		assertEquals("propiedad22", target.getProp2());

	}

	@Test
	public void copyPropertiesWithInclude3() {
		Probe source = createProbe("{prop1:'propiedad1',prop2:'propiedad2'}");
		Probe3 target = createProbe3("{prop1:'propiedad11',prop2:'propiedad22'}");

		Property<String> root = new BasicProperty("root").add("prop111");

		SpojoUtils.copyPropertiesWithInclude(source, target, root);
		assertEquals("propiedad11", target.getProp1());
		assertEquals("propiedad22", target.getProp2());

	}

	@Test
	public void copyPropertieswithInclude4() {
		Probe source = createProbe("{prop1:'propiedad1',prop2:'propiedad2'}");
		Probe3 target = createProbe3("{}");

		SpojoUtils.copyPropertiesWithInclude(source, target, null);

		assertEquals(null, target.getProp1());
		assertEquals(null, target.getProp2());
	}

	@Test
	public void copyPropertiesInner() {
		Probe source = createProbe("{prop1:'propiedad1',prop2:'propiedad2',probe:{prop1:'prop1',prop2:'prop2'}}");
		Probe3 target = createProbe3("{}");

		SpojoUtils.copyPropertiesWithExclude(source, target, null);

		assertNotNull(target.getProbe());
		assertNotSame(source.getProbe(), target.getProbe());
		assertEquals("prop1", target.getProbe().getProp1());
		assertEquals("prop2", target.getProbe().getProp2());
	}

	@Test
	public void copyPropertiesInnerExclude() {
		Probe source = createProbe("{prop1:'propiedad1',prop2:'propiedad2',probe:{prop1:'prop1',prop2:'prop2'}}");
		Probe3 target = createProbe3("{}");

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
		Probe3 target = createProbe3("{}");
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
		Probe3 target = createProbe3("{}");
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
		Probe3 target = createProbe3("{}");
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
		Probe3 target = createProbe3("{}");
		BasicProperty root = new BasicProperty("root");

		SpojoUtils.copyPropertiesWithExclude(source, target, root);

		assertEquals("propiedad1", target.getProp1());
		assertEquals("propiedad2", target.getProp2());
		assertNotNull(target.getProbe());
		assertNotSame(source.getProbe(), target.getProbe());
		assertEquals("prop1", target.getProbe().getProp1());
		assertEquals("prop2", target.getProbe().getProp2());
	}

}
