package se.spaced.server.mob.brains.templates;

import se.fearlessgames.common.util.TimeProvider;
import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.server.mob.MobOrderExecutor;
import se.spaced.server.mob.brains.MobBrain;
import se.spaced.server.mob.brains.ProximityWhisperBrain;
import se.spaced.server.model.Mob;
import se.spaced.server.model.spawn.BrainParameterProvider;
import se.spaced.server.model.spawn.area.SpawnArea;

import javax.inject.Inject;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
public class ProximityWhisperBrainTemplate extends BrainTemplate {
	@Transient
	private final MobOrderExecutor mobOrderExecutor;
	@Transient
	private final TimeProvider timeProvider;

	@Inject
	public ProximityWhisperBrainTemplate(MobOrderExecutor mobOrderExecutor, TimeProvider timeProvider) {
		this(mobOrderExecutor, timeProvider, null, null);
	}

	public ProximityWhisperBrainTemplate(
			MobOrderExecutor mobOrderExecutor,
			TimeProvider timeProvider,
			UUID pk,
			String name) {
		super(pk, name);
		this.mobOrderExecutor = mobOrderExecutor;
		this.timeProvider = timeProvider;
	}

	@Override
	public MobBrain createBrain(Mob mob, SpawnArea spawnArea, BrainParameterProvider brainParameterProvider) {
		return new ProximityWhisperBrain(mob, brainParameterProvider.getWhisperMessage(), mobOrderExecutor, timeProvider);
	}
}
