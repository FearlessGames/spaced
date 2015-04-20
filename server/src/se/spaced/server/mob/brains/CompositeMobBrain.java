package se.spaced.server.mob.brains;

import com.google.common.collect.Lists;
import se.spaced.messages.protocol.s2c.S2CMultiDispatcher;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.mob.MobDecision;
import se.spaced.server.model.Mob;
import se.spaced.shared.model.EntityInteractionCapability;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

public class CompositeMobBrain implements MobBrain {

	private final List<MobBrain> brains;
	private final Mob mob;
	private final S2CMultiDispatcher smrtReceiver;

	public CompositeMobBrain(Iterable<MobBrain> brains) {
		Iterator<MobBrain> brainIterator = brains.iterator();
		if (!brainIterator.hasNext()) {
			throw new IllegalArgumentException("Needs atleast one mob brain");
		}
		mob = brainIterator.next().getMob();
		while (brainIterator.hasNext()) {
			if (brainIterator.next().getMob() != mob) {
				throw new IllegalArgumentException("All brains need to reference the same mob");
			}
		}
		smrtReceiver = new S2CMultiDispatcher();
		for (MobBrain brain : brains) {
			smrtReceiver.add(brain.getSmrtReceiver());
		}
		this.brains = Lists.newArrayList(brains);
	}

	@Override
	public MobDecision act(long now) {
		for (MobBrain brain : brains) {
			MobDecision decision = brain.act(now);
			if (decision != MobDecision.UNDECIDED) {
				return decision;
			}
		}
		return MobDecision.UNDECIDED;
	}

	@Override
	public Mob getMob() {
		return mob;
	}

	@Override
	public S2CProtocol getSmrtReceiver() {
		return smrtReceiver;
	}

	@Override
	public EnumSet<EntityInteractionCapability> getInteractionCapabilities() {
		EnumSet<EntityInteractionCapability> capabilities = EnumSet.noneOf(EntityInteractionCapability.class);
		for (MobBrain brain : brains) {
			capabilities.addAll(brain.getInteractionCapabilities());
		}
		return capabilities;
	}

	public List<MobBrain> getBrains() {
		return brains;
	}
}
