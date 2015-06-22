package se.spaced.server.model.contribution;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.entity.EntityServiceListener;

import java.util.Map;

public class ContributionServiceImpl implements ContributionService, EntityServiceListener {

	private final Map<ServerEntity, Multiset<ServerEntity>> contributions = Maps.newConcurrentMap();

	@Override
	public void addContribution(ServerEntity entity, ServerEntity contributor, int amount) {
		Multiset<ServerEntity> contributionForEntity = contributions.get(entity);
		contributionForEntity.add(contributor, amount);
	}

	@Override
	public Multiset<ServerEntity> getContributors(ServerEntity entity) {
		return contributions.get(entity);
	}

	@Override
	public void clearContributions(ServerEntity entity) {
		Multiset<ServerEntity> entities = contributions.get(entity);
		if (entities != null) {
			entities.clear();
		}
	}

	@Override
	public boolean isContributor(ServerEntity entity, ServerEntity contributor) {
		return contributions.get(entity).contains(contributor);
	}

	@Override
	public void entityAdded(ServerEntity entity) {
		contributions.put(entity, HashMultiset.<ServerEntity>create());
	}

	@Override
	public void entityRemoved(ServerEntity entity) {
		contributions.remove(entity);
	}
}
