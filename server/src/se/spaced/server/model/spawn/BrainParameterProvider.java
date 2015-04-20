package se.spaced.server.model.spawn;


import se.spaced.server.model.items.ServerItemTemplate;
import se.spaced.server.model.spawn.area.SpawnArea;
import se.spaced.shared.util.math.interval.IntervalInt;
import se.spaced.shared.world.area.Path;

import java.util.List;

public interface BrainParameterProvider {
	Path getPatrolPath();

	IntervalInt getRoamPausAtPoints();

	SpawnArea getRoamArea();

	WhisperMessage getWhisperMessage();

	List<ServerItemTemplate> getItemTypesForSale();

	String getScriptPath();

	AttackingParameters getAttackingParameters();

	ProximityAggroParameters getProximityAggroParameters();
}
