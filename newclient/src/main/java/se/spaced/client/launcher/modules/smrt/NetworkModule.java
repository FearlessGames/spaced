package se.spaced.client.launcher.modules.smrt;

import com.google.inject.*;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import org.apache.mina.core.service.IoProcessor;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.executor.OrderedThreadPoolExecutor;
import org.apache.mina.transport.socket.nio.NioProcessor;
import org.apache.mina.transport.socket.nio.NioSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import se.fearless.common.lifetime.ExecutorServiceLifetimeAdapter;
import se.fearless.common.lifetime.LifetimeManager;
import se.fearless.common.time.SystemTimeProvider;
import se.fearless.common.time.TimeProvider;
import se.smrt.core.remote.mina.ByteArrayDecoder;
import se.smrt.core.remote.mina.ByteArrayEncoder;
import se.spaced.client.model.ClientEntity;
import se.spaced.client.model.ClientSpell;
import se.spaced.client.model.control.GroundImpactListener;
import se.spaced.client.model.control.GroundImpactListenerImpl;
import se.spaced.client.model.cooldown.ClientCooldown;
import se.spaced.client.model.cooldown.ClientCooldownService;
import se.spaced.client.model.cooldown.ClientCooldownServiceImpl;
import se.spaced.client.model.item.ClientItem;
import se.spaced.client.model.item.ItemLookup;
import se.spaced.client.model.item.ItemTemplateServiceImpl;
import se.spaced.client.net.GameServer;
import se.spaced.client.net.messagelisteners.*;
import se.spaced.client.net.mina.ConnectionHandler;
import se.spaced.client.net.ping.PingManager;
import se.spaced.client.net.ping.PingManagerAdapter;
import se.spaced.client.net.ping.PingManagerImpl;
import se.spaced.client.net.remoteservices.ServerInfoWSC;
import se.spaced.client.net.remoteservices.ServerInfoWSCImpl;
import se.spaced.client.net.smrt.*;
import se.spaced.messages.protocol.*;
import se.spaced.messages.protocol.c2s.remote.C2SRequiredWriteCodec;
import se.spaced.messages.protocol.s2c.*;
import se.spaced.messages.protocol.s2c.object.MessageObject;
import se.spaced.messages.protocol.s2c.object.S2CAllMessagesToObject;
import se.spaced.messages.protocol.s2c.remote.S2CIncomingMessageHandler;
import se.spaced.messages.protocol.s2c.remote.S2CRequiredReadCodec;
import se.spaced.shared.activecache.ActiveCache;
import se.spaced.shared.concurrency.SimpleThreadFactory;
import se.spaced.shared.model.AnimationState;
import se.spaced.shared.network.protocol.codec.mina.MessageCodecFactory;
import se.spaced.shared.playback.RecordingPoint;
import se.spaced.shared.scheduler.JobManager;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public final class NetworkModule extends AbstractModule {
	private final List<GameServer> gameServers;

	public NetworkModule(List<GameServer> gameServers) {
		this.gameServers = gameServers;
	}

	@Override
	public void configure() {
		bind(ServerConnection.class).to(ServerConnectionImpl.class).in(Scopes.SINGLETON);
		bind(TimeProvider.class).to(SystemTimeProvider.class).in(Scopes.SINGLETON);
		bind(JobManager.class).in(Scopes.SINGLETON);

		bind(new TypeLiteral<List<GameServer>>() {
		}).annotatedWith(Names.named("gameServers")).toInstance(gameServers);
		bind(ServerInfoWSC.class).to(ServerInfoWSCImpl.class);

		bind(PingManager.class).to(PingManagerImpl.class).in(Scopes.SINGLETON);

		bind(ProtocolDecoder.class).to(ByteArrayDecoder.class);
		bind(ProtocolEncoder.class).to(ByteArrayEncoder.class);

		bind(ServerConnectionMessages.class).to(ServerConnectionMessagesImpl.class);
		bind(ServerChatMessages.class).to(ServerChatMessagesImpl.class);
		bind(ServerCombatMessages.class).to(ServerCombatMessagesImpl.class);
		bind(ServerProjectileMessages.class).to(ServerCombatMessagesImpl.class);
		bind(ServerSpellMessages.class).to(SpellListener.class);
		bind(ServerEntityDataMessages.class).to(ServerEntityDataMessagesImpl.class);
		bind(ServerItemMessages.class).to(ServerItemMessagesImpl.class);
		bind(ServerEquipmentMessages.class).to(ServerEquipmentMessagesImpl.class);
		bind(ServerTradeMessages.class).to(ServerTradeMessagesImpl.class);
		bind(ServerVendorMessages.class).to(ServerVendorMessagesImpl.class);
		bind(ServerAccountMessages.class).to(ServerAccountMessagesImpl.class);
		bind(ServerMovementMessages.class).to(ServerMovementMessagesImpl.class);
		bind(ServerGameMasterMessages.class).to(ServerGameMasterMessagesImpl.class);
		bind(ServerLootMessages.class).to(ServerLootMessagesListener.class);
		bind(S2CRequiredReadCodec.class).to(ServerToClientReadCodec.class).in(Scopes.SINGLETON);
		bind(C2SRequiredWriteCodec.class).to(ClientToServerWriteCodec.class);

		bind(MessageQueue.class).to(MessageQueueImpl.class);
		bind(GroundImpactListener.class).to(GroundImpactListenerImpl.class);

		bind(ClientCooldownService.class).to(ClientCooldownServiceImpl.class);
	}

	@Singleton
	@Provides
	public ActiveCache<Cooldown, ClientCooldown> getCooldownCache(ClientCooldownService clientCooldownService) {
		return clientCooldownService;
	}

	@Singleton
	@Provides
	public ActiveCache<ItemTemplate, ItemTemplateData> getItemTemplateService(ServerConnection serverConnection) {
		return new ItemTemplateServiceImpl(serverConnection);
	}

	@Singleton
	@Provides
	public ActiveCache<SpacedItem, ClientItem> getItemLookup(ActiveCache<ItemTemplate, ItemTemplateData> itemTemplateCache) {
		return new ItemLookup(itemTemplateCache);
	}


	@Singleton
	@Provides
	public ActiveCache<Entity, ClientEntity> getEntityService(ServerConnection serverConnection) {
		return new EntityCacheImpl(serverConnection);
	}

	@Singleton
	@Provides
	public ActiveCache<Spell, ClientSpell> getSpellCache(ServerConnection serverConnection) {
		return new SpellCacheImpl(serverConnection);
	}

	@Singleton
	@Provides
	public Queue<MessageObject> getMessageQueue() {
		return new LinkedBlockingQueue<MessageObject>();
	}


	@Singleton
	@Provides
	@Named("clientThread")
	public S2CProtocol getAllMessages(Queue<MessageObject> queue) {
		return new S2CAllMessagesToObject(queue);
	}

	@Singleton
	@Provides
	@Named("mainThread")
	public S2CProtocol getAllMessages(
			ServerConnectionMessages connectionMessages,
			ServerChatMessages chatMessages,
			PingManagerAdapter pingManagerAdapter,
			ServerEntityDataMessages entityDataMessages,
			ServerMovementMessages movementMessages,
			ServerCombatMessages serverCombatMessages,
			ServerProjectileMessages serverProjectileMessages,
			ServerSpellMessages spellListener,
			ServerItemMessages itemListener,
			ServerAccountMessages accountMessages,
			ServerGameMasterMessages gameMasterMessages,
			ServerTradeMessages tradeMessages,
			ServerVendorMessages vendorMessages,
			ServerLootMessages lootMessages,
			ServerEquipmentMessages equipmentMessages) {

		return new S2CLogDecorator(new S2CMultiDispatcher().
				add(connectionMessages).
				add(chatMessages).
				add(pingManagerAdapter).
				add(entityDataMessages).
				add(movementMessages).
				add(serverCombatMessages).
				add(serverProjectileMessages).
				add(itemListener).
				add(spellListener).
				add(gameMasterMessages).
				add(tradeMessages).
				add(vendorMessages).
				add(lootMessages).
				add(equipmentMessages).
				add(accountMessages), "<<<< ") {
			@Override
			public void sendPlayback(
					Entity entity, RecordingPoint<AnimationState> recordingPoint) {
				delegate.movement().sendPlayback(entity, recordingPoint);
			}
		};
	}

	@Provides
	@Singleton
	public NioSocketConnector getNioSocketConnector(
			MessageCodecFactory messageCodecFactory,
			ConnectionHandler serverConnectionHandler, LifetimeManager lifetimeManager) {
		// newCachedThreadPool seems to be the mina default for this class
		final ExecutorService nioExecutor = Executors.newCachedThreadPool(SimpleThreadFactory.withPrefix(
				"nioSocketConnectorThread-"));
		IoProcessor<NioSession> processor = new NioProcessor(nioExecutor);
		NioSocketConnector nioSocketConnector = new NioSocketConnector(nioExecutor, processor);


		final ExecutorService filterExecutor = new OrderedThreadPoolExecutor(5,
				100,
				60L,
				TimeUnit.SECONDS,
				SimpleThreadFactory.withPrefix("minaThread-"));
		lifetimeManager.addListener(new ExecutorServiceLifetimeAdapter(nioExecutor, filterExecutor));

		nioSocketConnector.getFilterChain().addLast("threadPool", new ExecutorFilter(filterExecutor));
		nioSocketConnector.getFilterChain().addLast("codec", new ProtocolCodecFilter(messageCodecFactory));
		nioSocketConnector.getSessionConfig().setTcpNoDelay(true);
		nioSocketConnector.getSessionConfig().setReuseAddress(true);

		nioSocketConnector.setHandler(serverConnectionHandler);
		return nioSocketConnector;
	}

	@Provides
	@Singleton
	public S2CIncomingMessageHandler getIncomingMessageHandler(S2CRequiredReadCodec requiredReadCodec) {
		return new S2CIncomingMessageHandler(requiredReadCodec);
	}
}
