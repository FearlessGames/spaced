package se.spaced.server.net;

import org.apache.mina.core.service.IoHandler;
import se.fearless.common.uuid.UUID;

import java.util.Map;

public interface ClientConnectionHandler {
	IoHandler getIOHandler();

	Map<UUID, ClientConnection> getConnectedClients();
}
