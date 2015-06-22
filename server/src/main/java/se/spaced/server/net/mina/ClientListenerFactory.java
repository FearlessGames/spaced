package se.spaced.server.net.mina;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.fearlessgames.common.util.TimeProvider;
import se.fearlessgames.common.util.uuid.UUIDFactory;
import se.spaced.messages.protocol.c2s.C2SLogDecorator;
import se.spaced.messages.protocol.c2s.C2SMultiDispatcher;
import se.spaced.messages.protocol.c2s.C2SProtocol;
import se.spaced.messages.protocol.c2s.ClientAccountMessages;
import se.spaced.messages.protocol.c2s.ClientChatMessages;
import se.spaced.messages.protocol.c2s.ClientCombatMessages;
import se.spaced.messages.protocol.c2s.ClientConnectionMessages;
import se.spaced.messages.protocol.c2s.ClientEntityDataMessages;
import se.spaced.messages.protocol.c2s.ClientEquipmentMessages;
import se.spaced.messages.protocol.c2s.ClientGameMasterMessages;
import se.spaced.messages.protocol.c2s.ClientItemMessages;
import se.spaced.messages.protocol.c2s.ClientMovementMessages;
import se.spaced.messages.protocol.c2s.ClientPingMessages;
import se.spaced.messages.protocol.c2s.ClientSpellMessages;
import se.spaced.messages.protocol.c2s.ClientTradeMessages;
import se.spaced.messages.protocol.c2s.ClientVendorMessages;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.messages.protocol.s2c.ServerConnectionMessages;
import se.spaced.messages.protocol.s2c.ServerPingMessages;
import se.spaced.server.account.AccountService;
import se.spaced.server.mob.MobOrderExecutor;
import se.spaced.server.model.action.ActionScheduler;
import se.spaced.server.model.combat.EntityCombatService;
import se.spaced.server.model.combat.EntityTargetService;
import se.spaced.server.model.combat.SpellCombatService;
import se.spaced.server.model.cooldown.CooldownService;
import se.spaced.server.model.crafting.SalvageService;
import se.spaced.server.model.currency.MoneyService;
import se.spaced.server.model.entity.EntityService;
import se.spaced.server.model.entity.VisibilityService;
import se.spaced.server.model.items.EquipmentService;
import se.spaced.server.model.items.InventoryService;
import se.spaced.server.model.items.ItemService;
import se.spaced.server.model.movement.MovementService;
import se.spaced.server.model.movement.UnstuckService;
import se.spaced.server.model.player.RemotePlayerService;
import se.spaced.server.model.spawn.SpawnService;
import se.spaced.server.model.vendor.VendorService;
import se.spaced.server.model.world.TimeService;
import se.spaced.server.net.ClientConnection;
import se.spaced.server.net.broadcast.SmrtBroadcaster;
import se.spaced.server.net.listeners.auth.AuthenticatorService;
import se.spaced.server.net.listeners.auth.ClientAccountMessageAuth;
import se.spaced.server.net.listeners.auth.ClientChatMessagesAuth;
import se.spaced.server.net.listeners.auth.ClientCombatMessagesAuth;
import se.spaced.server.net.listeners.auth.ClientConnectionMessageAuth;
import se.spaced.server.net.listeners.auth.ClientEntityDataMessageAuth;
import se.spaced.server.net.listeners.auth.ClientEquipmentMessagesAuth;
import se.spaced.server.net.listeners.auth.ClientGameMasterMessagesAuth;
import se.spaced.server.net.listeners.auth.ClientItemMessagesAuth;
import se.spaced.server.net.listeners.auth.ClientMovementMessagesAuth;
import se.spaced.server.net.listeners.auth.ClientSpellMessagesAuth;
import se.spaced.server.net.listeners.auth.ClientTradeMessagesAuth;
import se.spaced.server.net.listeners.auth.ClientVendorMessagesAuth;
import se.spaced.server.net.listeners.auth.GameMasterApiImpl;
import se.spaced.server.net.listeners.auth.GmMobLifecycle;
import se.spaced.server.net.listeners.auth.PingMessageAuth;
import se.spaced.server.persistence.dao.impl.hibernate.TransactionManager;
import se.spaced.server.persistence.dao.interfaces.BrainTemplateDao;
import se.spaced.server.persistence.dao.interfaces.EntityTemplateDao;
import se.spaced.server.persistence.migrator.ServerContentPopulator;
import se.spaced.server.player.PlayerCreationService;
import se.spaced.server.services.GameMasterService;
import se.spaced.server.services.auth.GmAuthenticator;
import se.spaced.server.services.auth.PlayerAuthenticationProxyWrapper;
import se.spaced.server.spell.SpellService;
import se.spaced.server.trade.TradeService;
import se.spaced.shared.model.AnimationState;
import se.spaced.shared.playback.RecordingPoint;

