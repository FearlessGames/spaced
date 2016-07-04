package se.spaced.server.guice.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import se.fearless.common.json.GsonSerializer;
import se.fearless.common.json.JsonSerializer;
import se.fearless.common.lifetime.LifetimeManager;
import se.fearless.common.lifetime.LifetimeManagerImpl;
import se.fearless.common.lua.SimpleLuaSourceProvider;
import se.fearless.common.time.TimeProvider;
import se.fearless.common.uuid.UUIDFactory;
import se.fearless.common.uuid.UUIDFactoryImpl;
import se.krka.kahlua.require.LuaSourceProvider;
import se.smrt.core.remote.mina.ByteArrayDecoder;
import se.smrt.core.remote.mina.ByteArrayEncoder;
import se.spaced.messages.protocol.Entity;
import se.spaced.messages.protocol.c2s.C2SMultiDispatcher;
import se.spaced.messages.protocol.c2s.C2SProtocol;
import se.spaced.messages.protocol.c2s.object.C2SAllMessagesToObject;
import se.spaced.messages.protocol.c2s.remote.C2SRequiredReadCodec;
import se.spaced.messages.protocol.s2c.S2CEmptyReceiver;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.messages.protocol.s2c.adapter.S2CAdapters;
import se.spaced.messages.protocol.s2c.remote.S2CRequiredWriteCodec;
import se.spaced.server.HttpServerNotifier;
import se.spaced.server.ServerNotifier;
import se.spaced.server.account.AccountService;
import se.spaced.server.account.AccountServiceImpl;
import se.spaced.server.loot.LootAwardService;
import se.spaced.server.loot.LootAwardServiceImpl;
import se.spaced.server.loot.LootDistributionService;
import se.spaced.server.loot.LootDistributionServiceImpl;
import se.spaced.server.mob.MobInfoProvider;
import se.spaced.server.mob.MobOrderExecutor;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.action.ActionScheduler;
import se.spaced.server.model.aura.AuraService;
import se.spaced.server.model.aura.AuraServiceImpl;
import se.spaced.server.model.aura.AuraUpdateListener;
import se.spaced.server.model.combat.*;
import se.spaced.server.model.contribution.ContributionMapper;
import se.spaced.server.model.contribution.ContributionService;
import se.spaced.server.model.contribution.ContributionServiceImpl;
import se.spaced.server.model.cooldown.CooldownService;
import se.spaced.server.model.cooldown.CooldownServiceImpl;
import se.spaced.server.model.crafting.SalvageService;
import se.spaced.server.model.currency.MoneyService;
import se.spaced.server.model.currency.MoneyServiceImpl;
import se.spaced.server.model.entity.*;
import se.spaced.server.model.items.*;
import se.spaced.server.model.movement.MovementService;
import se.spaced.server.model.movement.MovementServiceImpl;
import se.spaced.server.model.movement.UnstuckService;
import se.spaced.server.model.movement.UnstuckServiceImpl;
import se.spaced.server.model.player.RemotePlayerService;
import se.spaced.server.model.player.RemotePlayerServiceImpl;
import se.spaced.server.model.relations.RelationsService;
import se.spaced.server.model.relations.RelationsServiceImpl;
import se.spaced.server.model.spawn.SpawnListener;
import se.spaced.server.model.spawn.SpawnService;
import se.spaced.server.model.spawn.SpawnServiceImpl;
import se.spaced.server.model.vendor.VendorService;
import se.spaced.server.model.vendor.VendorServiceImpl;
import se.spaced.server.model.world.TimeService;
import se.spaced.server.model.world.TimeServiceImpl;
import se.spaced.server.net.ClientConnectionHandler;
import se.spaced.server.net.RemoteServer;
import se.spaced.server.net.SmrtEnqueuer;
import se.spaced.server.net.broadcast.SmrtBroadcaster;
import se.spaced.server.net.broadcast.SmrtBroadcasterImpl;
import se.spaced.server.net.listeners.auth.GmMobLifecycle;
import se.spaced.server.net.mina.ClientConnectionHandlerImpl;
import se.spaced.server.net.mina.ClientToServerRequiredReadCodec;
import se.spaced.server.net.mina.RemoteServerImpl;
import se.spaced.server.net.mina.ServerToClientRequiredWriteCodec;
import se.spaced.server.persistence.dao.impl.hibernate.TransactionManager;
import se.spaced.server.persistence.dao.interfaces.*;
import se.spaced.server.persistence.migrator.ServerContentPopulator;
import se.spaced.server.persistence.util.transactions.TransactionProxyWrapper;
import se.spaced.server.player.PlayerCreationService;
import se.spaced.server.player.PlayerCreationServiceImpl;
import se.spaced.server.player.PlayerService;
import se.spaced.server.player.PlayerServiceImpl;
import se.spaced.server.services.*;
import se.spaced.server.services.webservices.InformationWebServiceImpl;
import se.spaced.server.services.webservices.SpellAdminWebServiceImpl;
import se.spaced.server.services.webservices.WebServicePublisher;
import se.spaced.server.services.webservices.WebServicePublisherImpl;
import se.spaced.server.services.webservices.external.*;
import se.spaced.server.spell.SpellService;
import se.spaced.server.spell.SpellServiceImpl;
import se.spaced.server.stats.KillStatisticsService;
import se.spaced.server.stats.KillStatisticsServiceImpl;
import se.spaced.server.trade.*;
import se.spaced.shared.network.webservices.admin.SpellAdminWebService;
import se.spaced.shared.network.webservices.informationservice.InformationWebService;
import se.spaced.shared.statistics.EventLogger;
import se.spaced.shared.statistics.FileEventLogger;
import se.spaced.shared.util.ListenerDispatcher;
import se.spaced.shared.util.cache.CacheManager;
import se.spaced.shared.util.random.RandomProvider;
import se.spaced.shared.util.random.RealRandomProvider;
import se.spaced.shared.world.TimeSystemInfo;
import se.spaced.shared.world.area.PolygonGraph;
import se.spaced.shared.world.area.PolygonGraphLoader;
import se.spaced.shared.world.area.ZoneBasedPolygonGraphLoader;
import se.spaced.shared.xml.XmlIOException;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public final class ServiceModule extends AbstractModule {
	public ServiceModule() {
	}

	@Override
	public void configure() {


		// Note: either use this one or the provider through DaoFactory
		//bind(SpellDao.class).to(SpellDaoMemory.class);

		bind(MovementService.class).to(MovementServiceImpl.class);

		bind(RemoteServer.class).to(RemoteServerImpl.class);
		bind(ClientConnectionHandler.class).to(ClientConnectionHandlerImpl.class);

		bind(EntityCombatService.class).to(EntityCombatServiceImpl.class);
		bind(SpellCombatService.class).to(SpellCombatServiceImpl.class);
		bind(EntityTargetService.class).to(EntityTargetServiceImpl.class);

		bind(RandomProvider.class).to(RealRandomProvider.class);
		bind(CombatMechanics.class).to(SimpleCombatMechanics.class);


		bind(EntityService.class).to(EntityServiceImpl.class);

		bind(PlayerConnectedService.class).to(PlayerConnectedServiceImpl.class);

		bind(WebServicePublisher.class).to(WebServicePublisherImpl.class);

		bind(InformationWebService.class).to(InformationWebServiceImpl.class);
		bind(SpellAdminWebService.class).to(SpellAdminWebServiceImpl.class);
		bind(KillStatisticsWebService.class).to(KillStatisticsWebServiceImpl.class);
		bind(EntityWebService.class).to(EntityWebServiceImpl.class);
		bind(SpellStatisticsWebService.class).to(SpellStatisticsWebServiceImpl.class);
		bind(BroadcastWebService.class).to(BroadcastWebServiceImpl.class);

		bind(AuraService.class).to(AuraServiceImpl.class);

		bind(C2SRequiredReadCodec.class).to(ClientToServerRequiredReadCodec.class);


		bind(ProtocolDecoder.class).to(ByteArrayDecoder.class);
		bind(ProtocolEncoder.class).to(ByteArrayEncoder.class);

		// TODO: set up a proper handler here
		bind(C2SProtocol.class).annotatedWith(Names.named("incomingMessageHandler")).
				toInstance(new C2SMultiDispatcher());
		bind(S2CRequiredWriteCodec.class).to(ServerToClientRequiredWriteCodec.class);

		bind(AtomicInteger.class).annotatedWith(Names.named("projectileId")).to(AtomicInteger.class).in(Scopes.SINGLETON);
		bind(LuaSourceProvider.class).to(SimpleLuaSourceProvider.class);

		bind(PlayerCreationService.class).to(PlayerCreationServiceImpl.class);
		bind(SalvageService.class).in(Scopes.SINGLETON);

		bind(PolygonGraphLoader.class).to(ZoneBasedPolygonGraphLoader.class).in(Scopes.SINGLETON);
		bind(CacheManager.class).annotatedWith(Names.named("xmoCachedManager")).to(CacheManager.class).in(Scopes.SINGLETON);

		bind(EventLogger.class).to(FileEventLogger.class).in(Scopes.SINGLETON);
	}

	@Provides
	@Singleton
	public UUIDFactory getUuidFactory(TimeProvider timeProvider, Random random) {
		return new UUIDFactoryImpl(timeProvider, random);
	}

	@Provides
	@Singleton
	public ListenerDispatcher<TargetUpdateListener> getTargetDispatcher() {
		return ListenerDispatcher.create(TargetUpdateListener.class);
	}


	@Provides
	@Singleton
	public TargetUpdateListener getTargetListener(ListenerDispatcher<TargetUpdateListener> dispatcher) {
		return dispatcher.trigger();
	}

	@Provides
	@Singleton
	public TransactionProxyWrapper getTransactionProxyWrapper(TransactionManager transactionManager) {
		return new TransactionProxyWrapper(transactionManager);
	}

	@Provides
	@Singleton
	public RemotePlayerService getRemotePlayerService(
			TransactionProxyWrapper proxy,
			EntityService entityService,
			PlayerConnectedService playerConnectedService,
			PlayerService playerService,
			SpellService spellService,
			SmrtBroadcaster<S2CProtocol> smrtBroadcaster,
			InventoryService inventoryService, EquipmentService equipmentService, MoneyService moneyService) {
		return proxy.wrap(new RemotePlayerServiceImpl(entityService, playerConnectedService,
				playerService, spellService, smrtBroadcaster, inventoryService, equipmentService, moneyService));
	}


	@Provides
	@Singleton
	public CombatRepository getCombatRepository() {
		return new CombatRepositoryImpl();
	}

	@Provides
	@Singleton
	public LifetimeManager getLifetimeManager() {
		return new LifetimeManagerImpl();
	}

	@Provides
	@Singleton
	public LootAwardService getLootService(
			InventoryService inventoryService, TransactionManager transactionManager,
			TransactionProxyWrapper transactionProxyWrapper, ItemService itemService,
			MoneyService moneyService, EntityService entityService) {
		return transactionProxyWrapper.wrap(new LootAwardServiceImpl(inventoryService, transactionManager,
				itemService, moneyService, entityService));
	}

	@Provides
	@Singleton
	public LootDistributionService getLootDistributionService(
			TransactionProxyWrapper transactionProxyWrapper,
			ContributionService contributionService, LootAwardService lootAwardService, SmrtBroadcaster<S2CProtocol> smrtBroadcaster) {
		final LootDistributionService wrappedService = transactionProxyWrapper.wrap(new LootDistributionServiceImpl(contributionService, lootAwardService));
		smrtBroadcaster.addSpy(new S2CEmptyReceiver() {
			@Override
			public void entityWasKilled(Entity attacker, Entity target) {
				wrappedService.distributeLoot((ServerEntity) target);
			}
		});
		return wrappedService;
	}

	@Provides
	@Singleton
	public MoneyService getWalletService(
			TransactionProxyWrapper transactionProxyWrapper,
			SmrtBroadcaster<S2CProtocol> smrtBroadcaster,
			WalletDao walletDao, CurrencyDao currencyDao) {
		return transactionProxyWrapper.wrap(new MoneyServiceImpl(smrtBroadcaster, walletDao, currencyDao));
	}

	@Provides
	@Singleton
	public SpawnService getSpawnService(
			SmrtBroadcaster<S2CProtocol> smrtBroadcaster,
			TimeProvider timeProvider, ListenerDispatcher<SpawnListener> listenerDispatcher,
			TransactionProxyWrapper transactionProxyWrapper) {
		return transactionProxyWrapper.wrap(new SpawnServiceImpl(smrtBroadcaster,
				timeProvider,
				listenerDispatcher
		));
	}

	@Provides
	@Singleton
	public ServerNotifier getServerNotifier() {
		return new HttpServerNotifier();
	}


	@Provides
	@Singleton
	public SmrtBroadcaster<S2CProtocol> getSmrtBroadcaster(EntityService entityService, CombatRepository combatRepository, VisibilityService visibilityService) {
		SmrtBroadcasterImpl smrtBroadcaster = new SmrtBroadcasterImpl(entityService, combatRepository, visibilityService);

		return smrtBroadcaster;
	}

	@Provides
	@Singleton
	public C2SAllMessagesToObject getAllMessagesToObject(
			ActionScheduler scheduler, TimeProvider timeProvider,
			@Named("incomingMessageHandler") C2SProtocol receiver) {
		return new C2SAllMessagesToObject(new SmrtEnqueuer(scheduler, timeProvider, receiver));
	}


	@Provides
	@Singleton
	public AccountService getAccountService(AccountDao accountDao, TransactionProxyWrapper transactionProxyWrapper) {
		return transactionProxyWrapper.wrap(new AccountServiceImpl(accountDao));
	}

	@Provides
	@Singleton
	public DeathService getDeathService(
			TransactionProxyWrapper transactionProxyWrapper,
			GraveyardService graveyardService,
			SmrtBroadcaster<S2CProtocol> broadcaster, AuraService auraService, OutOfCombatRegenService outOfCombatRegenService) {
		return transactionProxyWrapper.wrap(new DeathServiceImpl(broadcaster, auraService, graveyardService, outOfCombatRegenService));
	}

	@Provides
	@Singleton
	public EquipmentService getEquipmentService(
			TransactionProxyWrapper transactionProxyWrapper, EquipmentDao equipmentDao,
			InventoryService inventoryService,
			SmrtBroadcaster<S2CProtocol> broadcaster,
			VisibilityService visibilityService,
			AuraService auraService,
			TimeProvider timeProvider, EntityService entityService) {
		return transactionProxyWrapper.wrap(new EquipmentServiceImpl(equipmentDao,
				inventoryService,
				broadcaster,
				visibilityService, auraService, timeProvider, entityService));
	}

	@Provides
	@Singleton
	public CooldownService getCooldownService(TransactionProxyWrapper transactionProxyWrapper, CooldownTemplateDao dao) {
		return transactionProxyWrapper.wrap(new CooldownServiceImpl(dao));
	}

	@Provides
	@Singleton
	public PlayerService getPlayerService(
			PlayerDao playerDao,
			TimeProvider timeProvider,
			TransactionProxyWrapper transactionProxyWrapper,
			TransactionManager transactionManager) {
		return transactionProxyWrapper.wrap(new PlayerServiceImpl(timeProvider, playerDao, transactionManager));
	}

	@Provides
	@Singleton
	public SpellService getSpellService(
			TransactionManager transactionManager, TimeProvider timeProvider, SpellDao spellDao, SpellBookDao spellBookDao,
			TransactionProxyWrapper transactionProxyWrapper) {
		return transactionProxyWrapper.wrap(new SpellServiceImpl(transactionManager,
				timeProvider,
				spellDao,
				spellBookDao));
	}

	@Provides
	@Singleton
	public InventoryService getInventoryService(InventoryDao inventoryDao, TransactionProxyWrapper transactionProxyWrapper,
															  SmrtBroadcaster<S2CProtocol> broadcaster) {
		return transactionProxyWrapper.wrap(new InventoryServiceImpl(inventoryDao, broadcaster));
	}

	@Provides
	@Singleton
	public ListenerDispatcher<SpawnListener> getSpawnListener() {
		return ListenerDispatcher.create(SpawnListener.class);
	}

	@Provides
	@Singleton
	public TargetUpdateBroadcaster targetUpdateBroadcaster(SmrtBroadcaster<S2CProtocol> broadcaster) {
		return new TargetUpdateBroadcaster(broadcaster);
	}

	@Provides
	@Singleton
	public VisibilityService getVisibilityService(EntityService entityService, ListenerDispatcher<AppearanceService> appearanceListeners) {
		return new VisibilityServiceImpl(entityService, appearanceListeners);
	}


	@Provides
	@Singleton
	public ItemService getItemService(
			ItemDao itemDao,
			ItemTemplateDao itemTemplateDao,
			SpellCombatService spellCombatService,
			TimeProvider timeProvider,
			SmrtBroadcaster<S2CProtocol> broadcaster,
			TransactionProxyWrapper transactionProxyWrapper,
			ActionScheduler scheduler, InventoryService inventoryService, UUIDFactory uuidFactory) {
		return transactionProxyWrapper.wrap(new ItemServiceImpl(itemTemplateDao,
				itemDao,
				spellCombatService,
				timeProvider,
				broadcaster,
				scheduler, inventoryService, uuidFactory));
	}

	@Provides
	@Singleton
	public TradeService getTradeService(TransactionProxyWrapper transactionProxyWrapper, TradeTransitionModelProvider transitionModelProvider) {
		return transactionProxyWrapper.wrap(new TradeServiceImpl(transitionModelProvider));
	}

	@Provides
	@Singleton
	public VendorService getVendorService(TransactionProxyWrapper transactionProxyWrapper, ItemService itemService, InventoryService inventoryService,
													  MoneyService moneyService, SmrtBroadcaster<S2CProtocol> smrtBroadcaster) {
		return transactionProxyWrapper.wrap(new VendorServiceImpl(itemService,  inventoryService, moneyService, smrtBroadcaster));
	}


	@Provides
	@Singleton
	public ListenerDispatcher<EntityServiceListener> getEntityServiceListenerListenerDispatcher() {
		return ListenerDispatcher.create(EntityServiceListener.class);
	}

	@Provides
	@Singleton
	public ListenerDispatcher<AppearanceService> getAppearanceListeners() {
		return ListenerDispatcher.create(AppearanceService.class);
	}

	@Provides
	@Singleton
	public ListenerDispatcher<AuraUpdateListener> getAuraUpdateListener() {
		return ListenerDispatcher.create(AuraUpdateListener.class);
	}

	@Provides
	@Singleton
	public RelationsService getRelationsService(TransactionProxyWrapper transactionProxyWrapper) {
		return transactionProxyWrapper.wrap(new RelationsServiceImpl());
	}

	@Provides
	@Singleton
	public GameMasterService getGameMasterService(
			TransactionProxyWrapper transactionProxyWrapper,
			MovementService movementService,
			TimeProvider timeProvider,
			InventoryService inventoryService,
			ItemService itemService, SmrtBroadcaster<S2CProtocol> broadcaster,
			SpawnService spawnService,
			ActionScheduler scheduler,
			EntityService entityService,
			UUIDFactory uuidFactory,
			MobOrderExecutor mobOrderExecutor,
			GmMobLifecycle gmMoLifeCycle,
			ServerContentPopulator serverContentPopulator,
			SpellService spellService, MoneyService moneyService, RandomProvider randomProvider) {
		return transactionProxyWrapper.wrap(new GameMasterServiceImpl(movementService, timeProvider, inventoryService,
				itemService, broadcaster, spawnService, scheduler, entityService, uuidFactory,
				mobOrderExecutor, gmMoLifeCycle, serverContentPopulator, spellService, moneyService, randomProvider));
	}

	@Provides
	@Singleton
	public EntityServiceListener getEntityServiceListener(ListenerDispatcher<EntityServiceListener> listenerDispatcher) {
		return listenerDispatcher.trigger();
	}


	@Provides
	@Singleton
	public MobInfoProvider getMobInfoProvider() {
		return new MobInfoProvider();
	}

	@Provides
	@Singleton
	public KillStatisticsService getKillStatisticsService(
			TransactionProxyWrapper transactionProxyWrapper,
			KillEntryDao killEntryDao,
			EntityTemplateDao entityTemplateDao) {
		return transactionProxyWrapper.wrap(new KillStatisticsServiceImpl(killEntryDao, entityTemplateDao));
	}

	@Provides
	@Singleton
	public TradeCallback getTradeCallback(SmrtBroadcaster<S2CProtocol> broadcaster, TradeExecutor tradeExecutor) {
		return new SmrtTradeCallback(broadcaster, tradeExecutor);
	}

	@Provides
	@Singleton
	public TradeExecutor getTradeExecutor(TransactionManager transactionManager, ItemService itemService) {
		return new TradeExecutor(transactionManager, itemService);
	}

	@Provides
	@Singleton
	public UnstuckService getUnstuckService(
			TransactionProxyWrapper transactionProxyWrapper,
			GraveyardService graveyardService, MovementService movementService, TimeProvider timeProvider) {
		return transactionProxyWrapper.wrap(new UnstuckServiceImpl(graveyardService, movementService, timeProvider));
	}

	@Provides
	@Singleton
	public AppearanceService getAppearanceService(
			TransactionProxyWrapper transactionProxyWrapper,
			EntityService entityService,
			EquipmentDao equipmentDao,
			EntityTargetService entityTargetService,
			AuraService auraService,
			EntityCombatService combatService) {
		return transactionProxyWrapper.wrap(new AppearanceServiceImpl(entityService, equipmentDao, entityTargetService,
				auraService, combatService));
	}

	@Provides
	@Singleton
	public TimeService getTimeService(TimeProvider timeProvider) {
		TimeService timeService = new TimeServiceImpl(timeProvider);
		timeService.registerZone("outerSpace", new TimeSystemInfo(24, 60, 60, 1.0 / 96));
		return timeService;
	}

	@Provides
	@Singleton
	public PolygonGraph getPolygonGraph(PolygonGraphLoader loader) throws XmlIOException {
		//return loader.loadPolygonGraph("/mobs/navmesh/fearless.xml");
		return loader.loadPolygonGraph("/zone/spacebattle/outerSpace.zone");
	}

	@Provides
	@Singleton
	public ContributionService getContributionService(SmrtBroadcaster<S2CProtocol> broadcaster, CombatRepository combatRepository) {
		ContributionServiceImpl contributionService = new ContributionServiceImpl();
		ContributionMapper mapper = new ContributionMapper(contributionService, combatRepository);
		broadcaster.addSpy(S2CAdapters.createServerCombatMessages(mapper));
		return contributionService;
	}

	@Provides
	@Singleton
	public JsonSerializer getJsonSerializer() {
		return new GsonSerializer();
	}
}
