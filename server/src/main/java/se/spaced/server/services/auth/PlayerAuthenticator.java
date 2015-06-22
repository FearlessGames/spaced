package se.spaced.server.services.auth;

import se.spaced.server.model.Player;

public interface PlayerAuthenticator {
	boolean authenticate(Player player);
}
