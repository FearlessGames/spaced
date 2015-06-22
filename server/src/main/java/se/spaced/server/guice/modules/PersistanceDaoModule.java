package se.spaced.server.guice.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.hibernate.SessionFactory;
import se.spaced.server.persistence.dao.impl.hibernate.AccountDaoImpl;
import se.spaced.server.persistence.dao.impl.hibernate.AuraDaoImpl;
import se.spaced.server.persistence.dao.impl.hibernate.BrainTemplateDaoImpl;
import se.spaced.server.persistence.dao.impl.hibernate.CooldownTemplateDaoImpl;
import se.spaced.server.persistence.dao.impl.hibernate.CreatureTypeDaoImpl;
import se.spaced.server.persistence.dao.impl.hibernate.CurrencyDaoImpl;
import se.spaced.server.persistence.dao.impl.hibernate.EntityTemplateHibernateDao;
import se.spaced.server.persistence.dao.impl.hibernate.EquipmentDaoImpl;
import se.spaced.server.persistence.dao.impl.hibernate.FactionDaoImpl;
import se.spaced.server.persistence.dao.impl.hibernate.GraveyardTemplateDaoImpl;
import se.spaced.server.persistence.dao.impl.hibernate.InventoryDaoImpl;
import se.spaced.server.persistence.dao.impl.hibernate.ItemDaoImpl;
import se.spaced.server.persistence.dao.impl.hibernate.ItemTemplateDaoImpl;
import se.spaced.server.persistence.dao.impl.hibernate.KillEntryDaoImpl;
import se.spaced.server.persistence.dao.impl.hibernate.LootTemplateDaoImpl;
import se.spaced.server.persistence.dao.impl.hibernate.MobTemplateHibernateDao;
import se.spaced.server.persistence.dao.impl.hibernate.PlayerDaoImpl;
import se.spaced.server.persistence.dao.impl.hibernate.SessionFactoryProvider;
import se.spaced.server.persistence.dao.impl.hibernate.SpawnPatternTemplateDaoImpl;
import se.spaced.server.persistence.dao.impl.hibernate.SpellActionEntryDaoImpl;
import se.spaced.server.persistence.dao.impl.hibernate.SpellBookDaoImpl;
import se.spaced.server.persistence.dao.impl.hibernate.SpellDaoImpl;
import se.spaced.server.persistence.dao.impl.hibernate.WalletDaoImpl;
import se.spaced.server.persistence.dao.impl.hibernate.types.EntityStatsUserType;
import se.spaced.server.persistence.dao.interfaces.AccountDao;
import se.spaced.server.persistence.dao.interfaces.AuraDao;
import se.spaced.server.persistence.dao.interfaces.BrainTemplateDao;
import se.spaced.server.persistence.dao.interfaces.CooldownTemplateDao;
import se.spaced.server.persistence.dao.interfaces.CreatureTypeDao;
import se.spaced.server.persistence.dao.interfaces.CurrencyDao;
import se.spaced.server.persistence.dao.interfaces.EntityTemplateDao;
import se.spaced.server.persistence.dao.interfaces.EquipmentDao;
import se.spaced.server.persistence.dao.interfaces.FactionDao;
import se.spaced.server.persistence.dao.interfaces.GraveyardTemplateDao;
import se.spaced.server.persistence.dao.interfaces.InventoryDao;
import se.spaced.server.persistence.dao.interfaces.ItemDao;
import se.spaced.server.persistence.dao.interfaces.ItemTemplateDao;
import se.spaced.server.persistence.dao.interfaces.KillEntryDao;
import se.spaced.server.persistence.dao.interfaces.LootTemplateDao;
import se.spaced.server.persistence.dao.interfaces.MobTemplateDao;
import se.spaced.server.persistence.dao.interfaces.PlayerDao;
import se.spaced.server.persistence.dao.interfaces.SpawnPatternTemplateDao;
import se.spaced.server.persistence.dao.interfaces.SpellActionEntryDao;
import se.spaced.server.persistence.dao.interfaces.SpellBookDao;
import se.spaced.server.persistence.dao.interfaces.SpellDao;
import se.spaced.server.persistence.dao.interfaces.WalletDao;
import se.spaced.server.persistence.util.transactions.TransactionProxyWrapper;

