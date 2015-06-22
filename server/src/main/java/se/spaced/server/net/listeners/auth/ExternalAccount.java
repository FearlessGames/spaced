package se.spaced.server.net.listeners.auth;

import se.fearlessgames.common.util.uuid.UUID;

public class ExternalAccount {
	private final UUID uuid;
	private final int type;

	public ExternalAccount(UUID uuid, int type) {
		this.uuid = uuid;
		this.type = type;
	}

	public UUID getUuid() {
		return uuid;
	}

	public int getType() {
		return type;
	}
}
