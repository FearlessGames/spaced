package se.spaced.server.net;


import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.messages.protocol.c2s.C2SProtocol;
import se.spaced.messages.protocol.c2s.remote.C2SChecksum;
import se.spaced.messages.protocol.c2s.remote.C2SRemoteProtocolErrorHandler;
import se.spaced.messages.protocol.c2s.remote.C2SVersionReceiver;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.account.Account;
import se.spaced.server.model.Player;
import se.spaced.server.net.broadcast.SmrtBroadcaster;
import se.spaced.server.net.mina.ClientListenerFactory;


public class ClientConnectionImpl implements ClientConnection, C2SRemoteProtocolErrorHandler {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final UUID uuid;
	private final IoSession ioSession;
	private Player player;
	private Account account;
	private final S2CProtocol receiver;
	private final C2SVersionReceiver clientListener;

	public ClientConnectionImpl(IoSession ioSession, UUID uuid, S2CProtocol receiver, ClientListenerFactory clientListenerProvider,
									SmrtBroadcaster<S2CProtocol> broadcaster) {
		this.ioSession = ioSession;
		this.uuid = uuid;
		this.receiver = receiver;
		C2SProtocol protocol = clientListenerProvider.create(this, receiver.connection(), receiver.ping(), broadcaster);
		clientListener = new C2SVersionReceiver(protocol, this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		ClientConnectionImpl that = (ClientConnectionImpl) o;

		return uuid.equals(that.uuid);
	}

	@Override
	public int hashCode() {
		return uuid.hashCode();
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	@Override
	public void setPlayer(Player player) {
		this.player = player;
	}

	@Override
	public UUID getUUID() {
		return uuid;
	}

	@Override
	public Account getAccount() {
		return account;
	}

	@Override
	public void setAccount(Account account) {
		this.account = account;
	}

	@Override
	public S2CProtocol getReceiver() {
		return receiver;
	}

	@Override
	public C2SVersionReceiver getClientListener() {
		return clientListener;
	}


	@Override
	public void versionMismatch(C2SChecksum expected, C2SChecksum actual) {
		log.error("Version mismatch, expected " + expected + ", got " + actual);
		disconnect();
	}

	public void disconnect() {
		ioSession.close(true);
	}
}
