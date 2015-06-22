package se.spaced.server.persistence.dao.impl.hibernate;

import org.hibernate.SessionFactory;
import se.spaced.server.persistence.dao.interfaces.AccountDao;
import se.spaced.server.persistence.dao.interfaces.BrainTemplateDao;
import se.spaced.server.persistence.dao.interfaces.CreatureTypeDao;
import se.spaced.server.persistence.dao.interfaces.EntityTemplateDao;
import se.spaced.server.persistence.dao.interfaces.FactionDao;
import se.spaced.server.persistence.dao.interfaces.InventoryDao;
import se.spaced.server.persistence.dao.interfaces.ItemDao;
import se.spaced.server.persistence.dao.interfaces.ItemTemplateDao;
import se.spaced.server.persistence.dao.interfaces.LootTemplateDao;
import se.spaced.server.persistence.dao.interfaces.PlayerDao;
import se.spaced.server.persistence.dao.interfaces.SpawnPatternTemplateDao;
import se.spaced.server.persistence.dao.interfaces.SpellBookDao;
import se.spaced.server.persistence.dao.interfaces.SpellDao;
import se.spaced.server.persistence.migrator.Migrator;
import se.spaced.server.persistence.util.transactions.TransactionProxyWrapper;

import java.util.Collection;

public class DaoFactory {
	private final SessionFactory sessionFactory;
	private final TransactionProxyWrapper transactionProxyWrapper;

	public DaoFactory(SessionFactory sessionFactory, TransactionProxyWrapper transactionProxyWrapper) {
		this.sessionFactory = sessionFactory;
		this.transactionProxyWrapper = transactionProxyWrapper;
	}

	public AccountDao getAccountDao() {
		return transactionProxyWrapper.wrap(new AccountDaoImpl(sessionFactory));
	}

	public CreatureTypeDao getCreatureTypeDao() {
		return transactionProxyWrapper.wrap(new CreatureTypeDaoImpl(sessionFactory));
	}

	public PlayerDao getPlayerDao() {
		return transactionProxyWrapper.wrap(new PlayerDaoImpl(sessionFactory));
	}

	public SpellDao getSpellDao() {
		return transactionProxyWrapper.wrap(new SpellDaoImpl(sessionFactory));
	}

	public InventoryDao getInventoryDao() {
		return transactionProxyWrapper.wrap(new InventoryDaoImpl(sessionFactory));
	}

	public EntityTemplateDao getMobTemplateDao() {
		return transactionProxyWrapper.wrap(new EntityTemplateHibernateDao(sessionFactory));
	}

	public LootTemplateDao getLootTemplateDao() {
		return transactionProxyWrapper.wrap(new LootTemplateDaoImpl(sessionFactory));
	}

	public SpawnPatternTemplateDao getSpawnPatternTemplateDao() {
		return transactionProxyWrapper.wrap(new SpawnPatternTemplateDaoImpl(sessionFactory));
	}

	public FactionDao getFactionDao() {
		return transactionProxyWrapper.wrap(new FactionDaoImpl(sessionFactory));
	}

	public ItemDao getItemDao() {
		return transactionProxyWrapper.wrap(new ItemDaoImpl(sessionFactory));
	}

	public ItemTemplateDao getItemTemplateDao() {
		return transactionProxyWrapper.wrap(new ItemTemplateDaoImpl(sessionFactory));
	}

	public BrainTemplateDao getBrainTemplateDao() {
		return transactionProxyWrapper.wrap(new BrainTemplateDaoImpl(sessionFactory));
	}

	public void runMigrators(Collection<Migrator> migrators) {
		for (Migrator migrator : migrators) {
			migrator.execute();
		}
	}

	public SpellBookDao getSpellBookDao() {
		return transactionProxyWrapper.wrap(new SpellBookDaoImpl(sessionFactory));
	}
}
