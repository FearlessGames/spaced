package se.spaced.client.net.smrt;

import com.google.inject.Inject;
import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import se.fearless.common.lifetime.LifetimeListener;
import se.fearless.common.lifetime.LifetimeManager;
import se.fearless.common.time.TimeProvider;
import se.spaced.messages.protocol.c2s.C2SLogDecorator;
import se.spaced.messages.protocol.c2s.C2SProtocol;
import se.spaced.messages.protocol.c2s.remote.C2SChecksum;
import se.spaced.messages.protocol.c2s.remote.C2SRequiredWriteCodec;
import se.spaced.messages.protocol.c2s.remote.mina.C2SMinaReceiver;
import se.spaced.shared.model.AnimationState;
import se.spaced.shared.playback.RecordingPoint;
import se.spaced.shared.util.ListenerDispatcher;

import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;

public class ServerConnectionImpl implements ServerConnection {
	private final ListenerDispatcher<ServerConnectionListener> dispatcher;
	private final NioSocketConnector nioSocketConnector;
	private final TimeProvider timeProvider;
	private final C2SRequiredWriteCodec writeCodec;
	private IoSession session;
	private C2SProtocol response;

	@Inject
	public ServerConnectionImpl(
			ListenerDispatcher<ServerConnectionListener> dispatcher, NioSocketConnector nioSocketConnector, TimeProvider timeProvider,
			C2SRequiredWriteCodec writeCodec, LifetimeManager lifetimeManager) {
		this.dispatcher = dispatcher;
		this.nioSocketConnector = nioSocketConnector;
		this.timeProvider = timeProvider;
		this.writeCodec = writeCodec;
		lifetimeManager.addListener(new LifetimeListener() {
			@Override
			public void onStart() {
			}

			@Override
			public void onShutdown() {
				disconnect("Client initiated shutdown");
			}
		});
	}

	@Override
	public void connect(final String host, final int port) {
		disconnect(null);

		ConnectFuture future = nioSocketConnector.connect(new InetSocketAddress(host, port));
		future.addListener(new IoFutureListener<ConnectFuture>() {
			@Override
			public void operationComplete(ConnectFuture cf) {
				if (cf.isConnected()) {
					session = cf.getSession();

					C2SMinaReceiver minaReceiver = new C2SMinaReceiver<ByteArrayOutputStream>(writeCodec, new ByteArrayOutputStream(), null, session);
					response = new C2SLogDecorator(
							minaReceiver,
							">>>> ") {
						@Override
						public void sendPlayback(RecordingPoint<AnimationState> recordingPoint) {
							delegate.movement().sendPlayback(recordingPoint);
						}
					};

					dispatcher.trigger().connectionSucceeded(host, port);
					C2SChecksum.INSTANCE.sendVersion(minaReceiver);
				} else {
					dispatcher.trigger().connectionFailed("Failed to connect to server - " + cf.getException().getMessage());
				}
			}
		});
	}

	@Override
	public void disconnect(final String message) {
		if (isConnected()) {
			dispatcher.trigger().disconnected(message);
			CloseFuture closeFuture = session.close(true);
			closeFuture.awaitUninterruptibly();
		}
	}


	@Override
	public double getDownSpeed() {
		if (session != null) {
			session.updateThroughput(timeProvider.now(), false);
			return session.getReadBytesThroughput();
		}
		return 0;
	}

	@Override
	public double getUpSpeed() {
		if (session != null) {
			session.updateThroughput(timeProvider.now(), false);
			return session.getWrittenBytesThroughput();
		}
		return 0;
	}

	@Override
	public boolean isConnected() {
		return session != null && session.isConnected();
	}

	@Override
	public InetSocketAddress getLocalAddress() {
		return (InetSocketAddress) session.getLocalAddress();
	}

	@Override
	public C2SProtocol getReceiver() {
		return response;
	}
}
