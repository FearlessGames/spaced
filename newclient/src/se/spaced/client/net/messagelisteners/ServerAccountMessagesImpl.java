package se.spaced.client.net.messagelisteners;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.client.ardor.ui.api.ConnectionApi;
import se.spaced.client.ardor.ui.events.AccountEvents;
import se.spaced.messages.protocol.s2c.ServerAccountMessages;
import se.spaced.shared.events.EventHandler;
import se.spaced.shared.model.player.PlayerCreationFailure;
import se.spaced.shared.network.protocol.codec.datatype.EntityData;

@Singleton
public class ServerAccountMessagesImpl implements ServerAccountMessages {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final EventHandler eventHandler;
	private final ConnectionApi connectionApi;


	@Inject
	public ServerAccountMessagesImpl(EventHandler eventHandler, ConnectionApi connectionApi) {
		this.eventHandler = eventHandler;
		this.connectionApi = connectionApi;
	}

	@Override
	public void playerCreated(EntityData character) {
		log.debug("Created player with name: " + character.getName() + " with UUID: " + character.getId().toString());
		eventHandler.fireAsynchEvent(AccountEvents.PLAYER_CREATED, character.getName(), character.getId().toString());
		connectionApi.addCharacter(character);
	}

	@Override
	public void failedToCreatePlayer(String name, PlayerCreationFailure reason) {
		log.debug("Failed to create player with name '" + name + "' with reason: " + reason.toString());
		eventHandler.fireAsynchEvent(AccountEvents.PLAYER_CREATION_FAILED, name, reason);	
	}
}
