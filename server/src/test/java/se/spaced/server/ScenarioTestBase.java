package se.spaced.server;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearlessgames.common.util.MockTimeProvider;
import se.fearlessgames.common.util.uuid.UUIDFactory;
import se.fearlessgames.common.util.uuid.UUIDMockFactory;
import se.spaced.messages.protocol.Entity;
import se.spaced.messages.protocol.s2c.S2CEmptyReceiver;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.messages.protocol.s2c.adapter.S2CAdapters;
import se.spaced.server.guice.modules.AppearanceListenerDispatcherConnector;
import se.spaced.server.guice.modules.AuraUpdateListenerDispatcherConnector;
import se.spaced.server.guice.modules.EntityListenerDispatcherConnector;
import se.spaced.server.guice.modules.SpawnListenerDispatcherConnector;
import se.spaced.server.guice.modules.TargetUpdateListenerDispatcherConnector;
import se.spaced.server.loot.LootAwardService;
import se.spaced.server.loot.LootAwardServiceImpl;
import se.spaced.server.loot.LootDistributionService;
import se.spaced.server.loot.LootDistributionServiceImpl;
import se.spaced.server.mob.MobOrderExecutor;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.action.ActionScheduler;
import se.spaced.server.model.aura.AuraService;
import se.spaced.server.model.aura.AuraServiceImpl;
import se.spaced.server.model.aura.AuraUpdateBroadcaster;
import se.spaced.server.model.aura.AuraUpdateListener;
import se.spaced.server.model.combat.CombatMechanics;
import se.spaced.server.model.combat.CombatRepository;
import se.spaced.server.model.combat.CombatRepositoryImpl;
import se.spaced.server.model.combat.CurrentActionService;
import se.spaced.server.model.combat.DeathService;
import se.spaced.server.model.combat.DeathServiceImpl;
import se.spaced.server.model.combat.EntityCombatService;
import se.spaced.server.model.combat.EntityCombatServiceImpl;
import se.spaced.server.model.combat.EntityTargetService;
import se.spaced.server.model.combat.EntityTargetServiceImpl;
import se.spaced.server.model.combat.OutOfCombatRegenService;
import se.spaced.server.model.combat.SimpleCombatMechanics;
import se.spaced.server.model.combat.SpellCombatService;
import se.spaced.server.model.combat.SpellCombatServiceImpl;
import se.spaced.server.model.combat.TargetUpdateBroadcaster;
import se.spaced.server.model.combat.TargetUpdateListener;
import se.spaced.server.model.contribution.ContributionMapper;
import se.spaced.server.model.contribution.ContributionService;
import se.spaced.server.model.contribution.ContributionServiceImpl;
import se.spaced.server.model.crafting.SalvageService;
import se.spaced.server.model.currency.MoneyService;
import se.spaced.server.model.currency.MoneyServiceImpl;
import se.spaced.server.model.entity.AppearanceService;
import se.spaced.server.model.entity.AppearanceServiceImpl;
import se.spaced.server.model.entity.EntityService;
import se.spaced.server.model.entity.EntityServiceImpl;
import se.spaced.server.model.entity.EntityServiceListener;
import se.spaced.server.model.entity.VisibilityService;
import se.spaced.server.model.entity.VisibilityServiceImpl;
import se.spaced.server.model.items.EquipmentService;
import se.spaced.server.model.items.EquipmentServiceImpl;
import se.spaced.server.model.items.InventoryService;
import se.spaced.server.model.items.InventoryServiceImpl;
import se.spaced.server.model.items.ItemService;
import se.spaced.server.model.items.ItemServiceImpl;
import se.spaced.server.model.movement.MovementService;
import se.spaced.server.model.movement.MovementServiceImpl;
import se.spaced.server.model.player.PlayerMockFactory;
import se.spaced.server.model.spawn.SpawnListener;
import se.spaced.server.model.spawn.SpawnService;
import se.spaced.server.model.spawn.SpawnServiceImpl;
import se.spaced.server.model.vendor.VendorService;
import se.spaced.server.model.vendor.VendorServiceImpl;
import se.spaced.server.net.broadcast.SmrtBroadcaster;
import se.spaced.server.net.broadcast.SmrtBroadcasterImpl;
import se.spaced.server.persistence.dao.impl.hibernate.TransactionManager;
import se.spaced.server.persistence.dao.impl.inmemory.InMemoryCreatureTypeDao;
import se.spaced.server.persistence.dao.impl.inmemory.InMemoryCurrencyDao;
import se.spaced.server.persistence.dao.impl.inmemory.InMemoryEquipmentDao;
import se.spaced.server.persistence.dao.impl.inmemory.InMemoryFactionDao;
import se.spaced.server.persistence.dao.impl.inmemory.InMemoryGraveyardTemplateDao;
import se.spaced.server.persistence.dao.impl.inmemory.InMemoryInventoryDao;
import se.spaced.server.persistence.dao.impl.inmemory.InMemoryItemDao;
import se.spaced.server.persistence.dao.impl.inmemory.InMemoryItemTemplateDao;
import se.spaced.server.persistence.dao.impl.inmemory.InMemoryPlayerDao;
import se.spaced.server.persistence.dao.impl.inmemory.InMemorySpellBookDao;
import se.spaced.server.persistence.dao.impl.inmemory.InMemorySpellDao;
import se.spaced.server.persistence.dao.impl.inmemory.InMemoryWalletDao;
import se.spaced.server.persistence.dao.interfaces.CreatureTypeDao;
import se.spaced.server.persistence.dao.interfaces.CurrencyDao;
import se.spaced.server.persistence.dao.interfaces.EquipmentDao;
import se.spaced.server.persistence.dao.interfaces.FactionDao;
import se.spaced.server.persistence.dao.interfaces.ItemDao;
import se.spaced.server.persistence.dao.interfaces.ItemTemplateDao;
import se.spaced.server.persistence.dao.interfaces.Persistable;
import se.spaced.server.persistence.dao.interfaces.PlayerDao;
import se.spaced.server.persistence.dao.interfaces.SpellBookDao;
import se.spaced.server.persistence.dao.interfaces.SpellDao;
import se.spaced.server.persistence.dao.interfaces.WalletDao;
import se.spaced.server.player.PlayerService;
import se.spaced.server.player.PlayerServiceImpl;
import se.spaced.server.services.GraveyardService;
import se.spaced.server.spell.SpellService;
import se.spaced.server.spell.SpellServiceImpl;
import se.spaced.server.trade.TradeCallback;
import se.spaced.server.trade.TradeService;
import se.spaced.server.trade.TradeServiceImpl;
import se.spaced.server.trade.TradeTransitionModelProvider;
import se.spaced.shared.util.ListenerDispatcher;
import se.spaced.shared.util.random.RealRandomProvider;
import se.spaced.shared.world.area.PolygonGraph;

