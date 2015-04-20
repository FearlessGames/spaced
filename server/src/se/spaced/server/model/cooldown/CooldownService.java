package se.spaced.server.model.cooldown;

import se.spaced.messages.protocol.Cooldown;

public interface CooldownService {
	CooldownTemplate find(Cooldown cooldown);
}
