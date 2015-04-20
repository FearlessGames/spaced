package se.spaced.server.player;

import se.spaced.server.model.PersistedCreatureType;
import se.spaced.server.model.PersistedFaction;
import se.spaced.server.model.Player;
import se.spaced.server.model.PlayerType;
import se.spaced.server.persistence.DuplicateObjectException;
import se.spaced.shared.model.Gender;

public interface PlayerService {
	Player createPlayerCharacter(
			String name,
			Gender gender,
			PersistedCreatureType creatureType,
			PersistedFaction faction, PlayerType type) throws DuplicateObjectException, PlayerCreationException;

	Player getPlayer(String name);

	void updatePlayer(Player player);

	void reloadFromDatabase(Player player);
}
