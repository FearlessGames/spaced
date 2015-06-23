package se.spaced.client.game.logic.implementations.chat;

import com.google.inject.Inject;
import se.spaced.client.game.logic.local.LocalChatLogic;
import se.spaced.client.game.logic.remote.RemoteChatLogic;
import se.spaced.client.model.ClientEntity;
import se.spaced.client.net.smrt.ServerConnection;
import se.spaced.shared.util.ListenerDispatcher;

public class ChatLogicImpl implements RemoteChatLogic, LocalChatLogic {
	private final ServerConnection serverConnection;
	private final ListenerDispatcher<ChatLogicListener> dispatcher;

	@Inject
	public ChatLogicImpl(ListenerDispatcher<ChatLogicListener> dispatcher, ServerConnection serverConnection) {
		this.dispatcher = dispatcher;
		this.serverConnection = serverConnection;
	}

	// Remote
	@Override
	public void playerSaid(String player, String message) {
		dispatcher.trigger().playerSaid(player, message);
	}

	@Override
	public void playerWhispered(String fromName, String message) {
		dispatcher.trigger().playerWhispered(fromName, message);
	}

	@Override
	public void selfWhispered(String toName, String message) {
		dispatcher.trigger().selfWhispered(toName, message);
	}

	// Local
	@Override
	public void say(String message) {
		serverConnection.getReceiver().chat().say(message);
	}

	@Override
	public void whisper(String playerName, String message) {
		serverConnection.getReceiver().chat().whisper(playerName, message);
	}

	@Override
	public void systemMessage(String message) {
		dispatcher.trigger().systemMessage(message);
	}

	@Override
	public void playerEmoted(ClientEntity clientEntity, String emoteText, String emoteFile) {
		dispatcher.trigger().playerEmoted(clientEntity, emoteText, emoteFile);
	}
}
