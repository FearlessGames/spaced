package se.spaced.server.net.listeners.auth;

import se.spaced.messages.protocol.Salts;

public interface AuthSaltsCallback {
	void receievedSalts(Salts salts);
}
