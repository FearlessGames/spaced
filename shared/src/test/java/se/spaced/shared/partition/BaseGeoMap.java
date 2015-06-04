package se.spaced.shared.partition;

import com.ardor3d.math.Vector3;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public abstract class BaseGeoMap {
	protected final GeoMap<HasPosition> geomap;

	final DummyPosition a = new DummyPosition(0, 0, 0);
	final DummyPosition b = new DummyPosition(100, 0, 0);
	final DummyPosition c = new DummyPosition(1000, 0, 0);

	protected BaseGeoMap(GeoMap<HasPosition> mapImpl) {
		this.geomap = mapImpl;
	}

	public void testClear() {
		geomap.add(a);
		geomap.add(b);
		geomap.add(c);
		List<HasPosition> positionCollection = geomap.findNear(new Vector3(), 10000, true);
		assertEquals(3, positionCollection.size());
		assertEquals(a, positionCollection.get(0));
		assertEquals(b, positionCollection.get(1));
		assertEquals(c, positionCollection.get(2));
		geomap.clear();

		positionCollection = geomap.findNear(new Vector3(), 10000, false);
		assertEquals(0, positionCollection.size());
	}

	public void testEmpty() {
		Iterable near = geomap.findNear(new Vector3(), 10000, false);
		assertEquals(false, near.iterator().hasNext());

		HasPosition position = geomap.findNearest(new Vector3(), 10000);
		assertEquals(null, position);
	}

	public void testFinder() {
		geomap.add(a);
		geomap.add(b);
		geomap.add(c);

		List<HasPosition> positionCollection = geomap.findNear(new Vector3(), 0, false);
		assertEquals(1, positionCollection.size());
		assertTrue(positionCollection.contains(a));

		positionCollection = geomap.findNear(new Vector3(), 100, true);
		assertEquals(2, positionCollection.size());
		assertEquals(a, positionCollection.get(0));
		assertEquals(b, positionCollection.get(1));

		positionCollection = geomap.findNear(new Vector3(), 1000, true);
		assertEquals(3, positionCollection.size());
		assertEquals(a, positionCollection.get(0));
		assertEquals(b, positionCollection.get(1));
		assertEquals(c, positionCollection.get(2));

		positionCollection = geomap.findNear(new Vector3(), 10000, true);
		assertEquals(3, positionCollection.size());
		assertEquals(a, positionCollection.get(0));
		assertEquals(b, positionCollection.get(1));
		assertEquals(c, positionCollection.get(2));
	}

	public void testMultiThread() throws InterruptedException {
		final int numThreads = 100;
		final CountDownLatch starter = new CountDownLatch(1);
		final CountDownLatch done = new CountDownLatch(numThreads);
		final Exception[] error = new Exception[1];
		for (int i = 0; i < numThreads; i++) {
			Thread t = new Thread("Thread-" + i) {
				@Override
				public void run() {
					try {
						starter.await();
						//System.out.println("Thread " + getName() + " started");
						for (int i = 0; i < 10000 / numThreads; i++) {
							double x = 1000 * 1000 * (Math.random() - 0.5);
							double y = 1000 * 1000 * (Math.random() - 0.5);
							double z = 1000 * 1000 * (Math.random() - 0.5);
							DummyPosition position = new DummyPosition(x, y, z);
							geomap.add(position);

							x = 1000 * 1000 * (Math.random() - 0.5);
							y = 1000 * 1000 * (Math.random() - 0.5);
							z = 1000 * 1000 * (Math.random() - 0.5);
							position.setPosition(x, y, z);

							geomap.update(position);
							if (!geomap.contains(position)) {
								throw new RuntimeException("Broken contains - could not find added position");
							}
							geomap.remove(position);
							if (geomap.contains(position)) {
								throw new RuntimeException("Broken contains - could find removed position");
							}
						}
					} catch (Exception e) {
						System.out.println(e.getMessage());
						e.printStackTrace();
						error[0] = e;
					} finally {
						done.countDown();
						//System.out.println("Thread " + getName() + " done");
					}
				}
			};
			t.start();
		}
		starter.countDown();
		done.await();

		HasPosition position = geomap.findNearest(new Vector3(), 10000);
		assertTrue(position == null);
		if (error[0] != null) {
			throw new RuntimeException(error[0]);
		}
	}

	// testUpdate

	// testRemove

	// testMultithread
}
