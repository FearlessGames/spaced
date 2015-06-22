package se.spaced.server.model.spawn;

import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.server.model.spawn.area.SpawnArea;

import java.util.Collection;
import java.util.Set;

public class SpawnPattern {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private final SpawnArea area;

	private final Set<MobSpawn> mobSpawns = Sets.newHashSet();

	// Dependencies

	// TODO Have this in the persistable+editable template
	static final int DEFAULT_DECAY_TIME = 1 * 60 * 1000;

	/**
	 * Creates a new spawn pattern
	 *
	 * @param area					 the area within which the spawns will be added
	 * @param spawns
	 */
	public SpawnPattern(SpawnArea area, Collection<MobSpawn> spawns) {
		this.area = area;
		mobSpawns.addAll(spawns);
	}

	@Override
	public String toString() {
		return "SpawnPattern{" +
				"area=" + area +
				", mobSpawns=" + mobSpawns +
				'}';
	}

	public Iterable<? extends MobSpawn> getMobSpawns() {
		return mobSpawns;
	}
}
