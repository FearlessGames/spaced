package se.spaced.server.net.broadcast;

import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.combat.Combat;

import java.util.Collection;

public interface SmrtBroadcastMessage<T> {
	T getReceiver(ServerEntity entity);

	SmrtBroadcastMessage<T> to(T client);

	SmrtBroadcastMessage<T> to(T... clients);

	SmrtBroadcastMessage<T> to(ServerEntity entity);

	SmrtBroadcastMessage<T> to(ServerEntity... entities);

	SmrtBroadcastMessage<T> to(Collection<ServerEntity> entities);

	SmrtBroadcastMessage<T> toArea(ServerEntity around);

	SmrtBroadcastMessage<T> toAll();

	SmrtBroadcastMessage<T> toParty(ServerEntity member);

	SmrtBroadcastMessage<T> exclude(T client);

	SmrtBroadcastMessage<T> exclude(ServerEntity entity);

	T send();

	SmrtBroadcastMessage<T> toCombat(ServerEntity entity);

	SmrtBroadcastMessage<T> toCombat(Combat combat);

	SmrtBroadcastMessage<T> toCombat(ServerEntity... entities);

	Collection<T> getReceivers();
}
