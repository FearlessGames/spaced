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
		missingX = 123;
		map = new BijectiveMap<>(missingX, "fail");
		map.connect(1, "one");
	}

	@Test
	public void testSimple1() {
		assertEquals(Integer.valueOf(1), map.getX("one"));
	}

	@Test
	public void testSimple2() {
		assertEquals("one", map.getY(1));
	}

	@Test
	public void testMissing1() {
		assertEquals(missingX, map.getX("two"));
	}

	@Test
	public void testMissing2() {
		assertEquals("fail", map.getY(12345));
	}

	@Test
	public void testOverride() {
		map.connect(1, "oneone");
		assertEquals(Integer.valueOf(1), map.getX("oneone"));
		assertEquals("oneone", map.getY(1));

		assertEquals(missingX, map.getX("one"));
	}

	@Test
	public void testAll() {
		Set<Integer> allX = map.getAllX();
		assertEquals(1, allX.size());
		assertEquals(Integer.valueOf(1), allX.iterator().next());

		Set<String> allY = map.getAllY();
		assertEquals(1, allY.size());
		assertEquals("one", allY.iterator().next());
	}
}
