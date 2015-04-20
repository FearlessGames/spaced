package se.spaced.messages.protocol.s2c;

import se.smrt.core.SmrtProtocol;
import se.spaced.shared.model.player.PlayerCreationFailure;
import se.spaced.shared.network.protocol.codec.datatype.EntityData;

@SmrtProtocol
public interface ServerAccountMessages {
	void playerCreated(EntityData character);
	void failedToCreatePlayer(String name, PlayerCreationFailure reason);
}
