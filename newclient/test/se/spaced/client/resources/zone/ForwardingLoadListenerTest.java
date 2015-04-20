package se.spaced.client.resources.zone;

import org.junit.Before;
import org.junit.Test;

import static se.mockachino.Mockachino.*;

public class ForwardingLoadListenerTest {

	private LoadListener loadListener;
	private LoadListener delegate;

	@Before
	public void setUp() throws Exception {
		delegate = mock(LoadListener.class);
		loadListener = new ForwardingLoadListener(delegate);
	}

	@Test
	public void loadCompleted() throws Exception {
		loadListener.loadCompleted();
		verifyOnce().on(delegate).loadCompleted();
	}

	@Test
	public void testLoadUpdate() throws Exception {
		loadListener.loadUpdate(4);
		verifyOnce().on(delegate).loadUpdate(4);
	}

	@Test
	public void override() throws Exception {
		LoadListener l = new ForwardingLoadListener(delegate) {
			@Override
			public void loadCompleted() {
				super.loadCompleted();
			}
		};
		l.loadCompleted();
		verifyOnce().on(delegate).loadCompleted();
	}
}
