package se.spaced.server.net.mina;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.executor.OrderedThreadPoolExecutor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearless.common.lifetime.LifetimeListener;
import se.fearless.common.lifetime.LifetimeManager;
import se.spaced.server.net.ClientConnectionHandler;
import se.spaced.server.net.RemoteServer;
import se.spaced.shared.concurrency.SimpleThreadFactory;
import se.spaced.shared.network.protocol.codec.mina.MessageCodecFactory;

import javax.inject.Singleton;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Singleton
public class RemoteServerImpl implements RemoteServer {

	final Logger logger = LoggerFactory.getLogger(getClass());

	private int timeout = 120;
	private final NioSocketAcceptor acceptor;
	private final ClientConnectionHandler clientConnectionHandler;

	private final ProtocolCodecFactory messageCodecFactory;
	private final LifetimeManager lifetimeManager;
	private final int gameServerPort;
	private ExecutorService filterExecutor;

	private enum State {
		UNKNOWN, STARTING, RUNNING, SHUTTINGDOWN
	}

	private volatile State state = State.UNKNOWN;

	@Inject
	public RemoteServerImpl(
			ClientConnectionHandler clientConnectionHandler, MessageCodecFactory messageCodecFactory,
			LifetimeManager lifetimeManager, @Named("gameServerPort") int gameServerPort) {
		this.clientConnectionHandler = clientConnectionHandler;
		this.messageCodecFactory = messageCodecFactory;
		this.lifetimeManager = lifetimeManager;
		this.gameServerPort = gameServerPort;
		acceptor = new NioSocketAcceptor();
		logger.debug("Created new RemoteServerImpl");
	}

	@Override
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	@Override
	public void startup() throws IOException {
		state = State.STARTING;
		filterExecutor = new OrderedThreadPoolExecutor(5, 20, 60L, TimeUnit.SECONDS,
				SimpleThreadFactory.withPrefix("incomingWorkerThread-"));
		lifetimeManager.addListener(new LifetimeListener() {
			@Override
			public void onStart() {
			}

			@Override
			public void onShutdown() {
				shutdown(filterExecutor);
			}
		});
		DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();

		chain.addLast("codec", new ProtocolCodecFilter(messageCodecFactory));
		chain.addLast("threadPool", new ExecutorFilter(filterExecutor));


		//acceptor.getSessionConfig().setIdleTime(IdleStatus.READER_IDLE, timeout); removed because of debuging problems in client
		//chain.addLast("spacedStatsFilter", new SpacedMessageStatsFilter());
		// Bind
		acceptor.setHandler(clientConnectionHandler.getIOHandler());
		acceptor.getSessionConfig().setReuseAddress(true);
		acceptor.getSessionConfig().setTcpNoDelay(true);
		acceptor.bind(new InetSocketAddress(gameServerPort));
		logger.info("RemoteServerImpl is now listening on port:" + gameServerPort);
		state = State.RUNNING;
	}

	private void shutdown(ExecutorService filterExecutor) {
		state = State.SHUTTINGDOWN;
		acceptor.unbind();
		acceptor.dispose();

		filterExecutor.shutdownNow();
	}

	@Override
	public void shutdown() {
		shutdown(filterExecutor);
	}

	@Override
	public boolean isRunning() {
		return state == State.RUNNING;
	}

}