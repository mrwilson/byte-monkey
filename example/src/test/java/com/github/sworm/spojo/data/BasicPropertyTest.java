/**
 * 
 */
package com.github.sworm.spojo.data;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import com.github.sworm.spojo.BaseTestCase;
import com.github.sworm.spojo.data.BasicProperty;

/**
 * @author albert
 * @Since 06/03/2011
 * 
 */
public class BasicPropertyTest extends BaseTestCase {

	@Test
	public void simpleProperty() {
		String name = "nombre";
		BasicProperty bp = new BasicProperty(name);
		assertEquals(name, bp.getName());
		assertEquals(name, bp.getCanonicalName());
		assertNull(bp.getChild("child"));
		assertEquals(0, bp.getChildMap().size());
		assertNull(bp.getParent());
		assertEquals(0, bp.getProperties().size());

		assertFalse(bp.isComplexType());
		assertTrue(bp.isEmpty());
		assertFalse(bp.hasParent());
	}

	@Test
	public void simpleProperty1Child() {
		String name = "nombre";
		BasicProperty bp = new BasicProperty(name);
		String child = "child";
		BasicProperty ch = new BasicProperty(child);
		bp.add(ch);

		assertNull(bp.getParent());
		assertEquals(1, bp.getChildMap().size());
		assertEquals(1, bp.getProperties().size());

		assertEquals(name + "." + child, ch.getCanonicalName());
		assertSame(ch, bp.getChild(child));
		assertNotNull(ch.getParent());

		assertTrue(bp.isComplexType());
		assertFalse(bp.isEmpty());
		assertFalse(bp.hasParent());
		assertTrue(ch.hasParent());
	}

	@Test
	public void simplePropertyRoot() {
		String name = "nombre";
		BasicProperty bp = new BasicProperty(name);
		String child = "child";
		BasicProperty ch = new BasicProperty(child);
		bp.add(ch);

		assertSame(bp, bp.getParentRoot());
		assertSame(bp, ch.getParentRoot());

	}

	@Test
	public void canonicalName() {
		String name = "nombre";
		BasicProperty bp = new BasicProperty(name);
		String child1 = "child1";
		BasicProperty ch1 = new BasicProperty(child1);
		String child2 = "child2";
		BasicProperty ch2 = new BasicProperty(child2);
		ch1.add(ch2);
		bp.add(ch1);

		assertEquals("nombre.child1.child2", ch2.getCanonicalName());
		assertEquals("nombre.child1", ch1.getCanonicalName());
		assertEquals("nombre", bp.getCanonicalName());
	}

	@Test
	public void removeProperty() {
		String name = "nombre";
		BasicProperty bp = new BasicProperty(name);
		String child1 = "child1";
		BasicProperty ch1 = new BasicProperty(child1);
		String child2 = "child2";
		BasicProperty ch2 = new BasicProperty(child2);
		bp.add(ch1);
		bp.add(ch2);

		assertEquals(2, bp.size());
		bp.removeProperty(ch1);
		assertEquals(1, bp.size());
		assertNull(ch1.getParent());
		bp.removeProperty(ch2);
		assertNull(ch2.getParent());
		assertEquals(0, bp.size());
		bp.removeProperty(ch2);
		assertEquals(0, bp.size());

	}

	@Test
	public void clear() {
		String name = "nombre";
		BasicProperty bp = new BasicProperty(name);
		String child1 = "child1";
		BasicProperty ch1 = new BasicProperty(child1);
		String child2 = "child2";
		BasicProperty ch2 = new BasicProperty(child2);

		bp.clear();
		assertTrue(bp.isEmpty());
		bp.add(ch1);
		bp.add(ch2);
		assertFalse(bp.isEmpty());
		bp.clear();
		assertTrue(bp.isEmpty());
	}

	@Test
	public void toarray1() {
		String name = "nombre";
		BasicProperty bp = new BasicProperty(name);
		String child1 = "child1";
		BasicProperty ch1 = new BasicProperty(child1);
		String child2 = "child2";
		BasicProperty ch2 = new BasicProperty(child2);
		bp.add(ch1);
		bp.add(ch2);

		assertArrayEquals(new Object[] { ch1, ch2 }, bp.toArray());
		bp.clear();
		assertArrayEquals(new Object[] {}, bp.toArray());
	}

