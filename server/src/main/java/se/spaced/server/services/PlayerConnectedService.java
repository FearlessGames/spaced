package se.spaced.server.services;

import se.spaced.server.model.Player;

import java.util.Set;

/**
 * Service used by the exposed webservices to provide number of connected clients
 */
public interface PlayerConnectedService {
	void addConnectedPlayer(Player player);

	void removeConnectedPlayer(Player player);

	int getNrOfCurrentlyLoggedInClients();

	Set<PlayerConnectionInfo> getConnectedPlayers();
}
