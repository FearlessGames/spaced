package se.spaced.client.net.messagelisteners;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearless.common.time.TimeProvider;
import se.spaced.client.model.ClientSpell;
import se.spaced.client.model.SpellDirectory;
import se.spaced.client.model.cooldown.ClientCooldown;
import se.spaced.client.model.spelleffects.ClientGrantSpellEffect;
import se.spaced.messages.protocol.AuraTemplate;
import se.spaced.messages.protocol.Cooldown;
import se.spaced.messages.protocol.Spell;
import se.spaced.messages.protocol.s2c.ServerSpellMessages;
import se.spaced.shared.activecache.ActiveCache;
import se.spaced.shared.activecache.Job;
import se.spaced.shared.network.protocol.codec.datatype.SpellData;
import se.spaced.shared.network.protocol.codec.datatype.SpellEffect;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Singleton
public class SpellListener implements ServerSpellMessages {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final SpellDirectory spellDirectory;
	private final ActiveCache<Spell, ClientSpell> spellCache;
	private final ActiveCache<Cooldown, ClientCooldown> cooldownCache;
	private final TimeProvider timeProvider;

	@Inject
	public SpellListener(
			SpellDirectory spellDirectory,
			ActiveCache<Spell, ClientSpell> spellCache,
			ActiveCache<Cooldown, ClientCooldown> cooldownCache, TimeProvider timeProvider) {
		this.spellDirectory = spellDirectory;
		this.spellCache = spellCache;
		this.cooldownCache = cooldownCache;
		this.timeProvider = timeProvider;
	}


	@Override
	public void spellBookInfo(Collection<? extends Spell> spells) {
		spellDirectory.setSpellbook(spells);
	}

	@Override
	public void spellData(Collection<SpellData> spellData) {
		for (final SpellData data : spellData) {
			handleSpellData(data);
		}
	}

	private void handleSpellData(final SpellData data) {
		final Collection<? extends Cooldown> cooldowns = data.getCooldowns();
		final int numCooldowns = cooldowns.size();
		if (numCooldowns == 0) {
			addSpellToCache(data, Collections.<ClientCooldown>emptyList());
		} else {
			final List<ClientCooldown> clientCooldowns = Lists.newArrayListWithCapacity(numCooldowns);

			for (Cooldown cooldown : cooldowns) {
				cooldownCache.runWhenReady(cooldown, new Job<ClientCooldown>() {
					@Override
					public void run(ClientCooldown value) {
						synchronized (clientCooldowns) {
							clientCooldowns.add(value);
							if (clientCooldowns.size() == numCooldowns) {
								addSpellToCache(data, clientCooldowns);
							}
						}
					}
				});
			}
		}
	}

	private void addSpellToCache(SpellData data, List<ClientCooldown> clientCooldowns) {
		final ClientSpell clientSpell = new ClientSpell(data, clientCooldowns, timeProvider);
		for (SpellEffect spellEffect : data.getSpellEffects()) {
			if (spellEffect instanceof ClientGrantSpellEffect) {
				final ClientGrantSpellEffect grantEffect = (ClientGrantSpellEffect) spellEffect;
				spellCache.runWhenReady(grantEffect.getSpell(), new Job<ClientSpell>() {
					@Override
					public void run(ClientSpell value) {
						grantEffect.setClientSpell(value);
						ClientGrantSpellEffect updatedGrantEffect = new ClientGrantSpellEffect(value, grantEffect.getPk());
						clientSpell.updateSpellEffect(updatedGrantEffect);
					}
				});
			}
		}
		spellCache.setValue(clientSpell, clientSpell);
	}

	@Override
	public void spellAdded(Spell spell) {
		log.info("Spell was added");
		spellDirectory.addSpellToSpellbook(spell);
	}

	@Override
	public void auraInfo(AuraTemplate template) {
		// TODO: add an active cache for aura templates?
	}
}
