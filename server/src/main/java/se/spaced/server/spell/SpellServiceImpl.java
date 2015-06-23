package se.spaced.server.spell;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import org.hibernate.Transaction;
import se.fearless.common.time.TimeProvider;
import se.fearless.common.uuid.UUID;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.server.model.spell.SpellBook;
import se.spaced.server.persistence.dao.impl.hibernate.TransactionManager;
import se.spaced.server.persistence.dao.interfaces.SpellBookDao;
import se.spaced.server.persistence.dao.interfaces.SpellDao;
import se.spaced.server.persistence.util.transactions.AutoTransaction;
import se.spaced.shared.util.CacheUpdater;
import se.spaced.shared.util.CachedValue;
import se.spaced.shared.util.TimeConverter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SpellServiceImpl implements SpellService, CacheUpdater<Map<UUID, ServerSpell>> {
	private static final long CACHE_TIMEOUT = TimeConverter.THIRTY_MINUTES.getTimeInMillis();
	private final CachedValue<Map<UUID, ServerSpell>> cachedSpellMap;
	private final SpellDao spellDao;
	private final SpellBookDao spellBookDao;
	private final TransactionManager transactionManager;
	private final Map<ServerEntity, Collection<ServerSpell>> entitySpellCache;

	@Inject
	public SpellServiceImpl(
			TransactionManager transactionManager,
			TimeProvider timeProvider,
			SpellDao spellDao,
			SpellBookDao spellBookDao) {
		this.transactionManager = transactionManager;
		this.spellDao = spellDao;
		this.spellBookDao = spellBookDao;
		cachedSpellMap = new CachedValue<Map<UUID, ServerSpell>>(timeProvider, CACHE_TIMEOUT, this);
		entitySpellCache = new HashMap<ServerEntity, Collection<ServerSpell>>();
	}

	@Override
	@AutoTransaction
	public Collection<ServerSpell> getAllSpells() {
		return spellDao.findAll();
	}

	@Override
	public ServerSpell getSpellById(UUID spellId) {
		return cachedSpellMap.getCachedData().get(spellId);
	}

	@Override
	@AutoTransaction
	public Collection<ServerSpell> getSpellsForEntity(ServerEntity entity) {
		SpellBook spellBook = spellBookDao.findByOwner(entity);
		entitySpellCache.put(entity, spellBook.getSpells());
		return spellBook.getSpells();
	}

	@Override
	@AutoTransaction
	public void addSpellForEntity(ServerEntity entity, ServerSpell spell) {
		SpellBook spellBook = spellBookDao.findByOwner(entity);
		spellBook.addSpell(spell);
		entitySpellCache.put(entity, spellBook.getSpells());
	}

	@Override
	public Map<UUID, ServerSpell> refreshCashedData() {
		Map<UUID, ServerSpell> map = Maps.newHashMap();
		Transaction tx = transactionManager.beginTransaction();
		for (ServerSpell spell : spellDao.findAll()) {
			map.put(spell.getPk(), spell);
		}
		tx.commit();
		return map;
	}

	@Override
	@AutoTransaction
	public boolean entityHasSpell(ServerEntity entity, ServerSpell spell) {
		Collection<ServerSpell> spells = entitySpellCache.get(entity);
		if (spells == null) {
			spells = getSpellsForEntity(entity);
		}
		return spells.contains(spell);
	}

	@Override
	@AutoTransaction
	public ServerSpell findByName(String spellName) {
		return spellDao.findByName(spellName);
	}

	@Override
	@AutoTransaction
	public void createSpellBookForEntity(ServerEntity entity) {
		SpellBook playersSpellBook = new SpellBook(entity);
		spellBookDao.persist(playersSpellBook);
	}
}
