package se.spaced.server.persistence.dao.impl.inmemory;

import se.spaced.server.model.cooldown.CooldownSetTemplate;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.server.persistence.dao.interfaces.SpellDao;

public class InMemorySpellDao extends FindableInMemoryDao<ServerSpell> implements SpellDao {
	@Override
	public ServerSpell persist(ServerSpell obj) {
		final CooldownSetTemplate template = obj.getCoolDown();
		if (template.getPk() == null) {
			template.setPk(obj.getPk());
		}
		return super.persist(obj);
	}
	
}
