package se.spaced.messages.protocol.c2s;

import se.smrt.core.SmrtProtocol;
import se.spaced.messages.protocol.Entity;

@SmrtProtocol
public interface ClientGameMasterMessages {
	void visit(String name);

	void giveItem(String playerName, String templateName, int quantity);

	void reloadMob(Entity entity);

	void spawnMob(String mobTemplate, String brainTemplate);

	void reloadServerContent();

	void grantSpell(String playerName, String spellName);

	void requestAiInfo(Entity mob);

	void giveMoney(String playerName, String currency, long amount);

	void summonEntity(String entityName);

	void forceExceptionServerSide(boolean includeActionLoop);

}
