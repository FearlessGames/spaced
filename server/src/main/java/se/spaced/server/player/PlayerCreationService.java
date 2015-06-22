package se.spaced.server.player;

import se.spaced.server.account.Account;
import se.spaced.server.model.PersistedCreatureType;
import se.spaced.server.model.PersistedFaction;
import se.spaced.server.model.Player;
import se.spaced.server.model.PlayerType;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.server.persistence.DuplicateObjectException;
import se.spaced.shared.model.Gender;

import java.util.Collection;

public interface PlayerCreationService {
	Player createPlayer(
			Account account,
			String name,
			Gender gender,
			PersistedCreatureType creatureType,
			PersistedFaction faction,
			int playerInventorySize,
			Collection<ServerSpell> spells,
			PlayerType type) throws DuplicateObjectException, PlayerCreationException;

	Player createDefaultPlayer(
			Account account,
			String name,
			Gender gender,
			PlayerType type) throws DuplicateObjectException, PlayerCreationException;
}
