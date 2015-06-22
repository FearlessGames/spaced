package se.spaced.server.services.auth;

import se.spaced.server.model.Player;

public class GmAuthenticator implements PlayerAuthenticator {
	@Override
	public boolean authenticate(Player player) {
		return player.isGm();
	}
}
