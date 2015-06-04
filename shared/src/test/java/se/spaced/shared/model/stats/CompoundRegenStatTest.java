package se.spaced.shared.model.stats;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CompoundRegenStatTest {

	private static final double EPSILON = 1e-10;

	private SimpleStat base;
	private SimpleStat ooc;
	private CompoundRegenStat compoundRegen;

	@Before
	public void setUp() throws Exception {
		base = new SimpleStat("base", 3);
		ooc = new SimpleStat("ooc", 4);
		compoundRegen = new CompoundRegenStat(base, ooc, "RegenRate");
	}

	@Test
	public void init() throws Exception {
		assertEquals(7, compoundRegen.getValue(), EPSILON);
	}

	@Test
	public void changeBase() throws Exception {
		base.changeValue(5);
		assertEquals(9, compoundRegen.getValue(), EPSILON);
	}

	@Test
	public void changeOoc() throws Exception {
		ooc.changeValue(5);
		assertEquals(8, compoundRegen.getValue(), EPSILON);
	}
}
