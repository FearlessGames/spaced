package se.spaced.server.persistence.migrator;

import com.google.inject.Inject;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearlessgames.common.util.uuid.UUIDFactoryImpl;
import se.spaced.server.model.PersistedCreatureType;
import se.spaced.server.persistence.dao.impl.hibernate.TransactionManager;
import se.spaced.server.persistence.dao.interfaces.CreatureTypeDao;

public class MockCreatureTypePopulator implements Migrator {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final TransactionManager transactionManager;
	private final CreatureTypeDao creatureTypeDao;

	@Inject
	public MockCreatureTypePopulator(
			TransactionManager transactionManager, CreatureTypeDao creatureTypeDao) {
		this.transactionManager = transactionManager;
		this.creatureTypeDao = creatureTypeDao;
	}

	@Override
	public void execute() {
		Transaction transaction = transactionManager.beginTransaction();

		creatureTypeDao.persist(new PersistedCreatureType(UUIDFactoryImpl.INSTANCE.combUUID(), "humanoid"));
		creatureTypeDao.persist(new PersistedCreatureType(UUIDFactoryImpl.INSTANCE.combUUID(), "mechanical"));
		creatureTypeDao.persist(new PersistedCreatureType(UUIDFactoryImpl.INSTANCE.combUUID(), "beast"));
		transaction.commit();
	}
}