public final class PersistanceDaoModule extends AbstractModule {
	@Override
	protected void configure() {

		bind(SessionFactory.class).toProvider(SessionFactoryProvider.class);
		requestStaticInjection(EntityStatsUserType.class);

		/* using the interceptonr makes the tests go poop for me so have to use manual providers :(
		AutoTransactionInterceptor autoTransactionInterceptor = new AutoTransactionInterceptor();
		requestInjection(autoTransactionInterceptor);
		bindInterceptor(Matchers.any(), Matchers.annotatedWith(AutoTransaction.class), autoTransactionInterceptor);
		bind(AccountDao.class).to(AccountDaoImpl.class).in(Scopes.SINGLETON);
		bind(CreatureTypeDao.class).to(CreatureTypeDaoImpl.class).in(Scopes.SINGLETON);
		bind(PlayerDao.class).to(PlayerDaoImpl.class).in(Scopes.SINGLETON);
		bind(SpellDao.class).to(SpellDaoImpl.class).in(Scopes.SINGLETON);
		bind(Inventor
		yDao.class).to(InventoryDaoImpl.class).in(Scopes.SINGLETON);
		bind(MobTemplateDao.class).to(MobTemplateHibernateDao.class).in(Scopes.SINGLETON);
		bind(LootTemplateDao.class).to(LootTemplateDaoImpl.class).in(Scopes.SINGLETON);
		bind(CombatStatisticsDao.class).to(CombatStatisticsDaoImpl.class).in(Scopes.SINGLETON);
		bind(SpawnPatternTemplateDao.class).to(SpawnPatternTemplateDaoImpl.class).in(Scopes.SINGLETON);
		bind(WeaponTemplateDao.class).to(WeaponTemplateDaoImpl.class).in(Scopes.SINGLETON);
		bind(WeaponDao.class).to(WeaponDaoImpl.class).in(Scopes.SINGLETON);
		bind(FactionDao.class).to(FactionDaoImpl.class).in(Scopes.SINGLETON);
		bind(ItemDao.class).to(ItemDaoImpl.class).in(Scopes.SINGLETON);
		bind(ItemTemplateDao.class).to(ItemTemplateDaoImpl.class).in(Scopes.SINGLETON);
		bind(BrainTemplateDao.class).to(BrainTemplateDaoImpl.class).in(Scopes.SINGLETON);
		*/
	}


	@Provides
	@Singleton
	public CooldownTemplateDao getCooldownTemplateDao(SessionFactory sessionFactory) {
		return new CooldownTemplateDaoImpl(sessionFactory);
	}

	@Provides
	@Singleton
	public AccountDao getAccountDao(SessionFactory sessionFactory, TransactionProxyWrapper transactionProxyWrapper) {
		return transactionProxyWrapper.wrap(new AccountDaoImpl(sessionFactory));
	}

	@Provides
	@Singleton
	public CreatureTypeDao getCreatureTypeDao(
			SessionFactory sessionFactory, TransactionProxyWrapper transactionProxyWrapper) {
		return transactionProxyWrapper.wrap(new CreatureTypeDaoImpl(sessionFactory));
	}

	@Provides
	@Singleton
	public PlayerDao getPlayerDao(SessionFactory sessionFactory, TransactionProxyWrapper transactionProxyWrapper) {
		return transactionProxyWrapper.wrap(new PlayerDaoImpl(sessionFactory));
	}

	@Provides
	@Singleton
	public SpellDao getSpellDao(SessionFactory sessionFactory, TransactionProxyWrapper transactionProxyWrapper) {
		return transactionProxyWrapper.wrap(new SpellDaoImpl(sessionFactory));
	}

	@Provides
	@Singleton
	public InventoryDao getInventoryDao(SessionFactory sessionFactory, TransactionProxyWrapper transactionProxyWrapper) {
		return transactionProxyWrapper.wrap(new InventoryDaoImpl(sessionFactory));
	}

	@Provides
	@Singleton
	public MobTemplateDao getMobTemplateDao(
			SessionFactory sessionFactory, TransactionProxyWrapper transactionProxyWrapper) {
		return transactionProxyWrapper.wrap(new MobTemplateHibernateDao(sessionFactory));
	}

