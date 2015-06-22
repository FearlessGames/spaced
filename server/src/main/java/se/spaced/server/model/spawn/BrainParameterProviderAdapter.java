package se.spaced.server.model.spawn;

import com.google.common.collect.Lists;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.spaced.server.model.items.ServerItemTemplate;
import se.spaced.server.model.spawn.area.SinglePointSpawnArea;
import se.spaced.server.model.spawn.area.SpawnArea;
import se.spaced.shared.util.math.interval.IntervalInt;
import se.spaced.shared.world.area.Path;

import java.util.List;

public class BrainParameterProviderAdapter implements BrainParameterProvider {
	@Override

	public Path getPatrolPath() {
		return new Path();
	}

	@Override
	public IntervalInt getRoamPausAtPoints() {
		return new IntervalInt(0, 0);
	}

	@Override
	public SpawnArea getRoamArea() {
		return new SinglePointSpawnArea(new SpacedVector3(0, 0, 0), SpacedRotation.IDENTITY);
	}

	@Override
	public WhisperMessage getWhisperMessage() {
		return new WhisperMessage("", 0.0, Long.MAX_VALUE);
	}

	@Override
	public List<ServerItemTemplate> getItemTypesForSale() {
		return Lists.newArrayList();
	}

	@Override
	public String getScriptPath() {
		return "";
	}

	@Override
	public AttackingParameters getAttackingParameters() {
		return new AttackingParameters(true, true);
	}

	@Override
	public ProximityAggroParameters getProximityAggroParameters() {
		return new ProximityAggroParameters(30, 0);
	}
}
