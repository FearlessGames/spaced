package se.spaced.client.view.ardor;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearless.common.util.TimeProvider;
import se.spaced.client.ardor.ui.events.ChatEvents;
import se.spaced.client.game.logic.implementations.chat.ChatLogicListener;
import se.spaced.client.model.ClientEntity;
import se.spaced.shared.events.EventHandler;

import java.util.Date;

public class ArdorChat implements ChatLogicListener {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final EventHandler eventHandler;
	private final TimeProvider timeProvider;

	@Inject
	public ArdorChat(EventHandler eventHandler, TimeProvider timeProvider) {
		this.eventHandler = eventHandler;
		this.timeProvider = timeProvider;
	}

	@Override
	public void playerSaid(String player, String message) {
		logger.info("Ardor got playerSaid trigger.");
		eventHandler.fireAsynchEvent(ChatEvents.PLAYER_SAY, player, message, now());
	}

	private Date now() {
		return new Date(timeProvider.now());
	}

	@Override
	public void playerWhispered(String fromPlayer, String message) {
		logger.info("Ardor got playerWhispered trigger.");
		eventHandler.fireEvent(ChatEvents.PLAYER_WHISPER, fromPlayer, message, now());
	}

	@Override
	public void selfWhispered(String toPlayer, String message) {
		logger.info("Ardor got selfWhispered trigger.");
		eventHandler.fireEvent(ChatEvents.SELF_WHISPER, toPlayer, message, now());
	}

	@Override
	public void playerEmoted(ClientEntity entity, String emoteText, String emoteFile) {
		eventHandler.fireAsynchEvent(ChatEvents.PLAYER_EMOTE, entity.getName(), emoteText, now());
	}

	@Override
	public void systemMessage(String message) {
		logger.info("Ardor got systemMessage trigger.");
		eventHandler.fireEvent(ChatEvents.SYSTEM_MESSAGE, message, now());
	}
}