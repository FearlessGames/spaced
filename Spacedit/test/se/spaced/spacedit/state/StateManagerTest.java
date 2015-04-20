package se.spaced.spacedit.state;

import org.junit.Before;
import org.junit.Test;
import se.mockachino.annotations.*;

import static se.mockachino.Mockachino.*;


public class StateManagerTest {
	@Mock
	private StateChangeListener listener;

	@Before
	public void setup() {
		setupMocks(this);
	}

	@Test
	public void testChangingStateWillTriggerListeners() {
		StateManager manager = new StateManagerImpl();
		manager.registerStateChangeListener(listener);
		manager.switchState(RunningState.XMO_IN_CONTEXT);
		verifyOnce().on(listener).fromDefaultToXMOInContext();
		manager.switchState(RunningState.DEFAULT);
		verifyOnce().on(listener).fromXMOInContextToDefault();
	}


}
