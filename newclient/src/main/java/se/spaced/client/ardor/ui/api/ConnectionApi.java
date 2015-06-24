package se.spaced.client.ardor.ui.api;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.fearless.common.uuid.UUID;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.krka.kahlua.integration.expose.ReturnValues;
import se.krka.kahlua.vm.KahluaTable;
import se.spaced.client.game.logic.local.LocalLoginLogic;
import se.spaced.client.model.ClientEntity;
import se.spaced.client.model.listener.ClientEntityListener;
import se.spaced.client.net.ping.PingManager;
import se.spaced.client.net.remoteservices.ClientsideServerInfo;
import se.spaced.client.net.remoteservices.ServerInfoWSC;
import se.spaced.client.net.smrt.ServerConnection;
import se.spaced.messages.protocol.Salts;
import se.spaced.shared.network.protocol.codec.datatype.EntityData;
import se.spaced.shared.util.ListenerDispatcher;

import java.util.List;

@Singleton
public class ConnectionApi {
	private final LocalLoginLogic localLoginLogic;
	private final ServerInfoWSC remoteService;
	private final ServerConnection serverConnection;
	private final PingManager pingManager;
	private final ListenerDispatcher<ClientEntityListener> entityDispatcher;

	private List<ClientEntity> characters = Lists.newArrayList();

	@Inject
	public ConnectionApi(
			LocalLoginLogic localLoginLogic, ServerConnection serverConnection, PingManager pingManager,
			ServerInfoWSC remoteService,
			ListenerDispatcher<ClientEntityListener> entityDispatcher) {
		this.remoteService = remoteService;
		this.localLoginLogic = localLoginLogic;
		this.serverConnection = serverConnection;
		this.pingManager = pingManager;
		this.entityDispatcher = entityDispatcher;
	}

	@LuaMethod(global = true, name = "Connect")
	public void connect(String host, int port) {
		serverConnection.connect(host, port);
	}

	@LuaMethod(global = true, name = "LoginAccount")
	public void loginAccount(String username, String password, Salts authSalts) {
		if (isConnected()) {
			localLoginLogic.loginAccount(username, password, authSalts);
		}
	}

	@LuaMethod(global = true, name = "AuthenticateAccount")
	public void authenticateAccount(String username, String password, Salts authSalts, String key) {
		if (isConnected()) {
			localLoginLogic.authenticateAccount(username, password, authSalts, key);
		}
	}

	@LuaMethod(global = true, name = "RequestAuthSalts")
	public void requestAuthSalts(String userName) {
		if (isConnected()) {
			localLoginLogic.requestAuthSalts(userName);
		}
	}

	@LuaMethod(global = true, name = "LoginCharacter")
	public void loginCharacter(String characterUUID) {
		if (isConnected()) {
			localLoginLogic.loginCharacter(UUID.fromString(characterUUID));
		}
	}

	@LuaMethod(global = true, name = "GetServers")
	public List<KahluaTable> getServers() {
		List<KahluaTable> tables = Lists.newArrayList();
		for (ClientsideServerInfo serverInfo : remoteService.getServers()) {
			tables.add(serverInfo.toLuaTable());
		}
		return tables;
	}

	@LuaMethod(global = true, name = "UpdateServers")
	public void updateServers() {
		remoteService.fetchServerInfo();
	}

	@LuaMethod(global = true, name = "IsConnected")
	public boolean isConnected() {
		return serverConnection.isConnected();
	}

	@LuaMethod(global = true, name = "GetNetStats")
	public void getNetStats(ReturnValues r) {
		r.push(serverConnection.getDownSpeed());
		r.push(serverConnection.getUpSpeed());
		r.push(pingManager.getLatency());
	}

	@LuaMethod(global = true, name = "GetCharacters")
	public List<ClientEntity> getCharacters() {
		return characters;
	}

	@LuaMethod(global = true, name = "RequestCharInfo")
	public void requestCharInfo(ClientEntity entity) {
		serverConnection.getReceiver().connection().requestPlayerInfo(entity.getPk());
	}

	public void setCharacters(List<EntityData> characterList) {
		characters.clear();
		for (EntityData entityData : characterList) {
			addCharacter(entityData);
		}
	}

	public void addCharacter(EntityData character) {
		characters.add(new ClientEntity(character, entityDispatcher));
	}
}