@Singleton
public class ClientListenerFactory {
	private final AccountService accountService;
	private final TimeProvider timeProvider;
	private final EntityTargetService entityTargetService;
	private final EntityCombatService entityCombatService;
	private final RemotePlayerService remotePlayerService;
	private final EntityService entityService;
	private final SpellService spellService;
	private final SpellCombatService spellCombatService;
	private final ActionScheduler scheduler;
	private final ItemService itemService;
	private final PlayerCreationService playerCreationService;
	private final InventoryService inventoryService;
	private final MovementService movementService;
	private final EquipmentService equipmentService;
	private final SpawnService spawnService;
	private final EntityTemplateDao entityTemplateDao;
	private final UUIDFactory uuidFactory;
	private final GmMobLifecycle gmMobLifecycle;
	private final TransactionManager transactionManager;
	private final BrainTemplateDao brainTemplateDao;
	private final TradeService tradeService;
	private final CooldownService cooldownService;
	private final ServerContentPopulator serverContentPopulator;
	private final MobOrderExecutor mobOrderExecutor;
	private final MoneyService moneyService;
	private final GameMasterService gameMasterService;
	private final AuthenticatorService authenticatorService;
	private final VendorService vendorService;
	private final SalvageService salvageService;
	private final UnstuckService unstuckService;
	private final VisibilityService visibilityService;
	private final TimeService timeService;


	@Inject
	public ClientListenerFactory(
			AccountService accountService, TimeProvider timeProvider,
			EntityTargetService entityTargetService,
			EntityCombatService entityCombatService,
			RemotePlayerService remotePlayerService,
			EntityService entityService,
			SpellService spellService,
			SpellCombatService spellCombatService,
			ActionScheduler scheduler,
			ItemService itemService,
			InventoryService inventoryService,
			MovementService movementService,
			EquipmentService equipmentService,
			PlayerCreationService playerCreationService,
			SpawnService spawnService,
			EntityTemplateDao entityTemplateDao,
			UUIDFactory uuidFactory,
			GmMobLifecycle gmMobLifecycle,
			TransactionManager transactionManager,
			BrainTemplateDao brainTemplateDao,
			TradeService tradeService,
			CooldownService cooldownService,
			ServerContentPopulator serverContentPopulator,
			MobOrderExecutor mobOrderExecutor,
			MoneyService moneyService,
			GameMasterService gameMasterService,
			AuthenticatorService authenticatorService,
			VendorService vendorService,
			SalvageService salvageService,
			UnstuckService unstuckService, VisibilityService visibilityService, TimeService timeService) {
		this.accountService = accountService;
		this.timeProvider = timeProvider;
		this.entityTargetService = entityTargetService;
		this.entityCombatService = entityCombatService;
		this.remotePlayerService = remotePlayerService;
		this.entityService = entityService;
		this.spellService = spellService;
		this.spellCombatService = spellCombatService;
		this.scheduler = scheduler;
		this.itemService = itemService;
		this.inventoryService = inventoryService;
		this.movementService = movementService;
		this.equipmentService = equipmentService;
		this.playerCreationService = playerCreationService;
		this.spawnService = spawnService;
		this.entityTemplateDao = entityTemplateDao;
		this.uuidFactory = uuidFactory;
		this.gmMobLifecycle = gmMobLifecycle;
		this.transactionManager = transactionManager;
		this.brainTemplateDao = brainTemplateDao;
		this.tradeService = tradeService;
		this.cooldownService = cooldownService;
		this.serverContentPopulator = serverContentPopulator;
		this.mobOrderExecutor = mobOrderExecutor;
		this.moneyService = moneyService;
		this.gameMasterService = gameMasterService;
		this.authenticatorService = authenticatorService;
		this.vendorService = vendorService;
		this.salvageService = salvageService;
		this.unstuckService = unstuckService;

		this.visibilityService = visibilityService;
		this.timeService = timeService;
	}

