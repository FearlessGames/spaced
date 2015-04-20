package se.spaced.server.spell;

import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.spell.ServerSpell;

import java.util.Collection;

public interface SpellService {

	Collection<ServerSpell> getAllSpells();

	ServerSpell getSpellById(UUID spellId);

	Collection<ServerSpell> getSpellsForEntity(ServerEntity entity);

	void addSpellForEntity(ServerEntity entity, ServerSpell spell);

	boolean entityHasSpell(ServerEntity entity, ServerSpell spell);

	ServerSpell findByName(String spellName);

	void createSpellBookForEntity(ServerEntity entity);
}
