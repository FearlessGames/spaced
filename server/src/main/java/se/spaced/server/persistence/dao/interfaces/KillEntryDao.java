package se.spaced.server.persistence.dao.interfaces;

import se.spaced.server.model.spawn.EntityTemplate;
import se.spaced.server.stats.KillEntry;
import se.spaced.server.stats.KillStat;

import java.util.List;

public interface KillEntryDao extends Dao<KillEntry> {
	KillEntry findByParticipants(EntityTemplate killer, EntityTemplate victim);

	int deathsForVictim(EntityTemplate victim);

	List<KillStat> findTopKilled(int firstResult, int maxResults);

	List<KillStat> findTopKillers(int firstResult, int maxResults);

	List<KillStat> findTopKilledByEntity(EntityTemplate killer, int firstResult, int maxResult);

	List<KillStat> findTopEntityKilledBy(EntityTemplate victim, int firstResult, int maxResult);

}
