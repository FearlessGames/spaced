package se.spaced.client.sound;

import com.google.inject.Inject;
import se.spaced.client.game.logic.implementations.chat.ChatLogicListener;
import se.spaced.client.model.ClientEntity;
import se.spaced.shared.activecache.Job;
import se.spaced.shared.util.ListenerDispatcher;

public class ChatSounds implements ChatLogicListener {
	private final SoundLoader soundLoader;

	@Inject
	public ChatSounds(ListenerDispatcher<ChatLogicListener> dispatcher, SoundLoader soundLoader) {
		this.soundLoader = soundLoader;
		dispatcher.addListener(this);
	}

	@Override
	public void systemMessage(String message) {
	}

	@Override
	public void playerSaid(String player, String message) {
	}

	@Override
	public void playerWhispered(String fromPlayer, String message) {
	}

	@Override
	public void selfWhispered(String toPlayer, String message) {
	}

	@Override
	public void playerEmoted(final ClientEntity entity, String emoteText, String emoteFile) {
		soundLoader.runWhenReady(emoteFile, new Job<SoundSource>() {
			@Override
			public void run(SoundSource soundSource) {
				soundSource.setPosition(entity.getPosition());
				soundSource.setGain(5);
				soundSource.play();
			}
		});
	}
}
