package se.spaced.messages.protocol.s2c;

import se.smrt.core.SmrtProtocol;
import se.spaced.messages.protocol.Entity;

@SmrtProtocol
public interface ServerStatisticsMessages {
	void killStatisticsForEntity(Entity entity, int kills, int deaths);
}
