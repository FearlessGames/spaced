package se.spaced.server.mob.brains.templates;

import com.google.common.collect.ImmutableSet;
import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.server.mob.brains.MobBrain;
import se.spaced.server.mob.brains.NullBrain;
import se.spaced.server.model.Mob;
import se.spaced.server.model.spawn.BrainParameterProvider;
import se.spaced.server.model.spawn.area.SpawnArea;

import javax.persistence.Entity;

@Entity
public class NullBrainTemplate extends BrainTemplate {
	public static final NullBrainTemplate INSTANCE = new NullBrainTemplate(UUID.fromString(
			"f3578cef-d61e-44fe-a057-9f43c250aee4"), "Null brain template");

	public NullBrainTemplate() {
		super(null, null);
	}

	public NullBrainTemplate(UUID uuid, String name) {
		super(uuid, name);
	}

	@Override
	public MobBrain createBrain(
			Mob mob,
			SpawnArea spawnArea, BrainParameterProvider brainParameterProvider) {
		return new NullBrain(mob);
	}

	@Override
	public ImmutableSet<BrainParameter> getRequiredParameters() {
		return ImmutableSet.of();
	}
}
