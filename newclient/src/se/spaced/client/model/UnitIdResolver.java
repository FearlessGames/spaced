package se.spaced.client.model;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.client.model.player.PlayerEntityProvider;
import se.spaced.messages.protocol.Entity;
import se.spaced.shared.activecache.ActiveCache;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UnitIdResolver {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final PlayerEntityProvider playerProvider;

	public static final String PLAYER = "player";
	public static final String TARGET = "target";
	public static final String NONE = "none";
	private static final Pattern PLAYER_PATTERN = Pattern.compile("^( *)(player)|( *)( *)$");
	private static final Pattern TARGET_PATTERN = Pattern.compile("^(.*)target( *)$");
	private static final Pattern PARTY_PATTERN = Pattern.compile("^(.*)party([0-9]+)( *)$");
	private static final Pattern RAID_PATTERN = Pattern.compile("^(.*)raid([0-9]+)( *)$");
	private static final Pattern PET_PATTERN = Pattern.compile("^(.*)pet( *)$");
	private final ActiveCache<Entity, ClientEntity> entityCache;

	@Inject
	public UnitIdResolver(ActiveCache<Entity, ClientEntity> entityCache, PlayerEntityProvider playerProvider) {
		this.entityCache = entityCache;
		this.playerProvider = playerProvider;
	}

	public ClientEntity resolveEntity(String unitReference) {
		logger.debug("Resolving entity based on unitId {}", unitReference);

		if (unitReference.trim().length() == 0) {
			return null;
		}

		return innerResolveEntity(unitReference.toLowerCase());
	}

	private ClientEntity innerResolveEntity(String unitReference) {
		Matcher m;

		m = PLAYER_PATTERN.matcher(unitReference);
		if (m.matches()) {
			return playerProvider.get();
		}

		m = TARGET_PATTERN.matcher(unitReference);
		if (m.matches()) {
			String prependedReference = m.group(1);
			ClientEntity entity = innerResolveEntity(prependedReference);
			if (entity == null) {
				return null;
			}
			return entity.getTarget();
		}
		m = PET_PATTERN.matcher(unitReference);
		if (m.matches()) {
			String prependedReference = m.group(1);
			ClientEntity entity = innerResolveEntity(prependedReference);
			if (entity == null) {
				return null;
			}
			//return entity.getPet();
			return null;
		}

		ClientEntity entity = getEntityByName(unitReference);
		if (entity != null) {
			return entity;
		}

		return null;
	}

	public ClientEntity getEntityByName(String name) {
		for (ClientEntity entity : entityCache.getValues()) {
			if (name.equalsIgnoreCase(entity.getName())) {
				return entity;
			}
		}
		return null;
	}


}
