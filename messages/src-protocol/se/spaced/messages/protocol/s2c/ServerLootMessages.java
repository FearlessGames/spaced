package se.spaced.messages.protocol.s2c;

import se.smrt.core.SmrtProtocol;
import se.spaced.messages.protocol.SpacedItem;

@SmrtProtocol
public interface ServerLootMessages {
	void receivedLoot(SpacedItem item);
}
