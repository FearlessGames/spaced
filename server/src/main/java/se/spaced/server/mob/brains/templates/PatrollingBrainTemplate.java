package se.spaced.server.mob.brains.templates;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.server.mob.MobOrderExecutor;
import se.spaced.server.mob.brains.MobBrain;
import se.spaced.server.mob.brains.PatrollingMobBrain;
import se.spaced.server.model.Mob;
import se.spaced.server.model.spawn.BrainParameterProvider;
import se.spaced.server.model.spawn.MobSpawnTemplate;
import se.spaced.server.model.spawn.MobTemplate;
import se.spaced.server.model.spawn.area.SpawnArea;
import se.spaced.server.tools.spawnpattern.view.InputType;
import se.spaced.shared.world.area.Geometry;
import se.spaced.shared.world.area.Path;

import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
public class PatrollingBrainTemplate extends BrainTemplate {
	private static final Logger log = LoggerFactory.getLogger(PatrollingBrainTemplate.class);

	@Transient
	private final MobOrderExecutor orderExecutor;
	public static final String PATROL_PATH = "Patrol path";

	@Inject
	protected PatrollingBrainTemplate(MobOrderExecutor orderExecutor) {
		this(orderExecutor, null);
	}

	private PatrollingBrainTemplate(
			MobOrderExecutor orderExecutor,
			UUID pk) {
		super(pk, null);
		this.orderExecutor = orderExecutor;
	}

	@Override
	public MobBrain createBrain(Mob mob, SpawnArea spawnArea, BrainParameterProvider brainParameterProvider) {
		Path path = brainParameterProvider.getPatrolPath();
		if (path.isEmpty()) {
			log.error("Patrolling brain template tried to create a patrollingBrain with a empty path for mob {} ",
					mob.getName());
		}
		return new PatrollingMobBrain(mob, path, orderExecutor);
	}

	public static PatrollingBrainTemplate create(
			MobOrderExecutor orderExecutor,
			UUID pk) {
		return new PatrollingBrainTemplate(orderExecutor, pk);
	}

	@Override
	public ImmutableSet<BrainParameter> getRequiredParameters() {

		BrainParameter patrolPath = new BrainParameter() {
			@Override
			public Class<? extends BrainTemplate> getBrain() {
				return PatrollingBrainTemplate.class;
			}

			@Override
			public String getName() {
				return PATROL_PATH;
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
		return ImmutableSet.of(patrolPath);
	}
}
