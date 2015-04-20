package se.spaced.server.net;

import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.messages.protocol.c2s.remote.C2SVersionReceiver;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.account.Account;
import se.spaced.server.model.Player;

public interface ClientConnection {
	void setPlayer(Player player);
	Player getPlayer();

	void setAccount(Account account);
	Account getAccount();

	S2CProtocol getReceiver();

	C2SVersionReceiver getClientListener();

	UUID getUUID();
}