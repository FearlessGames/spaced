package se.spaced.server.player;

import com.google.inject.Inject;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.fearlessgames.common.util.TimeProvider;
import se.spaced.server.model.PersistedAppearanceData;
import se.spaced.server.model.PersistedCreatureType;
import se.spaced.server.model.PersistedFaction;
import se.spaced.server.model.PersistedPositionalData;
import se.spaced.server.model.Player;
import se.spaced.server.model.PlayerType;
import se.spaced.server.persistence.DuplicateObjectException;
import se.spaced.server.persistence.dao.impl.hibernate.TransactionManager;
import se.spaced.server.persistence.dao.interfaces.PlayerDao;
import se.spaced.server.persistence.util.transactions.AutoTransaction;
import se.spaced.shared.model.Gender;
import se.spaced.shared.model.player.PlayerCreationFailure;
import se.spaced.shared.model.stats.EntityStats;
import se.spaced.shared.model.stats.StatData;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayerServiceImpl implements PlayerService {

	private static final int NAME_MIN_LENGTH = 1;
	private static final int NAME_MAX_LENGTH = 20;
	public static final PersistedPositionalData DEFAULT_SPAWN_POSITION = new PersistedPositionalData(new SpacedVector3(25,
			22,
			-9.3), SpacedRotation.IDENTITY);

	private final PlayerDao playerDao;
	private final TimeProvider timeProvider;
	private final TransactionManager transactionManager;

	@Inject
	public PlayerServiceImpl(TimeProvider timeProvider, PlayerDao playerDao, TransactionManager transactionManager) {
		this.timeProvider = timeProvider;
		this.playerDao = playerDao;
		this.transactionManager = transactionManager;
	}

	@Override
	@AutoTransaction
	public Player createPlayerCharacter(
			String name,
			Gender gender,
			PersistedCreatureType creatureType,
			PersistedFaction faction, PlayerType type) throws DuplicateObjectException, PlayerCreationException {

		if (name.length() <= NAME_MIN_LENGTH) {
			throw new PlayerCreationException("Name " + name + " too short, must be > " + NAME_MIN_LENGTH + " characters.",
					PlayerCreationFailure.NAME_TOO_SHORT);
		}

		if (name.length() > NAME_MAX_LENGTH) {
			throw new PlayerCreationException("Name " + name + " too long, must be > " + NAME_MAX_LENGTH + " characters.",
					PlayerCreationFailure.NAME_TOO_LONG);
		}

		Pattern p = Pattern.compile("^[a-zA-Z][a-z]+$");
		Matcher m = p.matcher(name);

		if (!m.find()) {
			throw new PlayerCreationException("Name contains invalid characters. " + name,
					PlayerCreationFailure.NAME_CONTAINS_INVALID_CHARACTERS);
		}

		if (playerDao.findByName(name) != null) {
			throw new DuplicateObjectException("A player with the name " + name + " already exists");
		}

		String playerModel = "models/players/player_male.xmo";
		if (gender == Gender.FEMALE) {
			playerModel = "models/players/player_female.xmo";
		}

		Player player = new Player(null, name, gender, creatureType,
				DEFAULT_SPAWN_POSITION,
				new PersistedAppearanceData(playerModel, "icon_player"),
				new EntityStats(timeProvider, new StatData(12, 0, 0.3, 0, EntityStats.IN_COMBAT_COOLRATE, 0.0)), faction, type);

		playerDao.persist(player);

		return player;
	}

	@Override
	@AutoTransaction
	public Player getPlayer(String name) {
		return playerDao.findByName(name);
	}

	@Override
	@AutoTransaction
	public void updatePlayer(Player player) {
		playerDao.persist(player);
	}

	@Override
	@AutoTransaction
	public void reloadFromDatabase(Player player) {
		transactionManager.rebuildFromDataBase(player);
	}
}
