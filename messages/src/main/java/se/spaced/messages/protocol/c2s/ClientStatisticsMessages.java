package se.spaced.messages.protocol.c2s;

import se.smrt.core.SmrtProtocol;
import se.spaced.messages.protocol.Entity;

@SmrtProtocol
public interface ClientStatisticsMessages {
	void requestCombatStatistics(Entity entity);
}