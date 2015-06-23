package se.spaced.server.stats;

import se.fearless.common.uuid.UUID;
import se.spaced.server.persistence.util.PageParameters;

import java.util.List;

public interface KillStatisticsService {
	List<KillStat> getTopKilled(PageParameters pageParameters);

	List<KillStat> getTopKillers(PageParameters pageParameters);

	List<KillStat> getTopEntityKilledBy(UUID entityTemplatePk, PageParameters pageParameters);

	List<KillStat> getTopKilledByEntity(UUID entityTemplatePk, PageParameters pageParameters);
}
