package se.spaced.shared.util;

import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;

public class BijectiveMapTest {

	private Integer missingX;
	private BijectiveMap<Integer, String> map;

	@Before
	public void setup() {
		missingX = new Integer(123);
		map = new BijectiveMap<Integer, String>(missingX, "fail");
		map.connect(new Integer(1), "one");
	}

	@Test
	public void testSimple1() {
		assertEquals(new Integer(1), map.getX("one"));
	}

	@Test
	public void testSimple2() {
		assertEquals("one", map.getY(new Integer(1)));
	}

	@Test
	public void testMissing1() {
		assertEquals(missingX, map.getX("two"));
	}

	@Test
	public void testMissing2() {
		assertEquals("fail", map.getY(new Integer(12345)));
	}

	@Test
	public void testOverride() {
		map.connect(new Integer(1), "oneone");
		assertEquals(new Integer(1), map.getX("oneone"));
		assertEquals("oneone", map.getY(new Integer(1)));

		assertEquals(missingX, map.getX("one"));
	}

	@Test
	public void testAll() {
		Set<Integer> allX = map.getAllX();
		assertEquals(1, allX.size());
		assertEquals(new Integer(1), allX.iterator().next());

		Set<String> allY = map.getAllY();
		assertEquals(1, allY.size());
		assertEquals("one", allY.iterator().next());
	}
}
