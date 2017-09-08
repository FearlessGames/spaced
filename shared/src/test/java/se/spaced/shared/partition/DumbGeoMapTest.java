package se.spaced.shared.partition;

import org.apache.mina.util.ConcurrentHashSet;
import org.junit.Before;
import org.junit.Test;

public class DumbGeoMapTest extends BaseGeoMap {
	public DumbGeoMapTest() {
		super(new DumbGeoMap<HasPosition>(new ConcurrentHashSet<HasPosition>()));
	}

	@Before
	public void setup() {
		geomap.clear();
	}

	@Override
	@Test
	public void testEmpty() {
		super.testEmpty();
	}

	@Override
	@Test
	public void testClear() {
		super.testClear();
	}

	@Override
	@Test
	public void testFinder() {
		super.testFinder();
	}

	@Override
	@Test
	public void testMultiThread() throws InterruptedException {
		super.testMultiThread();
	}
}
