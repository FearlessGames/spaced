package se.spaced.client.net.mina;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.client.net.smrt.ServerConnectionListener;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.messages.protocol.s2c.remote.S2CChecksum;
import se.spaced.messages.protocol.s2c.remote.S2CIncomingMessageHandler;
import se.spaced.messages.protocol.s2c.remote.S2CRemoteProtocolErrorHandler;
import se.spaced.messages.protocol.s2c.remote.S2CVersionReceiver;
import se.spaced.shared.util.ListenerDispatcher;

import java.io.ByteArrayInputStream;

@Singleton
public class ConnectionHandler extends IoHandlerAdapter implements S2CRemoteProtocolErrorHandler {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private static final int PING_TIMEOUT = 20;
	private final S2CIncomingMessageHandler incomingMessageHandler;
	private final S2CVersionReceiver serverListener;
	private final ListenerDispatcher<ServerConnectionListener> dispatcher;
	private IoSession session;

	@Inject
	public ConnectionHandler(@Named("clientThread") S2CProtocol serverListener, S2CIncomingMessageHandler incomingMessageHandler,
									 ListenerDispatcher<ServerConnectionListener> dispatcher) {
		this.serverListener = new S2CVersionReceiver(serverListener, this);
		this.incomingMessageHandler = incomingMessageHandler;
		this.dispatcher = dispatcher;
	}

	@Override
	public void sessionOpened(IoSession session) {
		this.session = session;
		session.getConfig().setIdleTime(IdleStatus.READER_IDLE, PING_TIMEOUT);
	}

	@Override
	public void messageReceived(IoSession session, Object o) throws Exception {
		byte[] data = (byte[]) o;
		incomingMessageHandler.handleMessage(new ByteArrayInputStream(data), serverListener);
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable throwable) throws Exception {
		log.error("Got exception from mina", throwable);
		dispatcher.trigger().disconnected(throwable.getMessage());
		session.close(true);
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		dispatcher.trigger().disconnected("Lost connection to server");
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) {
		if (status == IdleStatus.READER_IDLE) {
			dispatcher.trigger().disconnected("Ping timeout");
			session.close(true);
		}
	}

	@Override
	public void versionMismatch(S2CChecksum expected, S2CChecksum actual) {
		log.error("Version mismatch, expected " + expected + ", got " + actual);
		session.close(true);
	}
}
