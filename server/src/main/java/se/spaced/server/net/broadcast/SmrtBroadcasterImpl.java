package se.spaced.server.net.broadcast;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.spaced.messages.protocol.s2c.S2CMultiDispatcher;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.combat.CombatRepository;
import se.spaced.server.model.entity.EntityService;
import se.spaced.server.model.entity.VisibilityService;

@Singleton
public class SmrtBroadcasterImpl implements SmrtBroadcaster<S2CProtocol> {

	protected final EntityService entityService;
	private final S2CMultiDispatcher spies = new S2CMultiDispatcher();
	private final CombatRepository combatRepository;
	private final VisibilityService visibilityService;

	@Inject
	public SmrtBroadcasterImpl(EntityService entityService, CombatRepository combatRepository, VisibilityService visibilityService) {
		this.entityService = entityService;
		this.combatRepository = combatRepository;
		this.visibilityService = visibilityService;
	}

	@Override
	public SmrtBroadcastMessageImpl create() {
		return new SmrtBroadcastMessageImpl(entityService, spies, combatRepository, visibilityService);
	}

	@Override
	public void addSpy(S2CProtocol spy) {
		spies.add(spy);
	}
}