import static se.fearlessgames.common.mock.MockUtil.deepMock;
import static se.mockachino.Mockachino.*;

public class ScenarioTestBase {
	private final Logger log = LoggerFactory.getLogger(getClass());
	// Core services - no dependencies
	protected final UUIDFactory uuidFactory = new UUIDMockFactory();
	protected final MockTimeProvider timeProvider = new MockTimeProvider();
	protected final ActionScheduler actionScheduler = spy(new ActionScheduler());
	protected final RealRandomProvider randomProvider = new RealRandomProvider();

	protected final PlayerMockFactory playerFactory = new PlayerMockFactory.Builder(timeProvider, uuidFactory).build();


	// In memory database
	protected final TransactionManager transactionManager = spy(new TransactionManager(deepMock(SessionFactory.class)));
	protected final SpellDao spellDao = new InMemorySpellDao();
	protected final SpellBookDao spellBookDao = new InMemorySpellBookDao();
	protected final ItemDao itemDao = spy(new InMemoryItemDao());
	protected final ItemTemplateDao itemTemplateDao = new InMemoryItemTemplateDao();
	protected final PlayerDao playerDao = new InMemoryPlayerDao();
	protected final InMemoryGraveyardTemplateDao graveyardTemplateDao = new InMemoryGraveyardTemplateDao();
	protected final EquipmentDao equipmentDao = new InMemoryEquipmentDao();
	protected final InMemoryInventoryDao inventoryDao = new InMemoryInventoryDao();
	protected final WalletDao walletDao = new InMemoryWalletDao();
	protected final CurrencyDao currencyDao = new InMemoryCurrencyDao();
	protected final FactionDao factionDao = new InMemoryFactionDao();
	protected final CreatureTypeDao creatureTypeDao = new InMemoryCreatureTypeDao();

