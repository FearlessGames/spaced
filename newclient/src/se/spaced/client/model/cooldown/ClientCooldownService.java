package se.spaced.client.model.cooldown;

import se.spaced.messages.protocol.Cooldown;
import se.spaced.shared.activecache.ActiveCache;

public interface ClientCooldownService extends ActiveCache<Cooldown, ClientCooldown> {
}
