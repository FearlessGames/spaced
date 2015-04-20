package se.spaced.client.core.states;

import com.google.common.collect.Maps;

import java.util.Map;

public class TransitionManager {
	private final GameState nullState = new NullState();

	private final Map<GameState, Map<Transition, GameState>> map = Maps.newHashMap();

	public void addMapping(GameState from, Transition transition, GameState to) {
		Map<Transition, GameState> gameStateMap = map.get(from);
		if (gameStateMap == null) {
			gameStateMap = Maps.newHashMap();
			map.put(from, gameStateMap);
		}
		gameStateMap.put(transition, to);
	}

	public GameState getNext(GameState from, Transition transition) {
		Map<Transition, GameState> gameStateMap = map.get(from);
		if (gameStateMap != null) {
			GameState endState = gameStateMap.get(transition);
			if (endState != null) {
				return endState;
			}
		}
		return nullState;
	}

	public void makeTransition(GameStateContext context, Transition transition) {
		GameState current = context.current();
		GameState nextState = getNext(current, transition);
		context.changeState(nextState);
	}
}
