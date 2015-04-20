package se.spaced.server.model;

import org.junit.Test;
import se.spaced.server.persistence.dao.impl.hibernate.PersistentTestBase;


public class PositionServiceImplScenarioTest extends PersistentTestBase {
	/*
	private ClientCombatMessagesAuth clientCombatMessagesAuth;
	private ClientMovementMessagesAuth clientMovementMessagesAuth;
	private PlayerService playerService;

	private SpellService spellService;
	private MockTimeProvider timeProvider;
	private CurrentActionService currentActionService;
	private Player player;
	private Player target;
	private ServerSpell spell;
	private SpellCombatService spellCombatService;
	private InventoryService inventoryService;


	private MovementService movementService;
	private EntityService entityService;

	@Mock
	private VisibilityService visibilityService;

	@Before
	public void setUp() {
		setupMocks(this);

		TransactionProxyWrapper transactionProxyWrapper = new TransactionProxyWrapper(transactionManager);
		timeProvider = new MockTimeProvider();

		ActionScheduler actionScheduler = new ActionScheduler();
		CombatRepository combatRepository = new CombatRepositoryImpl();
		entityService = mock(EntityService.class);
		SmrtBroadcaster<S2CProtocol> smrtBroadcaster = new SmrtBroadcasterImpl(entityService, combatRepository, null);
		spellService = transactionProxyWrapper.wrap(new SpellServiceImpl(transactionManager, timeProvider, daoFactory.getSpellDao()));
		PlayerService realPlayerService = transactionProxyWrapper.wrap(new PlayerServiceImpl(timeProvider, daoFactory.getPlayerDao()));
		AccountService accountService = transactionProxyWrapper.wrap(new AccountServiceImpl(daoFactory.getAccountDao()));
		inventoryService = mock(InventoryService.class);
		stubReturn(new SpacedInventory(null, 20)).on(inventoryService).createInventory(any(Player.class), anyInt());
		spellCombatService = mock(SpellCombatService.class);


		SpellDao spellDao = mock(SpellDao.class);
		PlayerCreationService realPlayerCreationService = transactionProxyWrapper.wrap(new PlayerCreationServiceImpl(
				transactionManager,
				spellService, inventoryService, daoFactory.getSpellDao(), realPlayerService, daoFactory.getFactionDao(), daoFactory.getCreatureTypeDao(), accountService));
	
		List<Migrator> migrators = Lists.newArrayList();
		migrators.add(new MockCreatureTypePopulator(transactionManager, daoFactory.getCreatureTypeDao()));
		migrators.add(new MockFactionPopulator(transactionManager, daoFactory.getFactionDao()));
		migrators.add(new MockItemTemplatePopulator(transactionManager, daoFactory.getItemTemplateDao(), spellDao));
		UUIDFactory uuidFactory = new UUIDFactoryImpl(timeProvider, new Random());
		migrators.add(new MockSpellPopulator(transactionManager, daoFactory.getSpellDao(), spellCombatService, actionScheduler, smrtBroadcaster, projectileIdCounter, visibilityService,
				null,
				uuidFactory));
		migrators.add(new DefaultMobTemplatePopulator(transactionManager, daoFactory.getSpellDao(), daoFactory.getMobTemplateDao(), daoFactory.getFactionDao(), daoFactory.getCreatureTypeDao()
		));
		migrators.add(new DevAccountPopulator(transactionManager, transactionProxyWrapper.wrap(new AccountServiceImpl(daoFactory.getAccountDao())), realPlayerCreationService, daoFactory.getAccountDao()));

		daoFactory.runMigrators(migrators);

		player = realPlayerService.getPlayer("Dem");     //wtf
		target = realPlayerService.getPlayer("hiflyer");

		playerService = mock(PlayerService.class, Settings.spyOn(realPlayerService));

		currentActionService = new CurrentActionService();

		ClientConnectionImpl clientConnection = mock(ClientConnectionImpl.class);
		stubReturn(player).on(clientConnection).getPlayer();


		movementService = new MovementServiceImpl(smrtBroadcaster, timeProvider, actionScheduler, spellCombatService, visibilityService, playerService, currentActionService
		);

		CombatMechanics combatMechanics = new SimpleCombatMechanics(new RealRandomProvider());

		spellCombatService = new SpellCombatServiceImpl(mock(EntityCombatService.class), combatMechanics, mock(ActionScheduler.class), currentActionService, smrtBroadcaster, visibilityService
		);

		clientCombatMessagesAuth = new ClientCombatMessagesAuth(clientConnection, spellCombatService, spellService, timeProvider, mock(CooldownService.class));
		clientMovementMessagesAuth = new ClientMovementMessagesAuth(clientConnection, timeProvider, movementService);

		for (ServerSpell playerSpell : spellService.getSpellsForEntity(player)) {
			if (playerSpell.getName().equals("Lazor blast")) {
				spell = playerSpell;
				break;
			}
		}

	}
	*/

	@Test
	public void autopass() {
	}

	/*
	@Test
	public void testMoveToSameVectorAfterSpellCastShouldNotCancelIt() {
		target.setPositionalData(new PersistedPositionalData(SpacedVector3.ZERO, SpacedRotation.IDENTITY));

		clientMovementMessagesAuth.updatePositionalData(new PositionalData(new SpacedVector3(10, 10, 10), SpacedRotation.IDENTITY));

		timeProvider.advanceTime(5 * 1000);
		clientMovementMessagesAuth.updatePositionalData(new PositionalData(new SpacedVector3(15, 10, 10), SpacedRotation.IDENTITY));

		timeProvider.advanceTime(6 * 1000);
		clientMovementMessagesAuth.updatePositionalData(new PositionalData(new SpacedVector3(20, 10, 10), SpacedRotation.IDENTITY));

		clientMovementMessagesAuth.updatePositionalData(new PositionalData(new SpacedVector3(25, 10, 10), SpacedRotation.IDENTITY));
		clientCombatMessagesAuth.startSpellCast(target, spell);
		assertNotNull(currentActionService.getCurrentAction(player));
		clientMovementMessagesAuth.updatePositionalData(new PositionalData(new SpacedVector3(25, 10, 10), SpacedRotation.IDENTITY));
		assertNotNull(currentActionService.getCurrentAction(player));

		timeProvider.advanceTime(6 * 1000);
		clientMovementMessagesAuth.updatePositionalData(new PositionalData(new SpacedVector3(25, 10, 10), SpacedRotation.IDENTITY));	  //this is almost a bug imo

		verifyExactly(2).on(playerService).updatePlayer(player);

	}
	*/
}
