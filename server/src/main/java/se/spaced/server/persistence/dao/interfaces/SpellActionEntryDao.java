package se.spaced.server.persistence.dao.interfaces;

import se.fearless.common.uuid.UUID;
import se.spaced.server.persistence.util.PageParameters;
import se.spaced.server.stats.SpellActionEntry;

import java.util.List;

public interface SpellActionEntryDao extends Dao<SpellActionEntry> {
	List<SpellActionEntry> findPerformersSpellActions(UUID pk, PageParameters pageParameters);
}
