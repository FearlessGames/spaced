package se.spaced.server.services;

import se.spaced.server.model.Player;

import java.util.Date;

public class PlayerConnectionInfo {
	private final Player player;
	private final Date date;

	public PlayerConnectionInfo(Player player, Date date) {
		this.player = player;
		this.date = date;
	}

	public Player getPlayer() {
		return player;
	}

	public Date getDate() {
		return date;
	}
}
