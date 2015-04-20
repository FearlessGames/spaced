package se.spaced.server.model.entity;

import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.ServerEntity;

public interface AppearanceService {
	void notifyAppeared(ServerEntity receiver, ServerEntity appeared);

	void notifyAppeared(S2CProtocol receiver, ServerEntity appeared);

	void notifyDisappeared(ServerEntity receiver, ServerEntity disappeared);

	void notifyDisappeared(S2CProtocol receiver, ServerEntity disappeared);
}
