package se.spaced.client.net.messagelisteners;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.client.game.logic.remote.RemoteChatLogic;
import se.spaced.client.model.ClientEntity;
import se.spaced.messages.protocol.Entity;
import se.spaced.messages.protocol.s2c.ServerChatMessages;
import se.spaced.shared.activecache.ActiveCache;
import se.spaced.shared.activecache.Job;

@Singleton
public class ServerChatMessagesImpl implements ServerChatMessages {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final RemoteChatLogic chatLogic;
	private final ActiveCache<Entity, ClientEntity> entityCache;


	@Inject
	public ServerChatMessagesImpl(RemoteChatLogic chatLogic, ActiveCache<Entity, ClientEntity> entityCache) {
		this.chatLogic = chatLogic;
		this.entityCache = entityCache;
	}

	@Override
	public void playerSaid(String name, String message) {
		chatLogic.playerSaid(name, message);
	}

	@Override
	public void whisperFrom(String name, String message) {
		chatLogic.playerWhispered(name, message);
	}

	@Override
	public void whisperTo(String name, String message) {
		chatLogic.selfWhispered(name, message);
	}

	@Override
	public void systemMessage(String message) {
		chatLogic.systemMessage(message);
	}

	@Override
	public void emote(Entity performer, final String emoteFile, final String emoteText) {
		entityCache.runWhenReady(performer, new Job<ClientEntity>() {
			@Override
			public void run(final ClientEntity clientEntity) {
				chatLogic.playerEmoted(clientEntity, emoteText, emoteFile);
			}
		});
	}
}