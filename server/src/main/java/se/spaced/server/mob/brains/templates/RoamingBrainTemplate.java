package se.spaced.server.mob.brains.templates;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import se.fearless.common.uuid.UUID;
import se.spaced.server.mob.MobOrderExecutor;
import se.spaced.server.mob.brains.MobBrain;
import se.spaced.server.mob.brains.RoamingBrain;
import se.spaced.server.model.Mob;
import se.spaced.server.model.spawn.BrainParameterProvider;
import se.spaced.server.model.spawn.MobSpawnTemplate;
import se.spaced.server.model.spawn.MobTemplate;
import se.spaced.server.model.spawn.area.SpawnArea;
import se.spaced.server.tools.spawnpattern.view.InputType;
import se.spaced.shared.util.random.RandomProvider;
import se.spaced.shared.util.random.RealRandomProvider;
import se.spaced.shared.world.area.Geometry;

import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
public class RoamingBrainTemplate extends BrainTemplate {

	@Transient
	private final RandomProvider randomProvider;
	@Transient
	private final MobOrderExecutor orderExecutor;

	@Inject
	public RoamingBrainTemplate(RandomProvider randomProvider, MobOrderExecutor orderExecutor) {
		this(randomProvider, orderExecutor, null);
	}

	private RoamingBrainTemplate(
			RandomProvider randomProvider,
			MobOrderExecutor orderExecutor,
			UUID pk) {
		super(pk, null);
		this.randomProvider = randomProvider;
		this.orderExecutor = orderExecutor;
	}

	@Override
	public MobBrain createBrain(Mob mob, SpawnArea spawnArea, BrainParameterProvider brainParameterProvider) {
		SpawnArea roamArea = brainParameterProvider.getRoamArea();
		if (roamArea == null) {
			roamArea = spawnArea;
		}
		return new RoamingBrain(mob,
				orderExecutor,
				roamArea,
				brainParameterProvider.getRoamPausAtPoints(),
				randomProvider
		);
	}

	public static RoamingBrainTemplate create(MobOrderExecutor mobOrderExecutor, UUID pk) {
		return new RoamingBrainTemplate(new RealRandomProvider(), mobOrderExecutor, pk);
	}

	@Override
	public ImmutableSet<BrainParameter> getRequiredParameters() {
		BrainParameter area = new BrainParameter() {
			@Override
			public Class<? extends BrainTemplate> getBrain() {
				return RoamingBrainTemplate.class;
			}

			@Override
			public String getName() {
				return "Roam area";
			}

			@Override
			public Object retrieveValue(MobTemplate mobTemplate, MobSpawnTemplate mobSpawnTemplate) {
				return mobSpawnTemplate.getGeometry();
			}

			@Override
			public void updateValue(MobSpawnTemplate mobSpawnTemplate, Object parameter) {
				mobSpawnTemplate.setGeometryData((Geometry) parameter);
			}

			@Override
			public InputType getType() {
				return InputType.GEOMETRY;
			}

			@Override
			public boolean isEditable() {
				return true;
			}
		};
		return ImmutableSet.of(area);
	}
}
