package se.spaced.server.model.spawn;

import se.ardortech.math.SpacedRotation;
import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.server.model.items.ServerItemTemplate;
import se.spaced.server.model.spawn.area.PolygonSpaceSpawnArea;
import se.spaced.server.model.spawn.area.SinglePointSpawnArea;
import se.spaced.server.model.spawn.area.SpawnArea;
import se.spaced.shared.util.math.interval.IntervalInt;
import se.spaced.shared.util.random.RandomProvider;
import se.spaced.shared.world.area.Geometry;
import se.spaced.shared.world.area.Path;
import se.spaced.shared.world.area.Polygon;
import se.spaced.shared.world.area.SinglePoint;

import java.util.List;

public class BrainParameterProviderImpl implements BrainParameterProvider {

	private final MobSpawnTemplate mobSpawnTemplate;
	private final RandomProvider randomProvider;
	private final MobTemplate mobTemplate;

	public BrainParameterProviderImpl(
			MobSpawnTemplate mobSpawnTemplate,
			RandomProvider randomProvider,
			MobTemplate mobTemplate) {
		this.mobSpawnTemplate = mobSpawnTemplate;
		this.randomProvider = randomProvider;
		this.mobTemplate = mobTemplate;
	}

	@Override
	public Path getPatrolPath() {
		Geometry geometry = mobSpawnTemplate.getGeometry();
		if (geometry instanceof Path) {
			return (Path) geometry;
		}
		throw new RuntimeException(String.format("Requested path could not be found for %s",
				mobSpawnTemplate.getMobTemplate().getName()));
	}

	@Override
	public IntervalInt getRoamPausAtPoints() {
		return mobSpawnTemplate.getTimePausAtPoints();
	}

	@Override
	public SpawnArea getRoamArea() {
		Geometry roamArea = mobSpawnTemplate.getRoamArea();
		if (roamArea != null) {
			return createSpawnArea(roamArea);
		}
		Geometry geometry = mobSpawnTemplate.getGeometry();
		return createSpawnArea(geometry);
	}

	private SpawnArea createSpawnArea(Geometry geometry) {
		if (geometry instanceof Polygon) {
			return new PolygonSpaceSpawnArea(UUID.ZERO,
					(Polygon) geometry,
					SpacedRotation.IDENTITY,
					randomProvider);
		} else if (geometry instanceof SinglePoint) {
			return new SinglePointSpawnArea(UUID.ZERO, (SinglePoint) geometry);
		}
		return null;
	}

	@Override
	public WhisperMessage getWhisperMessage() {
		return mobTemplate.getWhisperMessage();
	}

	@Override
	public List<ServerItemTemplate> getItemTypesForSale() {
		return mobTemplate.getItemTypesForSale();
	}

	@Override
	public String getScriptPath() {
		String scriptPath = mobTemplate.getScriptPath();
		if (scriptPath == null || scriptPath.isEmpty()) {
			throw new RuntimeException(String.format("Script not specified for %s with script brain",
					mobTemplate.getName()));
		}
		return scriptPath;
	}

	@Override
	public AttackingParameters getAttackingParameters() {
		return new AttackingParameters(mobTemplate.isMoveToTarget(), mobTemplate.isLookAtTarget());
	}

	@Override
	public ProximityAggroParameters getProximityAggroParameters() {
		return new ProximityAggroParameters(mobTemplate.getProximityAggroDistance(),
				mobTemplate.getSocialAggroDistance());
	}
}
