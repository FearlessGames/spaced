package se.spaced.server.model.player;

import se.spaced.server.model.Player;
import se.spaced.server.net.ClientConnection;

public interface RemotePlayerService {
	void playerLoggedIn(Player player, ClientConnection client);

	void playerLoggedOut(Player player, ClientConnection client);
}
