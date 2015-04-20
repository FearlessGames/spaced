package se.spaced.server.model.contribution;

import com.google.common.collect.Multiset;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.entity.EntityServiceListener;

public interface ContributionService extends EntityServiceListener {

	void addContribution(ServerEntity entity, ServerEntity contributor, int amount);

	Multiset<ServerEntity> getContributors(ServerEntity entity);

	void clearContributions(ServerEntity entity);

	boolean isContributor(ServerEntity entity, ServerEntity contributor);
}
