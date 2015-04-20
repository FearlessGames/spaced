package se.spaced.client.ardor.ui.api;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.spaced.client.core.states.GameState;
import se.spaced.client.core.states.GameStateContext;
import se.spaced.client.net.smrt.ServerConnection;

@Singleton
public class PromoApi {
	private final ServerConnection serverConnection;
	private final GameStateContext context;
	private final GameState nextState;

	@Inject
	public PromoApi(
			final ServerConnection serverConnection, GameStateContext context,
			@Named("selectServerState") GameState nextState) {
		this.serverConnection = serverConnection;
		this.context = context;
		this.nextState = nextState;
	}

	@LuaMethod(name = "PromoEnded", global = true)
	public void promoEnded() {
		context.changeState(nextState);
	}


}
