package se.spaced.server.stats;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.server.model.spawn.EntityTemplate;
import se.spaced.server.persistence.dao.interfaces.EntityTemplateDao;
import se.spaced.server.persistence.dao.interfaces.KillEntryDao;
import se.spaced.server.persistence.util.PageParameters;
import se.spaced.server.persistence.util.transactions.AutoTransaction;

import java.util.List;

@Singleton
public class KillStatisticsServiceImpl implements KillStatisticsService {

	private final KillEntryDao killEntryDao;
	private final EntityTemplateDao entityTemplateDao;

	@Inject
	public KillStatisticsServiceImpl(KillEntryDao killEntryDao, EntityTemplateDao entityTemplateDao) {
		this.killEntryDao = killEntryDao;
		this.entityTemplateDao = entityTemplateDao;
	}

	@Override
	@AutoTransaction
	public List<KillStat> getTopKilled(PageParameters pageParameters) {
		return killEntryDao.findTopKilled(pageParameters.getFirstResult(), pageParameters.getMaxResults());
	}

	@Override
	@AutoTransaction
	public List<KillStat> getTopKillers(PageParameters pageParameters) {
		return killEntryDao.findTopKillers(pageParameters.getFirstResult(), pageParameters.getMaxResults());
	}

	@Override
	@AutoTransaction
	public List<KillStat> getTopEntityKilledBy(UUID entityTemplatePk, PageParameters pageParameters) {
		EntityTemplate template = entityTemplateDao.findByPk(entityTemplatePk);
		return killEntryDao.findTopEntityKilledBy(template,
				pageParameters.getFirstResult(),
				pageParameters.getMaxResults());
	}

	@Override
	@AutoTransaction
	public List<KillStat> getTopKilledByEntity(UUID entityTemplatePk, PageParameters pageParameters) {
		EntityTemplate template = entityTemplateDao.findByPk(entityTemplatePk);
		return killEntryDao.findTopKilledByEntity(template,
				pageParameters.getFirstResult(),
				pageParameters.getMaxResults());
	}
}
