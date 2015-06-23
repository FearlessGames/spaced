package se.spaced.client.model.player;

import com.google.inject.Singleton;
import se.spaced.client.model.ClientEntity;

@Singleton
public class PlayerEntityProvider {
	private ClientEntity player;

	public ClientEntity get() {
		return player;
	}

	public void setPlayerEntity(final ClientEntity player) {
		this.player = player;
	}

	public boolean isPlayerEntity(final ClientEntity entity) {
		return player.equals(entity);
	}
}
