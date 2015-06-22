package se.spaced.server.persistence.migrator;

import com.google.inject.Inject;
import org.hibernate.Transaction;
import se.fearlessgames.common.util.uuid.UUIDFactoryImpl;
import se.spaced.server.model.PersistedFaction;
import se.spaced.server.persistence.dao.impl.hibernate.TransactionManager;
import se.spaced.server.persistence.dao.interfaces.FactionDao;

public class MockFactionPopulator implements Migrator {
	private final TransactionManager transactionManager;
	private final FactionDao factionDao;

	@Inject
	public MockFactionPopulator(TransactionManager transactionManager, FactionDao factionDao) {
		this.transactionManager = transactionManager;
		this.factionDao = factionDao;
	}

	@Override
	public void execute() {
		Transaction transaction = transactionManager.beginTransaction();
		if (!factionDao.findAll().isEmpty()) {
			transaction.commit();
			return;
		}

		factionDao.persist(new PersistedFaction(UUIDFactoryImpl.INSTANCE.combUUID(), "players"));
		factionDao.persist(new PersistedFaction(UUIDFactoryImpl.INSTANCE.combUUID(), "bots"));
		factionDao.persist(new PersistedFaction(UUIDFactoryImpl.INSTANCE.combUUID(), "mobs"));
		factionDao.persist(new PersistedFaction(UUIDFactoryImpl.INSTANCE.combUUID(), "borgpigs"));
		factionDao.persist(new PersistedFaction(UUIDFactoryImpl.INSTANCE.combUUID(), "crates"));
		factionDao.persist(new PersistedFaction(UUIDFactoryImpl.INSTANCE.combUUID(), "dcs"));
		transaction.commit();
	}
}