package se.spaced.server.model.aggro;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import se.fearless.common.collections.Collections3;
import se.spaced.server.model.ServerEntity;

import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleAggroManager implements AggroManager {
	private final Map<ServerEntity, AggroStats> map = Maps.newHashMap();

	private final AtomicInteger counter = new AtomicInteger();
	private final double switchFactor;

	private ServerEntity focus;
	private final Random rand;

	public SimpleAggroManager(double switchFactor, Random random) {
		this.switchFactor = switchFactor;
		this.rand = random;
	}

	@Override
	public synchronized ServerEntity getMostHated() {
		return focus;
	}

	@Override
	public synchronized void addHate(ServerEntity entity, int hate) {
		AggroStats aggroStats = map.get(entity);
		if (aggroStats == null) {
			aggroStats = new AggroStats(entity, hate, counter.incrementAndGet());
			map.put(entity, aggroStats);
		} else {
			int oldHate = aggroStats.getHate();
			aggroStats = new AggroStats(aggroStats, oldHate + hate);
			map.put(entity, aggroStats);
		}
		updateFocus();
	}

	@Override
	public synchronized int getHate(ServerEntity entity) {
		AggroStats aggroStats = map.get(entity);
		if (aggroStats == null) {
			return 0;
		}
		return aggroStats.getHate();
	}

	@Override
	public synchronized void clearHate(ServerEntity entity) {
		AggroStats aggroStats = map.get(entity);
		if (aggroStats != null) {
			map.put(entity, new AggroStats(aggroStats, 0));
		}
		if (entity == focus) {
			focus = null;
			updateFocus();
		}
	}

	private synchronized void updateFocus() {
		if (map.isEmpty()) {
			focus = null;
			return;
		}

		SortedSet<AggroStats> aggroes = Sets.newTreeSet();
		aggroes.addAll(map.values());

		AggroStats first = aggroes.first();
		if (focus == null) {
			assignFocus(first);
		} else {
			double focusHate = getHate(focus);
			double firstHate = first.getHate();
			if (firstHate > focusHate * switchFactor) {
				assignFocus(first);
			}
		}
	}

	private void assignFocus(AggroStats newFocus) {
		if (newFocus.getHate() <= 0) {
			return;
		}
		focus = newFocus.getSpacedServerEntity();
	}

	@Override
	public synchronized void clearAll() {
		map.clear();
		updateFocus();
	}

	@Override
	public synchronized  boolean isAggroWith(ServerEntity enemy) {
		return map.containsKey(enemy) && map.get(enemy).getHate() > 0;
	}

	@Override
	public synchronized ServerEntity getRandomHated(final ServerEntity... excluded) {
		final Set<ServerEntity> excludedEntities = Sets.newHashSet(excluded);
		return Collections3.getRandomElement(Lists.newArrayList(Iterables.filter(map.keySet(),
				new Predicate<ServerEntity>() {
					@Override
					public boolean apply(ServerEntity entity) {
						return !excludedEntities.contains(entity);
					}
				})), rand);
	}

	@Override
	public String dumpAggroDebug() {
		StringBuilder sb = new StringBuilder();
		sb.append("AggroMap={");
		for (Map.Entry<ServerEntity, AggroStats> entry : map.entrySet()) {
			sb.append("{");
			sb.append(entry.getKey().getName()).append(" : ").append(entry.getValue());
			sb.append("}");
		}
		sb.append("}");


		return sb.toString();

	}


	protected synchronized int nrOfAggroTargets() {
		return map.keySet().size();
	}
}
