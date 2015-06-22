package se.spaced.server.services;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.server.model.Player;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class PlayerConnectedServiceImpl implements PlayerConnectedService {
	private final Map<Player, PlayerConnectionInfo> connectedPlayers = new ConcurrentHashMap<Player, PlayerConnectionInfo>();

	Logger logger = LoggerFactory.getLogger(getClass());

	public PlayerConnectedServiceImpl() {

	}

	@Override
	public void addConnectedPlayer(Player player) {
		PlayerConnectionInfo playerConnectionInfo = new PlayerConnectionInfo(player, new Date());
		connectedPlayers.put(player, playerConnectionInfo);
	}

	@Override
	public int getNrOfCurrentlyLoggedInClients() {
		return connectedPlayers.size();
	}

	@Override
	public void removeConnectedPlayer(Player player) {
		connectedPlayers.remove(player);
	}

	@Override
	public Set<PlayerConnectionInfo> getConnectedPlayers() {
		return ImmutableSet.copyOf(connectedPlayers.values());
	}

}
