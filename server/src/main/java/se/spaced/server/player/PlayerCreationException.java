package se.spaced.server.player;

import se.spaced.shared.model.player.PlayerCreationFailure;

public class PlayerCreationException extends Exception {

	private final PlayerCreationFailure playerCreationFailure;

	public PlayerCreationException(String message, PlayerCreationFailure playerCreationFailure) {
		super(message);
		this.playerCreationFailure = playerCreationFailure;
	}

	public PlayerCreationFailure getPlayerCreationFailure() {
		return playerCreationFailure;	
	}
}