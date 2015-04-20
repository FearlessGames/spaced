package se.spaced.server.net.listeners.auth;

import se.spaced.messages.protocol.Entity;
import se.spaced.server.model.Player;

public interface GameMasterApi {
	void visit(Player gm, String name);

	void giveItem(Player gm, String playerName, String templateName, int quantity);

	void reloadMob(Player gm, Entity entity);

	void spawnMob(Player gm, String mobTemplateName, String brainTemplateName);

	void reloadServerContent(Player gm);

	void grantSpell(Player gm, String playerName, String spellName);

	void requestAiInfo(Player gm, Entity entity);

	void giveMoney(Player gm, String playerName, String currencyName, long amount);

	void summonEntity(Player gm, String entityName);

	void forceException(Player gm, boolean includeActionLoop);
}