	@Test
	public void toarray2() {
		String name = "nombre";
		BasicProperty bp = new BasicProperty(name);
		String child1 = "child1";
		BasicProperty ch1 = new BasicProperty(child1);
		String child2 = "child2";
		BasicProperty ch2 = new BasicProperty(child2);
		bp.add(ch1);
		bp.add(ch2);

		BasicProperty[] array = new BasicProperty[2];
		assertArrayEquals(new BasicProperty[] { ch1, ch2 }, bp.toArray(array));
		bp.clear();
		assertArrayEquals(new BasicProperty[] {}, bp.toArray());
	}

	@Test
	public void changeParent() {
		BasicProperty bp1 = new BasicProperty("p1");
		BasicProperty bp2 = new BasicProperty("p2");
		BasicProperty bp3 = new BasicProperty("p3");
		bp1.add(bp2);

		bp2.setParent(bp3);
		assertFalse(bp1.isComplexType()); // bp1 has not children
		assertTrue(bp3.isComplexType()); // bp3 has children
		assertSame(bp2, bp3.getChild("p2"));
		assertSame(bp3, bp2.getParent());
	}

	@Test
	public void createWithParent() {
		BasicProperty bp1 = new BasicProperty("p1");
		BasicProperty bp2 = new BasicProperty(bp1, "p2");

		assertTrue(bp1.isComplexType()); // bp1 has children
		assertSame(bp2, bp1.getChild("p2"));
		assertSame(bp1, bp2.getParent());

	}

	@Test
	public void equalsProperty() {
		BasicProperty pr1 = new BasicProperty("p1");
		BasicProperty expected = new BasicProperty("p1");
		assertEquals(expected, pr1);
		assertEquals(expected.hashCode(), pr1.hashCode());
	}

	@Test
	public void equalsProperty2() {
		Property<String> pr1 = new BasicProperty("p1").add("pr1,pr22");
		Property<String> expected = new BasicProperty("p1").add("pr1,pr22");
		assertEquals(expected, pr1);
		assertEquals(expected.hashCode(), pr1.hashCode());
	}

	@Test
	public void equalsProperty3() {
		BasicProperty pr1 = new BasicProperty("p1");
		BasicProperty expected = new BasicProperty("p2");
		assertFalse(expected.equals(pr1));
		assertFalse(expected.hashCode() == pr1.hashCode());
	}

	@Test
	public void equalsProperty4() {
		Property<String> pr1 = new BasicProperty("p1").add("pr1,pr2");
		Property<String> expected = new BasicProperty("p1").add("pr1,prr2");
		assertFalse(expected.equals(pr1));
		assertFalse(expected.hashCode() == pr1.hashCode());
	}

	@Test
	public void equalsProperty5() {
		Property<String> pr1 = new BasicProperty("p1").add("pr1,pr2");
		Property<String> expected = pr1;
		assertEquals(expected, pr1);
		assertEquals(expected.hashCode(), pr1.hashCode());
	}

	@Test
	public void equalsProperty6() {
		Property<String> pr1 = new BasicProperty("p1");
		Object other = new Object();
		assertFalse(pr1.equals(other));
		assertFalse(pr1.hashCode() == other.hashCode());
	}

	@Test
	public void equalsProperty7() {
		Property<String> pr1 = new BasicProperty("p1");
		Object other = null;
		assertFalse(pr1.equals(other));
	}

	@Test
	public void addCollection() {
		Collection<String> coll = new ArrayList<String>();
		coll.add("p1,p2");
		coll.add("p3.p1,p1.p3");
		Property<String> pr = new BasicProperty("r").add(coll);
		assertTrue(pr.containsChild("p1"));
		assertTrue(pr.containsChild("p2"));
		assertTrue(pr.containsChild("p3"));
		assertTrue(pr.getChild("p3").containsChild("p1"));
		assertTrue(pr.getChild("p1").containsChild("p3"));
	}

	@Test
	public void merge() {
		Property<String> pr1 = new BasicProperty("root").add("pr1,pr2");
		Property<String> pr2 = new BasicProperty("root").add("pr3,pr4,pr1.pr11");

		pr1.merge(pr2);

		assertTrue(pr1.containsChild("pr3"));
		assertTrue(pr1.containsChild("pr4"));
		assertTrue(pr1.getChild("pr1").containsChild("pr11"));
		assertFalse(pr2.containsChild("pr2"));

	}
}
