package se.spaced.server.net.listeners.auth;

import se.spaced.messages.protocol.Entity;
import se.spaced.messages.protocol.c2s.ClientGameMasterMessages;
import se.spaced.server.net.ClientConnection;

public class ClientGameMasterMessagesAuth implements ClientGameMasterMessages {
	private final GameMasterApi gameMasterApi;
	private final ClientConnection clientConnection;

	public ClientGameMasterMessagesAuth(GameMasterApi gameMasterApi, ClientConnection clientConnection) {
		this.gameMasterApi = gameMasterApi;
		this.clientConnection = clientConnection;
	}

	@Override
	public void visit(String name) {
		gameMasterApi.visit(clientConnection.getPlayer(), name);
	}

	@Override
	public void giveItem(String playerName, String templateName, int quantity) {
		gameMasterApi.giveItem(clientConnection.getPlayer(), playerName, templateName, quantity);
	}

	@Override
	public void reloadMob(Entity entity) {
		gameMasterApi.reloadMob(clientConnection.getPlayer(), entity);
	}

	@Override
	public void spawnMob(String mobTemplate, String brainTemplate) {
		gameMasterApi.spawnMob(clientConnection.getPlayer(), mobTemplate, brainTemplate);
	}

	@Override
	public void reloadServerContent() {
		gameMasterApi.reloadServerContent(clientConnection.getPlayer());
	}

	@Override
	public void grantSpell(String playerName, String spellName) {
		gameMasterApi.grantSpell(clientConnection.getPlayer(), playerName, spellName);
	}

	@Override
	public void requestAiInfo(Entity mob) {
		gameMasterApi.requestAiInfo(clientConnection.getPlayer(), mob);
	}

	@Override
	public void giveMoney(String playerName, String currency, long amount) {
		gameMasterApi.giveMoney(clientConnection.getPlayer(), playerName, currency, amount);
	}

	@Override
	public void summonEntity(String entityName) {
		gameMasterApi.summonEntity(clientConnection.getPlayer(), entityName);
	}

	@Override
	public void forceExceptionServerSide(boolean includeActionLoop) {
		gameMasterApi.forceException(clientConnection.getPlayer(), includeActionLoop);
	}

}
