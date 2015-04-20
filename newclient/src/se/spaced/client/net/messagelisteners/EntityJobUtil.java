package se.spaced.client.net.messagelisteners;

import se.spaced.client.model.ClientEntity;
import se.spaced.client.model.ClientSpell;
import se.spaced.messages.protocol.Entity;
import se.spaced.messages.protocol.Spell;
import se.spaced.shared.activecache.ActiveCache;
import se.spaced.shared.activecache.Job;

public class EntityJobUtil {
	private final ActiveCache<Entity, ClientEntity> entityCache;
	private final ActiveCache<Spell, ClientSpell> spellCache;

	public EntityJobUtil(ActiveCache<Entity, ClientEntity> entityCache, ActiveCache<Spell, ClientSpell> spellCache) {
		this.entityCache = entityCache;
		this.spellCache = spellCache;
	}

	public void run(Entity first, final Entity second, final EntityEntityJob job) {
		entityCache.runWhenReady(first, new Job<ClientEntity>() {
			@Override
			public void run(final ClientEntity clientFirst) {
				entityCache.runWhenReady(second, new Job<ClientEntity>() {
					@Override
					public void run(ClientEntity clientSecond) {
						job.run(clientFirst, clientSecond);
					}
				});
			}
		});
	}

	public void run(Entity first, final Entity second, final Spell spell, final EntityEntitySpellJob job) {
		entityCache.runWhenReady(first, new Job<ClientEntity>() {
			@Override
			public void run(final ClientEntity clientFirst) {
				entityCache.runWhenReady(second, new Job<ClientEntity>() {
					@Override
					public void run(final ClientEntity clientSecond) {
						spellCache.runWhenReady(spell, new Job<ClientSpell>() {
							@Override
							public void run(ClientSpell value) {
								job.run(clientFirst, clientSecond, value);
							}
						});
					}
				});
			}
		});
	}

}
