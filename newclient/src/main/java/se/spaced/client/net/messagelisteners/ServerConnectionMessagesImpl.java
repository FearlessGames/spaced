package se.spaced.client.net.messagelisteners;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearless.common.util.TimeProvider;
import se.fearless.common.uuid.UUID;
import se.spaced.client.ardor.ui.events.OnlineEvents;
import se.spaced.client.core.states.LoadingState;
import se.spaced.client.environment.time.GameTimeManager;
import se.spaced.client.model.ClientEntity;
import se.spaced.client.model.UserCharacter;
import se.spaced.client.model.control.ClientTeleporter;
import se.spaced.client.model.control.states.LocalRecorder;
import se.spaced.client.model.listener.ClientEntityListener;
import se.spaced.client.model.listener.LoginListener;
import se.spaced.client.model.player.PlayerEntityProvider;
import se.spaced.client.net.smrt.ServerConnection;
import se.spaced.client.resources.zone.RootZoneService;
import se.spaced.client.resources.zone.ScenegraphService;
import se.spaced.messages.protocol.Entity;
import se.spaced.messages.protocol.Salts;
import se.spaced.messages.protocol.SpacedItem;
import se.spaced.messages.protocol.s2c.ServerConnectionMessages;
import se.spaced.shared.activecache.ActiveCache;
import se.spaced.shared.events.EventHandler;
import se.spaced.shared.model.AnimationState;
import se.spaced.shared.model.PositionalData;
import se.spaced.shared.model.items.ContainerType;
import se.spaced.shared.network.protocol.codec.datatype.EntityData;
import se.spaced.shared.util.ListenerDispatcher;
import se.spaced.shared.world.TimeSystemInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Singleton
public class ServerConnectionMessagesImpl implements ServerConnectionMessages {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final EventHandler eventHandler;
	private final ListenerDispatcher<ClientEntityListener> entityDispatcher;
	private final ServerConnection serverConnection;
	private final ListenerDispatcher<LoginListener> loginDispatcher;
	private final UserCharacter userCharacter;
	private final PlayerEntityProvider playerProvider;
	private final RootZoneService rootZoneService;
	private final LoadingState loadingState;
	private final ActiveCache<Entity, ClientEntity> entityCache;
	private final ServerItemMessagesImpl itemMessages;
	private final LocalRecorder localRecorder;
	private final ScenegraphService scenegraphService;
	private final ClientTeleporter clientTeleporter;
	private final GameTimeManager gameTimeManager;
	private final TimeProvider timeProvider;
	private final ServerEquipmentMessagesImpl equipmentMessages;

	@Inject
	public ServerConnectionMessagesImpl(
			EventHandler eventHandler,
			ListenerDispatcher<ClientEntityListener> entityDispatcher,
			ServerConnection serverConnection,
			ListenerDispatcher<LoginListener> loginDispatcher,
			UserCharacter userCharacter,
			PlayerEntityProvider playerProvider,
			RootZoneService rootZoneService,
			LoadingState loadingState,
			ActiveCache<Entity, ClientEntity> entityCache,
			ServerItemMessagesImpl itemMessages,
			LocalRecorder localRecorder,
			ScenegraphService scenegraphService,
			ClientTeleporter clientTeleporter, GameTimeManager gameTimeManager, TimeProvider timeProvider, ServerEquipmentMessagesImpl equippedItem) {
		this.eventHandler = eventHandler;
		this.entityDispatcher = entityDispatcher;
		this.serverConnection = serverConnection;
		this.loginDispatcher = loginDispatcher;
		this.userCharacter = userCharacter;
		this.playerProvider = playerProvider;
		this.rootZoneService = rootZoneService;
		this.loadingState = loadingState;
		this.entityCache = entityCache;
		this.itemMessages = itemMessages;
		this.localRecorder = localRecorder;
		this.scenegraphService = scenegraphService;
		this.clientTeleporter = clientTeleporter;
		this.gameTimeManager = gameTimeManager;
		this.timeProvider = timeProvider;
		this.equipmentMessages = equippedItem;
	}

