package se.spaced.client.net.messagelisteners;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.client.ardor.ui.events.GameMasterEvents;
import se.spaced.messages.protocol.s2c.ServerGameMasterMessages;
import se.spaced.shared.events.EventHandler;

public class ServerGameMasterMessagesImpl implements ServerGameMasterMessages {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private final EventHandler eventHandler;

	@Inject
	public ServerGameMasterMessagesImpl(EventHandler eventHandler) {
		this.eventHandler = eventHandler;
	}

	@Override
	public void successNotification(String message) {
		log.info("GM: " + message);
		eventHandler.fireAsynchEvent(GameMasterEvents.GM_SUCCESS_NOTIFICATION, message);
	}

	@Override
	public void failureNotification(String message) {
		log.info("GM: " + message);
		eventHandler.fireAsynchEvent(GameMasterEvents.GM_FAILURE_NOTIFICATION, message);
	}
}