	@Provides
	@Singleton
	public EntityTemplateDao getEntityTemplateDao(
			SessionFactory sessionFactory, TransactionProxyWrapper transactionProxyWrapper) {
		return transactionProxyWrapper.wrap(new EntityTemplateHibernateDao(sessionFactory));
	}


	@Provides
	@Singleton
	public LootTemplateDao getLootTemplateDao(
			SessionFactory sessionFactory, TransactionProxyWrapper transactionProxyWrapper) {
		return transactionProxyWrapper.wrap(new LootTemplateDaoImpl(sessionFactory));
	}

	@Provides
	@Singleton
	public GraveyardTemplateDao getGraveyardTemplateDao(
			SessionFactory sessionFactory, TransactionProxyWrapper transactionProxyWrapper) {
		return transactionProxyWrapper.wrap(new GraveyardTemplateDaoImpl(sessionFactory));
	}

	@Provides
	@Singleton
	public SpawnPatternTemplateDao getSpawnPatternTemplateDao(
			SessionFactory sessionFactory, TransactionProxyWrapper transactionProxyWrapper) {
		return transactionProxyWrapper.wrap(new SpawnPatternTemplateDaoImpl(sessionFactory));
	}

	@Provides
	@Singleton
	public FactionDao getFactionDao(SessionFactory sessionFactory, TransactionProxyWrapper transactionProxyWrapper) {
		return transactionProxyWrapper.wrap(new FactionDaoImpl(sessionFactory));
	}

	@Provides
	@Singleton
	public ItemDao getItemDao(SessionFactory sessionFactory, TransactionProxyWrapper transactionProxyWrapper) {
		return transactionProxyWrapper.wrap(new ItemDaoImpl(sessionFactory));
	}

	@Provides
	@Singleton
	public ItemTemplateDao getItemTemplateDao(
			SessionFactory sessionFactory, TransactionProxyWrapper transactionProxyWrapper) {
		return transactionProxyWrapper.wrap(new ItemTemplateDaoImpl(sessionFactory));
	}

	@Provides
	@Singleton
	public BrainTemplateDao getBrainTemplateDao(
			SessionFactory sessionFactory, TransactionProxyWrapper transactionProxyWrapper) {
		return transactionProxyWrapper.wrap(new BrainTemplateDaoImpl(sessionFactory));
	}

	@Provides
	@Singleton
	public EquipmentDao getEquipmentDao(SessionFactory sessionFactory, TransactionProxyWrapper transactionProxyWrapper) {
		return transactionProxyWrapper.wrap(new EquipmentDaoImpl(sessionFactory));
	}

	@Provides
	@Singleton
	public WalletDao getWalletDao(SessionFactory sessionFactory, TransactionProxyWrapper transactionProxyWrapper) {
		return transactionProxyWrapper.wrap(new WalletDaoImpl(sessionFactory));
	}

	@Provides
	@Singleton
	public CurrencyDao getCurrencyDao(SessionFactory sessionFactory, TransactionProxyWrapper transactionProxyWrapper) {
		return transactionProxyWrapper.wrap(new CurrencyDaoImpl(sessionFactory));
	}

	@Provides
	@Singleton
	public KillEntryDao getKillEntryDao(SessionFactory sessionFactory, TransactionProxyWrapper transactionProxyWrapper) {
		return transactionProxyWrapper.wrap(new KillEntryDaoImpl(sessionFactory));
	}

	@Provides
	@Singleton
	public SpellActionEntryDao getSpellActionEntryDao(
			SessionFactory sessionFactory, TransactionProxyWrapper transactionProxyWrapper) {
		return transactionProxyWrapper.wrap(new SpellActionEntryDaoImpl(sessionFactory));
	}

	@Provides
	@Singleton
	public SpellBookDao getSpellBookDao(
			SessionFactory sessionFactory, TransactionProxyWrapper transactionProxyWrapper) {
		return transactionProxyWrapper.wrap(new SpellBookDaoImpl(sessionFactory));
	}

	@Provides
	@Singleton
	public AuraDao getAuraDao(SessionFactory sessionFactory, TransactionProxyWrapper transactionProxyWrapper) {
		return transactionProxyWrapper.wrap(new AuraDaoImpl(sessionFactory));
	}

}