	@Override
	public void accountLoginResponse(final String accountName, final boolean successful, final String message) {
		log.info("AccountLoginRespone {} - {}", new Object[]{accountName, message});
		if (successful) {
			serverConnection.getReceiver().connection().requestPlayerList();
		} else {
			eventHandler.fireAsynchEvent(OnlineEvents.LOGIN_FAILED, message);
			log.info("loginUnsuccessful");
		}
	}

	@Override
	public void playerLoginResponse(
			final boolean successful,
			final String message,
			final EntityData entityData,
			Map<ContainerType, ? extends SpacedItem> equipment, boolean isGm) {
		if (successful) {

			ClientEntity spacedEntity = new ClientEntity(entityData, entityDispatcher);
			entityCache.setValue(spacedEntity, spacedEntity);

			serverConnection.getReceiver().spell().requestSpellBook();
			userCharacter.setControlledEntity(spacedEntity);
			userCharacter.setIsGm(isGm);
			playerProvider.setPlayerEntity(spacedEntity);
			for (Map.Entry<ContainerType, ? extends SpacedItem> entry : equipment.entrySet()) {
				equipmentMessages.equippedItem(entry.getValue(), entry.getKey());
			}
			clientTeleporter.forcePosition(new PositionalData(spacedEntity.getPosition(), spacedEntity.getRotation()));
			localRecorder.startRecording(spacedEntity.getPosition(), spacedEntity.getRotation(), AnimationState.IDLE);
			loginDispatcher.trigger().successfulPlayerLogin();
		} else {
			loginDispatcher.trigger().failedPlayerLogin(message);
		}
	}

	@Override
	public void playerLoggedIn(final EntityData data) {
		ClientEntity entity = new ClientEntity(data, entityDispatcher);
		eventHandler.fireEvent(OnlineEvents.PLAYER_LOGGED_IN, entity);
	}

	@Override
	public void playerDisconnected(final Entity entity, String name) {
		ClientEntity clientEntity = entityCache.getValue(entity);
		eventHandler.fireEvent(OnlineEvents.PLAYER_LOGGED_OUT, clientEntity, name);

		entityCache.delete(entity);
	}

	@Override
	public void playerListResponse(final List<EntityData> players) {
		log.info("playerListResponse({})", players);
		loginDispatcher.trigger().characterListUpdated(players);
	}

	@Override
	public void requestSaltsResponse(Salts salts) {
		log.info("received salts: " + salts);
		eventHandler.fireAsynchEvent(OnlineEvents.RECEIVED_SALTS, salts);
	}

	@Override
	public void logoutResponse() {
		loginDispatcher.trigger().successfulPlayerLogout();
	}

	@Override
	public void locationResponse(
			final UUID playerId,
			String worldName,
			PositionalData position,
			TimeSystemInfo timeSystemInfo,
			long currentTimeOffset) {
		final AtomicInteger maxTasks = new AtomicInteger();

		loadingState.setPosition(position.getPosition());
		scenegraphService.setLoadListener(new SceneLoadListener(maxTasks, playerId,
				serverConnection.getReceiver().connection(), eventHandler, scenegraphService));
		rootZoneService.setFileName(worldName);
		rootZoneService.reload(position.getPosition());
		gameTimeManager.setLocalTimeInfo(timeSystemInfo, currentTimeOffset, timeProvider.now());
	}

	@Override
	public void playerInfoResponse(UUID playerPk, final Map<ContainerType, String> equippedItems, final boolean isGm) {
		eventHandler.fireEvent(OnlineEvents.CHARSELECT_PLAYER_DATA_UPDATE, playerPk.toString(), equippedItems, isGm);
	}

	@Override
	public void needsAuthenticator() {
		eventHandler.fireEvent(OnlineEvents.REQUIRES_AUTHENTICATOR_AUTHENTICATION);
	}

}
