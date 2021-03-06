package se.spaced.shared.partition;

import org.junit.Before;
import org.junit.Test;

public class PartitionedGeoMapTest extends BaseGeoMap {
	public PartitionedGeoMapTest() {
		super(new PartitionedGeoMap<HasPosition>(1000));
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