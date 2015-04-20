package se.spaced.server.model.spawn.area;

import com.google.common.collect.Lists;
import org.hibernate.annotations.Type;
import se.spaced.server.persistence.dao.impl.ExternalPersistableBase;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Entity
public class CompositeSpawnArea extends ExternalPersistableBase implements SpawnArea {

	@Type(type = "xml")
	private List<? extends SpawnArea> areas;

	@Transient
	Random random = new Random();

	protected CompositeSpawnArea() {
	}

	public CompositeSpawnArea(Random random, Iterable<? extends SpawnArea> areas) {
		this.random = random;
		this.areas = Lists.newArrayList(areas);
	}

	@Override
	public SpawnPoint getNextSpawnPoint() {
		List<? extends SpawnArea> all = Lists.newArrayList(areas);
		Collections.shuffle(all, random);

		Collections.sort(all, new SpawnAreaComparator());
		SpawnArea first = all.get(0);
		return first.getNextSpawnPoint();
	}

	@Override
	public void addSpawn() {
		throw new RuntimeException("This method should never be called. Only supposed to call the children");
	}

	@Override
	public void removeSpawn() {
		throw new RuntimeException("This method should never be called. Only supposed to call the children");
	}

	public List<? extends SpawnArea> getAreas() {
		return areas;
	}

	@Override
	public int getSpawnCount() {
		int count = 0;
		for (SpawnArea area : areas) {
			count += area.getSpawnCount();
		}
		return count;
	}

}