	// Listener dispatchers
	protected final ListenerDispatcher<EntityServiceListener> entityServiceListenerDispatcher = ListenerDispatcher.create(
			EntityServiceListener.class);
	private final ListenerDispatcher<TargetUpdateListener> targetUpdateDispatcher = ListenerDispatcher.create(
			TargetUpdateListener.class);
	protected final ListenerDispatcher<AppearanceService> appearanceListeners = ListenerDispatcher.create(
			AppearanceService.class);

	protected final TargetUpdateListener targetUpdateListener = targetUpdateDispatcher.trigger();

	// Services
	protected final CombatRepository combatRepository = spy(new CombatRepositoryImpl());

	protected final EntityService entityService = spy(new EntityServiceImpl(uuidFactory,
			entityServiceListenerDispatcher));

	protected final EntityTargetService entityTargetService = spy(new EntityTargetServiceImpl(
			targetUpdateListener));

	protected final VisibilityService visibilityService = spy(new VisibilityServiceImpl(entityService, appearanceListeners));

	protected final SmrtBroadcaster<S2CProtocol> smrtBroadcaster = new SmrtBroadcasterImpl(entityService,
			combatRepository,
			visibilityService);

	protected final SpellService spellService = spy(new SpellServiceImpl(transactionManager,
			timeProvider,
			spellDao,
			spellBookDao));

	protected final InventoryService inventoryService = spy(new InventoryServiceImpl(inventoryDao, smrtBroadcaster));


	protected final ListenerDispatcher<AuraUpdateListener> auraUpdateListener = ListenerDispatcher.create(AuraUpdateListener.class);

	protected final AuraService auraService = spy(new AuraServiceImpl(actionScheduler,
			auraUpdateListener));

	protected final TradeCallback tradeCallback = mock(TradeCallback.class);
	protected final TradeService tradeService = new TradeServiceImpl(new TradeTransitionModelProvider(tradeCallback));




	protected final TargetUpdateBroadcaster targetUpdateBroadcaster = new TargetUpdateBroadcaster(smrtBroadcaster
	);


	protected final EquipmentService equipmentService = spy(new EquipmentServiceImpl(equipmentDao,
			inventoryService,
			smrtBroadcaster,
			visibilityService,
			auraService,
			timeProvider, entityService));
	protected final CurrentActionService currentActionService = spy(new CurrentActionService());
	protected final GraveyardService graveyardService = mock(GraveyardService.class);
	OutOfCombatRegenService outOfCombatRegenService = new OutOfCombatRegenService(smrtBroadcaster);
	protected final DeathService deathService = new DeathServiceImpl(smrtBroadcaster, auraService,
			graveyardService, outOfCombatRegenService);
	protected final EntityCombatService entityCombatService = new EntityCombatServiceImpl(actionScheduler,
			currentActionService,
			smrtBroadcaster,
			combatRepository,
			deathService);
	protected final AppearanceService appearanceService = new AppearanceServiceImpl(entityService, equipmentDao,
			entityTargetService, auraService, entityCombatService);
	protected final CombatMechanics combatMechanics = new SimpleCombatMechanics(randomProvider, auraService);
	protected final SpellCombatService spellCombatService = spy(new SpellCombatServiceImpl(entityCombatService,
			combatMechanics,
			actionScheduler,
			currentActionService,
			smrtBroadcaster,
			deathService));
	protected final ItemService itemService = spy(new ItemServiceImpl(itemTemplateDao,
			itemDao,
			spellCombatService,
			timeProvider,
			smrtBroadcaster,
			actionScheduler,
			inventoryService,
			uuidFactory));

