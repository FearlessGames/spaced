package se.spaced.server.mob.brains.templates;

import com.google.common.collect.ImmutableSet;
import se.fearless.common.uuid.UUID;
import se.spaced.server.mob.brains.MobBrain;
import se.spaced.server.model.Mob;
import se.spaced.server.model.spawn.BrainParameterProvider;
import se.spaced.server.model.spawn.area.SpawnArea;
import se.spaced.server.persistence.dao.impl.ExternalPersistableBase;
import se.spaced.server.persistence.dao.interfaces.NamedPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public abstract class BrainTemplate extends ExternalPersistableBase implements NamedPersistable {

	@Column(nullable = true)
	private final String name;

	public BrainTemplate(UUID pk, String name) {
		super(pk);
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	public abstract MobBrain createBrain(Mob mob, SpawnArea spawnArea, BrainParameterProvider brainParameterProvider);

	public ImmutableSet<BrainParameter> getRequiredParameters() {
		return ImmutableSet.of();
	}
}
