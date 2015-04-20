package se.spaced.server.net.broadcast;

import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.messages.protocol.s2c.S2CMultiDispatcher;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.combat.Combat;
import se.spaced.server.model.combat.CombatRepository;
import se.spaced.server.model.entity.EntityService;
import se.spaced.server.model.entity.VisibilityService;
import se.spaced.shared.activecache.Job;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

public class SmrtBroadcastMessageImpl implements SmrtBroadcastMessage<S2CProtocol> {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final Set<S2CProtocol> receivers = Sets.newHashSet();
	private final EntityService entityService;
	private final S2CProtocol spies;
	private final CombatRepository combatRepository;
	private final VisibilityService visibilityService;

	public SmrtBroadcastMessageImpl(
			EntityService entityService,
			S2CProtocol spies,
			CombatRepository combatRepository, VisibilityService visibilityService) {
		this.entityService = entityService;
		this.spies = spies;
		this.combatRepository = combatRepository;
		this.visibilityService = visibilityService;
	}

	@Override
	public S2CProtocol getReceiver(ServerEntity entity) {
		return entityService.getSmrtReceiver(entity);
	}

	@Override
	public SmrtBroadcastMessageImpl to(S2CProtocol client) {
		if (client != null) {
			receivers.add(client);
		}
		return this;
	}

	@Override
	public SmrtBroadcastMessageImpl to(S2CProtocol... clients) {
		for (S2CProtocol client : clients) {
			to(client);
		}
		return this;
	}

	@Override
	public SmrtBroadcastMessageImpl to(ServerEntity entity) {
		return to(getReceiver(entity));
	}

	@Override
	public SmrtBroadcastMessageImpl to(ServerEntity... entities) {
		for (ServerEntity entity : entities) {
			to(entity);
		}
		return this;
	}

	@Override
	public SmrtBroadcastMessageImpl to(Collection<ServerEntity> entities) {
		for (ServerEntity entity : entities) {
			to(entity);
		}
		return this;
	}

	@Override
	public SmrtBroadcastMessageImpl toArea(ServerEntity around) {
		to(around);
		final CountDownLatch latch = new CountDownLatch(1);
		visibilityService.invokeForNearby(around, new Job<Collection<ServerEntity>>() {
			@Override
			public void run(Collection<ServerEntity> value) {
				to(value);
				latch.countDown();
			}
		});
		try {
			latch.await();
		} catch (InterruptedException e) {
			log.warn("Interrupted when waiting for message sending to complete", e);
		}
		return this;
	}

	@Override
	public SmrtBroadcastMessageImpl toAll() {
		return to(entityService.getAllEntities(null));
	}

	@Override
	public SmrtBroadcastMessageImpl toParty(ServerEntity member) {
		// TODO: implement this properly once we have parties
		return to(member);
	}

	@Override
	public SmrtBroadcastMessageImpl exclude(S2CProtocol client) {
		receivers.remove(client);
		return this;
	}

	@Override
	public SmrtBroadcastMessageImpl exclude(ServerEntity entity) {
		receivers.remove(getReceiver(entity));
		return this;

	}

	@Override
	public S2CProtocol send() {
		S2CMultiDispatcher multiDispatcher = new S2CMultiDispatcher(receivers);
		multiDispatcher.add(spies);

		return multiDispatcher;
	}

	@Override
	public SmrtBroadcastMessageImpl toCombat(ServerEntity entity) {
		Combat combat = combatRepository.getCombat(entity);
		if (combat == null) {
			return toArea(entity);
		}
		return toCombat(combat);
	}

	@Override
	public SmrtBroadcastMessageImpl toCombat(Combat combat) {
		if (combat != null) {
			Collection<ServerEntity> participants = combat.getParticipants();
			for (ServerEntity participant : participants) {
				to(participant);
				toArea(participant);
			}
		}
		return this;
	}


	@Override
	public SmrtBroadcastMessageImpl toCombat(ServerEntity... entities) {
		for (ServerEntity entity : entities) {
			toCombat(entity);
		}
		return this;
	}

	@Override
	public Collection<S2CProtocol> getReceivers() {
		return receivers;
	}
}