	public C2SProtocol create(
			ClientConnection clientConnection,
			ServerConnectionMessages response,
			ServerPingMessages pingResponse, SmrtBroadcaster<S2CProtocol> broadcaster) {
		ClientConnectionMessages clientConnectionMessages = new ClientConnectionMessageAuth(clientConnection,
				accountService,
				response,
				remotePlayerService,
				authenticatorService, equipmentService, timeService);
		ClientEntityDataMessages clientEntityDataMessages = new ClientEntityDataMessageAuth(clientConnection,
				entityTargetService, entityCombatService, entityService, unstuckService,
				visibilityService);

		ClientSpellMessages clientSpellMessages = new ClientSpellMessagesAuth(clientConnection, spellService);
		ClientCombatMessages clientCombatMessages = new ClientCombatMessagesAuth(
				clientConnection, spellCombatService, spellService, timeProvider, cooldownService);
		ClientChatMessages clientChatMessages = new ClientChatMessagesAuth(broadcaster, clientConnection, entityService);
		ClientItemMessages clientItemMessages = new ClientItemMessagesAuth(clientConnection,
				itemService,
				entityTargetService,
				inventoryService, salvageService);

		ClientEquipmentMessages clientEquipmentMessages = new ClientEquipmentMessagesAuth(clientConnection, equipmentService);

		ClientPingMessages clientPingMessages = new PingMessageAuth(pingResponse);
		ClientMovementMessages clientMovementMessages = new ClientMovementMessagesAuth(clientConnection,
				timeProvider,
				movementService);

		GameMasterApiImpl gameMasterApi = new GameMasterApiImpl(
				entityService, clientConnection, broadcaster,
				itemService, entityTemplateDao,
				transactionManager, brainTemplateDao, spellService, moneyService,
				gameMasterService);
		ClientGameMasterMessages clientGameMasterMessages = new ClientGameMasterMessagesAuth(
				PlayerAuthenticationProxyWrapper.wrap(
						gameMasterApi, new GmAuthenticator()),
				clientConnection);
		ClientAccountMessages clientAccountMessages = new ClientAccountMessageAuth(clientConnection,
				response,
				playerCreationService);

		ClientTradeMessages clientTradeMessages = new ClientTradeMessagesAuth(clientConnection,
				tradeService,
				itemService);

		ClientVendorMessages clientVendorMessages = new ClientVendorMessagesAuth(clientConnection, vendorService,
				broadcaster);

		return new C2SLogDecorator(new C2SMultiDispatcher().
				add(clientConnectionMessages).
				add(clientEntityDataMessages).
				add(clientSpellMessages).
				add(clientCombatMessages).
				add(clientPingMessages).
				add(clientItemMessages).
				add(clientChatMessages).
				add(clientMovementMessages).
				add(clientAccountMessages).
				add(clientTradeMessages).
				add(clientGameMasterMessages).
				add(clientEquipmentMessages).
				add(clientVendorMessages), "<<<< ") {

			@Override
			public void sendPlayback(RecordingPoint<AnimationState> recordingPoint) {
				delegate.movement().sendPlayback(recordingPoint);
			}
		};
	}
}