	protected final MoneyService moneyService = spy(new MoneyServiceImpl(smrtBroadcaster, walletDao, currencyDao));
	protected final LootAwardService lootAwardService = new LootAwardServiceImpl(inventoryService, transactionManager,
			itemService, moneyService, entityService);

	protected final SalvageService salvageService = spy(new SalvageService(itemService, transactionManager, lootAwardService,
			randomProvider));

	protected final ListenerDispatcher<SpawnListener> spawnListenerDispatcher = ListenerDispatcher.create(SpawnListener.class);

	protected final ContributionService contributionService = new ContributionServiceImpl();

	protected final SpawnService spawnService = new SpawnServiceImpl(smrtBroadcaster,
			timeProvider,
			spawnListenerDispatcher
	);

	protected final LootDistributionService lootDistributionService = new LootDistributionServiceImpl(contributionService, lootAwardService);

	protected final PlayerService playerService = new PlayerServiceImpl(timeProvider, playerDao, transactionManager);

	protected final MovementService movementService = new MovementServiceImpl(smrtBroadcaster,
			timeProvider,
			actionScheduler,
			spellCombatService,
			visibilityService,
			playerService,
			currentActionService);

	protected final PolygonGraph polygonGraph = new PolygonGraph();
	protected final MobOrderExecutor mobOrderExecutor = spy(new MobOrderExecutor(movementService,
			timeProvider,
			smrtBroadcaster,
			spellCombatService,
			polygonGraph));

	protected final AuraUpdateBroadcaster auraUpdateBroadcaster = new AuraUpdateBroadcaster(smrtBroadcaster);

	protected final VendorService vendorService = new VendorServiceImpl(itemService, inventoryService, moneyService, smrtBroadcaster);

	private final EntityListenerDispatcherConnector entityListenerDispatcherConnector = new EntityListenerDispatcherConnector(
			entityServiceListenerDispatcher,
			visibilityService,
			auraService,
			entityCombatService,
			entityTargetService,
			mobOrderExecutor,
			tradeService, contributionService, vendorService);

	private final AuraUpdateListenerDispatcherConnector auraUpdateListenerDispatcherConnector = new AuraUpdateListenerDispatcherConnector(auraUpdateListener, auraUpdateBroadcaster);

	private final AppearanceListenerDispatcherConnector appearanceListenerDispatcherConnector = new AppearanceListenerDispatcherConnector(appearanceListeners, appearanceService);

	private final TargetUpdateListenerDispatcherConnector targetUpdateListenerDispatcherConnector = new TargetUpdateListenerDispatcherConnector(targetUpdateDispatcher, targetUpdateBroadcaster);

	private final SpawnListenerDispatcherConnector spawnListenerDispatcherConnector = new SpawnListenerDispatcherConnector(spawnListenerDispatcher, vendorService);


	@Before
	public void setupBase() {
		log.info("Setup in ScenarioTestBase");
		setupMocks(this);

		ContributionMapper contributionMapper = new ContributionMapper(contributionService, combatRepository);
		smrtBroadcaster.addSpy(S2CAdapters.createServerCombatMessages(contributionMapper));
		smrtBroadcaster.addSpy(new S2CEmptyReceiver() {
			@Override
			public void entityWasKilled(Entity attacker, Entity target) {
				lootDistributionService.distributeLoot((ServerEntity) target);
			}
		});
	}

	protected void tick(long amountInMillis) {
		timeProvider.advanceTime(amountInMillis);
		log.info("Ticking up to {}", timeProvider.now());
		actionScheduler.tick(timeProvider.now());
		mobOrderExecutor.executeMoveMap();
	}

	@Test
	public void testPlaceholder() {
	}

	@SuppressWarnings("unchecked")
	public <T extends Persistable> T mockWithId(Class<T> clazz) {
		Persistable entity = mock(clazz);
		entity.setPk(uuidFactory.combUUID());
		return (T) entity;
	}

}
