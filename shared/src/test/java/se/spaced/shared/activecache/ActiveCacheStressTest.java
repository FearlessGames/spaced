package se.spaced.shared.activecache;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

import static se.mockachino.Mockachino.*;

public class ActiveCacheStressTest {
	private KeyRequestHandler<Integer> keyRequestHandler;
	private ActiveCache<Integer, String> cache;

	@Before
	public void setUp() throws Exception {
		keyRequestHandler = mock(KeyRequestHandler.class);
		cache = new ActiveCacheImpl<Integer, String>(keyRequestHandler);
	}

	@Test
	public void stressTest() throws InterruptedException {
		int N = 37;
		final int M = 10001;
		final CyclicBarrier barrier = new CyclicBarrier(N);

		final AtomicInteger runJobs = new AtomicInteger();
		final CountDownLatch counter = new CountDownLatch(2 * N);
		final Job job = new Job() {
			@Override
			public void run(Object value) {
				runJobs.incrementAndGet();
			}
		};

		Thread[] consumers = new Thread[N];
		for (int threadId = 0; threadId < N; threadId++) {
			consumers[threadId] = new Thread() {
				@Override
				public void run() {
					List<Integer> list = new ArrayList<Integer>();
					for (int i = 0; i < M; i++) {
						list.add(i);
					}
					Collections.shuffle(list);

					barrWait(barrier);
					for (Integer integer : list) {
						cache.runWhenReady(integer % 100, job);
						Thread.yield();
					}
					counter.countDown();
				}
			};
			consumers[threadId].start();
		}

		final Thread[] producers = new Thread[N];
		for (int threadId = 0; threadId < N; threadId++) {
			producers[threadId] = new Thread() {
				@Override
				public void run() {
					List<Integer> list = new ArrayList<Integer>();
					for (int i = 0; i < M; i++) {
						list.add(i);
					}
					Collections.shuffle(list);
					barrWait(barrier);
					for (Integer i : list) {
						cache.delete(i % 100);
						Thread.yield();
						cache.setValue(i % 100, "String: " + i);
						Thread.yield();
					}
					counter.countDown();
				}
			};
			producers[threadId].start();
		}

		counter.await();
		if (runJobs.get() != N * M) {
			throw new RuntimeException("Missed jobs: " + (N * M - runJobs.get()));
		}
	}

	private void barrWait(CyclicBarrier barrier) {
		try {
			barrier.await();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (BrokenBarrierException e) {
			throw new RuntimeException(e);
		}
	}

}