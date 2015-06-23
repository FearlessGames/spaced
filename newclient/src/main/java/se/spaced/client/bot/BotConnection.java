package se.spaced.client.bot;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.client.game.logic.local.LocalLoginLogic;
import se.spaced.client.model.listener.LoginListener;
import se.spaced.client.net.ping.PingManager;
import se.spaced.client.net.smrt.ServerConnection;
import se.spaced.client.net.smrt.ServerConnectionListener;
import se.spaced.messages.protocol.Salts;
import se.spaced.shared.network.protocol.codec.datatype.EntityData;

import java.util.List;


public class BotConnection implements ServerConnectionListener, LoginListener {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final ServerConnection serverConnection;
	private final LocalLoginLogic loginLogic;
	private final PingManager pingManager;
	private String accountName = "test10";
	private String accountPassword = "test";
	private long tokenId = 1L;
	private final List<Long> pingReplies = Lists.newLinkedList();

	private long latestLatency = -1;

	private boolean isConnecting = true;

	private final String hostName;

	private final int port;

	@Inject
	public BotConnection(
			@Named("hostName") String hostName, @Named("port") int port, ServerConnection serverConnection,
			LocalLoginLogic loginLogic, PingManager pingManager) {

		this.hostName = hostName;
		this.port = port;
		this.serverConnection = serverConnection;
		this.loginLogic = loginLogic;
		this.pingManager = pingManager;
	}

//	@Override
//	public void disconnected(String message) {
//		setDisconnected(true);
//		logger.info("DC " + message);
//		long sum = 0;
//		int samples = 0;
//		long max = 0;
//		for (Long pingReply : pingReplies) {
//			sum = pingReply;
//			samples++;
//			max = Math.max(max, pingReply);
//		}
//		logger.info(String.format("Avg ping: %f in %d replies. Max: %d", (double) (sum / samples), samples, max));
//	}
//
//	@Override
//	public void pong(long latency) {
//		latestLatency = latency;
//		pingReplies.add(latency);
//	}

	public void connectToServer() {
		logger.info("Starting bot");
		serverConnection.connect(hostName, port);
	}

	public void setAccountPassword(String accountPassword) {
		this.accountPassword = accountPassword;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public boolean isConnected() {
		return serverConnection.isConnected();
	}

	public boolean isConnecting() {
		return isConnecting;
	}

	public long getLatency() {
		return pingManager.getLatency();
	}

	@Override
	public void disconnected(final String message) {
		logger.info("Connection failed for server: " + message);
	}

	@Override
	public void connectionSucceeded(final String host, final int port) {
		logger.info("Connection succeeded");
		isConnecting = false;
		Salts authSalts = new Salts("userSalt", "oneTimeSalt");
		loginLogic.loginAccount(accountName, accountPassword, authSalts);
	}

	@Override
	public void connectionFailed(final String errorMessage) {
		logger.error("connectionFailed: {}", errorMessage);
		connectToServer();
	}

	@Override
	public void successfulPlayerLogin() {
	}

	@Override
	public void failedPlayerLogin(final String message) {
		logger.error("failedPlayerLogin: {}", message);
	}

	@Override
	public void characterListUpdated(final List<EntityData> characters) {
		logger.info("Got player list");
		if (!characters.isEmpty()) {
			loginLogic.loginCharacter(characters.get(0).getId());
		} else {
			logger.error("Got empty player list");
		}
	}

	@Override
	public void successfulPlayerLogout() {

	}
}
