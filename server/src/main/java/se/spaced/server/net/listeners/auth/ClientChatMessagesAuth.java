package se.spaced.server.net.listeners.auth;

import se.spaced.messages.protocol.c2s.ClientChatMessages;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.Player;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.entity.EntityService;
import se.spaced.server.net.ClientConnection;
import se.spaced.server.net.broadcast.SmrtBroadcaster;

public class ClientChatMessagesAuth implements ClientChatMessages {

	private static final double CHAT_RADIUS = 300d;

	private final ClientConnection clientConnection;
	private final SmrtBroadcaster<S2CProtocol> broadcaster;
	private final EntityService entityService;

	public ClientChatMessagesAuth(SmrtBroadcaster<S2CProtocol> smrtBroadcaster,
											ClientConnection clientConnection,
											EntityService entityService) {
		this.broadcaster = smrtBroadcaster;
		this.clientConnection = clientConnection;
		this.entityService = entityService;
	}

	@Override
	public void say(String message) {
		Player player = clientConnection.getPlayer();
		String playerName = player.getName();
		broadcaster.create().toAll().send().chat().playerSaid(playerName, message);
	}

	@Override
	public void whisper(String name, String message) {
		Player player = clientConnection.getPlayer();
		ServerEntity entity = entityService.findEntityByName(name);
		if (entity != null) {
			broadcaster.create().to(entity).send().chat().whisperFrom(player.getName(), message);
			clientConnection.getReceiver().chat().whisperTo(name, message);
		} else {
			clientConnection.getReceiver().chat().systemMessage("Can't find a player with the name " + name);
		}
	}
}
