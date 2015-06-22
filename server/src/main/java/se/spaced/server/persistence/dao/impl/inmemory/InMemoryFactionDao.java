package se.spaced.server.persistence.dao.impl.inmemory;

import se.spaced.server.model.PersistedFaction;
import se.spaced.server.persistence.dao.interfaces.FactionDao;

public class InMemoryFactionDao extends FindableInMemoryDao<PersistedFaction> implements FactionDao {
}