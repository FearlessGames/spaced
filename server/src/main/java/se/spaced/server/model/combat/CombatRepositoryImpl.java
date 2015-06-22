package se.spaced.server.model.combat;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import se.spaced.server.model.ServerEntity;

import java.util.Map;

public class CombatRepositoryImpl implements CombatRepository {

	private final Map<ServerEntity, Combat> entitiesInCombat = Maps.newHashMap();

	@Override
	public Combat getCombat(ServerEntity entity) {
		return entitiesInCombat.get(entity);
	}

	@Override
	public int numberOfCombat() {
		return Sets.newHashSet(entitiesInCombat.values()).size();
	}

	@Override
	public void add(ServerEntity entity, Combat combat) {
		entitiesInCombat.put(entity, combat);
	}

	@Override
	public void remove(ServerEntity entity) {
		entity.resetCombatTimestamp();
		Combat combat = entitiesInCombat.remove(entity);
		if (combat != null) {
			combat.removeParticipant(entity);
		}
	}

	@Override
	public Iterable<Combat> getAllCombat() {
		return entitiesInCombat.values();
	}
}
