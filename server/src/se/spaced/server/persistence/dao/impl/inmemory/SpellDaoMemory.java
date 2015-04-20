package se.spaced.server.persistence.dao.impl.inmemory;

import com.google.inject.Singleton;
import se.spaced.server.model.spell.ServerSpell;

@Singleton
public class SpellDaoMemory extends FindableInMemoryDao<ServerSpell> {
}
