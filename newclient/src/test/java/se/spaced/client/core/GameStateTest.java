package se.spaced.client.core;

import com.ardor3d.util.ReadOnlyTimer;
import org.junit.Before;
import org.junit.Test;
import se.mockachino.order.*;
import se.spaced.client.core.states.GameState;
import se.spaced.client.core.states.GameStateHandler;
import se.spaced.client.core.states.StateChangeListener;
import se.spaced.shared.util.ListenerDispatcher;

import static se.mockachino.Mockachino.*;


public class GameStateTest {
	private GameStateHandler stateHandler;
	private GameState gameStateA;
	private GameState gameStateB;
	private ReadOnlyTimer timer;

	@Before
	public void setUp() {
		gameStateA = mock(GameState.class);
		gameStateB = mock(GameState.class);
		timer = mock(ReadOnlyTimer.class);

		stateHandler = new GameStateHandler(ListenerDispatcher.create(StateChangeListener.class));
	}

	@Test
	public void startsNewGameState() {
		stateHandler.changeState(gameStateA);
		stateHandler.update(timer.getTimePerFrame());
		verifyOnce().on(gameStateA).start();
	}

	@Test
	public void startsNewGameStateExitsOld() {
		stateHandler.changeState(gameStateA);
		stateHandler.update(timer.getTimePerFrame());
		stateHandler.changeState(gameStateB);
		stateHandler.update(timer.getTimePerFrame());

		OrderingContext ordering = newOrdering();
		ordering.verify().on(gameStateA).start();
		ordering.verify().on(gameStateA).exit();
		ordering.verify().on(gameStateB).start();
	}

	@Test
	public void runsGameStateUpdateMethod() {
		stateHandler.changeState(gameStateA);
		stateHandler.update(timer.getTimePerFrame());
		verifyOnce().on(gameStateA).update(stateHandler, timer.getTimePerFrame());
	}

	@Test
	public void runsCorrectGameStateUpdateMethodAfterChange() {
		stateHandler.changeState(gameStateA);
		stateHandler.changeState(gameStateB);
		stateHandler.update(timer.getTimePerFrame());

		verifyNever().on(gameStateA).update(stateHandler, timer.getTimePerFrame());
		verifyOnce().on(gameStateB).update(stateHandler, timer.getTimePerFrame());
	}
}
