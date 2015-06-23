package se.spaced.client.model;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.client.ardor.ui.events.SpellEvents;
import se.spaced.messages.protocol.Spell;
import se.spaced.shared.activecache.ActiveCache;
import se.spaced.shared.activecache.CacheUpdateListener;
import se.spaced.shared.activecache.Job;
import se.spaced.shared.events.EventHandler;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Singleton
public class SpellDirectory implements CacheUpdateListener<Spell, ClientSpell> {

	// TODO: SRP violation, move user spellbook to a different class
	private final Map<UUID, ClientSpell> usersSpells;
	private final EventHandler eventHandler;
	private final ActiveCache<Spell, ClientSpell> spellCache;

	@Inject
	public SpellDirectory(EventHandler eventHandler, ActiveCache<Spell, ClientSpell> spellCache) {
		this.eventHandler = eventHandler;
		this.spellCache = spellCache;
		usersSpells = Maps.newLinkedHashMap();
		spellCache.addListener(this);
	}

	public Collection<ClientSpell> getUsersSpells() {
		return Collections.unmodifiableCollection(usersSpells.values());
	}

	public ClientSpell getSpell(String spellName) {
		Collection<ClientSpell> allSpells = spellCache.getValues();
		for (ClientSpell spell : allSpells) {
			if (spellName.equals(spell.getName())) {
				return spell;
			}
		}
		return null;
	}

	public void setSpellbook(Collection<? extends Spell> spells) {
		clearSpellbook();
		final AtomicInteger countdown = new AtomicInteger(spells.size());
		for (final Spell spell : spells) {
			spellCache.runWhenReady(spell, new Job<ClientSpell>() {
				@Override
				public void run(ClientSpell value) {
					addToSpellbook(value);
					if (countdown.decrementAndGet() == 0) {
						spellbookUpdated();
					}
				}
			});
		}
	}

	public void addSpellToSpellbook(Spell spell) {
		usersSpells.put(spell.getPk(), spellCache.getValue(spell));
		spellbookUpdated();
	}

	private void clearSpellbook() {
		usersSpells.clear();
	}

	private void addToSpellbook(ClientSpell value) {
		usersSpells.put(value.getPk(), value);
	}

	private void spellbookUpdated() {
		eventHandler.fireAsynchEvent(SpellEvents.SPELLBOOK_UPDATED);
	}

	@Override
	public void updatedValue(Spell key, ClientSpell oldValue, ClientSpell value) {
		addedValue(key, value);
	}

	@Override
	public void deletedValue(Spell key, ClientSpell oldValue) {
		if (usersSpells.containsKey(key.getPk())) {
			usersSpells.remove(key.getPk());
			spellbookUpdated();
		}
	}

	@Override
	public void addedValue(Spell key, ClientSpell value) {
		if (usersSpells.containsKey(key.getPk())) {
			usersSpells.put(key.getPk(), value);
			spellbookUpdated();
		}
	}
}
