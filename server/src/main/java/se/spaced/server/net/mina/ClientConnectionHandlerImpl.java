package se.spaced.server.net.mina;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearless.common.uuid.UUID;
import se.fearless.common.uuid.UUIDFactory;
import se.spaced.messages.protocol.Entity;
import se.spaced.messages.protocol.c2s.remote.C2SIncomingMessageHandler;
import se.spaced.messages.protocol.c2s.remote.C2SRequiredReadCodec;
import se.spaced.messages.protocol.s2c.S2CLogDecorator;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.messages.protocol.s2c.remote.S2CChecksum;
import se.spaced.messages.protocol.s2c.remote.S2CRequiredWriteCodec;
import se.spaced.messages.protocol.s2c.remote.mina.S2CMinaReceiver;
import se.spaced.server.model.Player;
import se.spaced.server.model.player.RemotePlayerService;
import se.spaced.server.net.ClientConnection;
import se.spaced.server.net.ClientConnectionHandler;
import se.spaced.server.net.ClientConnectionImpl;
import se.spaced.server.net.broadcast.SmrtBroadcaster;
import se.spaced.shared.model.AnimationState;
import se.spaced.shared.playback.RecordingPoint;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class ClientConnectionHandlerImpl extends IoHandlerAdapter implements ClientConnectionHandler {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final String CLIENT_CONNECTION = "CLIENT_CONNECTION";

	private final Map<UUID, ClientConnection> connectedClients;

	private final UUIDFactory uuidFactory;
	private final C2SIncomingMessageHandler incomingMessageHandler;
	private final ClientListenerFactory clientListenerProvider;
	private final S2CRequiredWriteCodec writeCodec;
	private final RemotePlayerService remotePlayerService;

	private final SmrtBroadcaster<S2CProtocol> broadcaster;


	@Inject
	public ClientConnectionHandlerImpl(C2SRequiredReadCodec codec,
												ClientListenerFactory clientListenerProvider,
												S2CRequiredWriteCodec writeCodec, UUIDFactory uuidFactory,
												SmrtBroadcaster<S2CProtocol> broadcaster, RemotePlayerService remotePlayerService) {
		this.clientListenerProvider = clientListenerProvider;
		this.writeCodec = writeCodec;
		this.uuidFactory = uuidFactory;
		this.broadcaster = broadcaster;
		this.remotePlayerService = remotePlayerService;
		connectedClients = new ConcurrentHashMap<UUID, ClientConnection>();
		incomingMessageHandler = new C2SIncomingMessageHandler(codec);
	}

	@Override
	public void sessionOpened(IoSession ioSession) throws Exception {
		S2CMinaReceiver<ByteArrayOutputStream> minaReceiver = new S2CMinaReceiver<ByteArrayOutputStream>(
				writeCodec, new ByteArrayOutputStream(), null, ioSession);
		S2CProtocol response = new S2CLogDecorator(
				minaReceiver, ">>>> ") {

			@Override
			public void sendPlayback(Entity entity, RecordingPoint<AnimationState> recordingPoint) {
				delegate.movement().sendPlayback(entity, recordingPoint);
			}
		};
		ClientConnection clientConnection = new ClientConnectionImpl(ioSession, uuidFactory.randomUUID(), response, clientListenerProvider, broadcaster);

		setClientConnection(ioSession, clientConnection);
		connectedClients.put(clientConnection.getUUID(), clientConnection);
		logger.info("Client " + clientConnection.getUUID() + " connected");

		S2CChecksum.INSTANCE.sendVersion(minaReceiver);
	}

	@Override
	public void sessionClosed(IoSession ioSession) throws Exception {
		ClientConnection clientConnection = getClientConnection(ioSession);
		logger.info("Client " + clientConnection.getUUID() + " rejected");
		ioSession.close(true).awaitUninterruptibly(10 * 1000);

		Player player = clientConnection.getPlayer();
		if (player != null) {
			remotePlayerService.playerLoggedOut(player, clientConnection);
		}
		connectedClients.remove(clientConnection.getUUID());
	}

	@Override
	public void sessionIdle(IoSession ioSession, IdleStatus idleStatus) throws Exception {
		ClientConnection clientConnection = getClientConnection(ioSession);
		logger.info("Client " + clientConnection.getUUID() + " was idleing too long");
		sessionClosed(ioSession);
	}

	@Override
	public void exceptionCaught(IoSession ioSession, Throwable throwable) throws Exception {
		ClientConnection clientConnection = getClientConnection(ioSession);
		logger.info("Client " + clientConnection.getUUID() + " Exception!!!", throwable);

		sessionClosed(ioSession);
	}

	@Override
	public void messageReceived(IoSession ioSession, Object o) throws Exception {
		ClientConnection clientConnection = getClientConnection(ioSession);
		//logger.debug("Client " + clientConnection.getUUID() + " received: " + o.toString());

		byte[] data = (byte[]) o;

		try {
			incomingMessageHandler.handleMessage(new ByteArrayInputStream(data), clientConnection.getClientListener());
		} catch (Exception e) {
			logger.error("Exception on handleMessage " + clientConnection, e);
			throw e;
		}
	}

	@Override
	public Map<UUID, ClientConnection> getConnectedClients() {
		return connectedClients;
	}

	private ClientConnection getClientConnection(IoSession ioSession) {
		return (ClientConnection) ioSession.getAttribute(CLIENT_CONNECTION);
	}

	private void setClientConnection(IoSession ioSession, ClientConnection clientConnection) {
		ioSession.setAttribute(CLIENT_CONNECTION, clientConnection);
	}

	@Override
	public IoHandler getIOHandler() {
		return this;
	}
}
