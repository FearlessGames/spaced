package se.spaced.server.model.combat;

import org.junit.Before;
import org.junit.Test;
import se.spaced.server.ScenarioTestBase;
import se.spaced.server.model.Player;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.action.OrderedAction;
import se.spaced.server.model.player.PlayerMockFactory;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class OrderedActionTest extends ScenarioTestBase {
	private static final long EXECUTION_TIME = 1300L;
	private OrderedAction action;
	private Player player;


	@Before
	public void setUp() {
		PlayerMockFactory factory = new PlayerMockFactory.Builder(timeProvider, uuidFactory).build();
		player = factory.createPlayer("kalle");
		action = new TestOrderedAction(EXECUTION_TIME, player);
	}

	@Test
	public void testTimeToExecuteTooEarly() {
		assertFalse(action.timeToExecute(EXECUTION_TIME - 100));
	}

	@Test
	public void testTimeToExecuteExactlyAt() {
		assertTrue(action.timeToExecute(EXECUTION_TIME));
	}

	@Test
	public void testTimeToExecuteLater() {
		assertTrue(action.timeToExecute(EXECUTION_TIME + 100));
	}

	@Test
	public void testTimeToExecuteNegative() {
		assertFalse(action.timeToExecute(-1000));
	}

	@Test
	public void testCompareTo() {
		OrderedAction action2 = new TestOrderedAction(EXECUTION_TIME + 200, player);
		assertTrue("Comparison failed", action.compareTo(action2) < 0);
	}

	@Test
	public void testGetPerformer() {
		assertSame(player, action.getPerformer());
	}

	private class TestOrderedAction extends OrderedAction {
		public TestOrderedAction(long executionTime, ServerEntity performer) {
			super(executionTime, performer);
		}

		@Override
		public void perform() {
			// not tested here
		}
	}
}
