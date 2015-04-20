package se.spaced.server.services;

import se.spaced.server.mob.brains.templates.BrainTemplate;
import se.spaced.server.model.Mob;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.currency.PersistedCurrency;
import se.spaced.server.model.items.ServerItemTemplate;
import se.spaced.server.model.spawn.MobTemplate;
import se.spaced.server.model.spell.ServerSpell;

public interface GameMasterService {
	void visit(ServerEntity gm, ServerEntity entityToVisit);

	void giveItem(ServerEntity gm, ServerEntity player, ServerItemTemplate template, int quantity);

	void reloadMob(ServerEntity gm, ServerEntity entity);

	void spawnMob(ServerEntity gm, MobTemplate mobTemplate, BrainTemplate brainTemplate);

	void reloadServerContent(ServerEntity gm);

	void grantSpell(ServerEntity gm, ServerEntity player, ServerSpell spell);

	void sendAiInfo(ServerEntity gm, Mob entity);

	void giveMoney(ServerEntity gm, ServerEntity player, PersistedCurrency currency, long amount);

	void summonEntity(ServerEntity gm, ServerEntity player);

	void forceException(ServerEntity gm, boolean includeActionLoop);
}
