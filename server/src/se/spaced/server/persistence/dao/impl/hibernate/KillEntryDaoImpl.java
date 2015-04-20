package se.spaced.server.persistence.dao.impl.hibernate;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import se.spaced.server.model.spawn.EntityTemplate;
import se.spaced.server.persistence.dao.interfaces.KillEntryDao;
import se.spaced.server.persistence.util.transactions.AutoTransaction;
import se.spaced.server.stats.KillEntry;
import se.spaced.server.stats.KillStat;

import java.util.List;

public class KillEntryDaoImpl extends DaoImpl<KillEntry> implements KillEntryDao {

	public KillEntryDaoImpl(SessionFactory sessionFactory) {
		super(sessionFactory, KillEntry.class);
	}

	@Override
	@AutoTransaction
	public KillEntry findByParticipants(EntityTemplate killer, EntityTemplate victim) {
		return (KillEntry) getSession().
				createCriteria(KillEntry.class).
				add(Restrictions.eq("killer", killer)).
				add(Restrictions.eq("victim", victim)).
				uniqueResult();
	}

	@Override
	@AutoTransaction
	public int deathsForVictim(EntityTemplate victim) {

		Criteria criteria = getSession().createCriteria(KillEntry.class);
		criteria.add(Restrictions.eq("victim", victim));
		criteria.setProjection(Projections.groupProperty("victim"));
		criteria.setProjection(Projections.sum("killCount"));
		Object o = criteria.list().get(0);

		if (o == null) {
			return 0;
		}

		return ((Long) o).intValue();
	}

	@Override
	@AutoTransaction
	public List<KillStat> findTopKilled(int firstResult, int maxResults) {
		Query query = getSession().createQuery(
				"select new se.spaced.server.stats.KillStat(sum(k.killCount), k.victim) " +
						"from se.spaced.server.stats.KillEntry k group by k.victim order by sum(k.killCount) desc");
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResults);
		return query.list();
	}

	@Override
	public List<KillStat> findTopKillers(int firstResult, int maxResults) {
		Query query = getSession().createQuery(
				"select new se.spaced.server.stats.KillStat(sum(k.killCount), k.killer) " +
						"from se.spaced.server.stats.KillEntry k group by k.killer order by sum(k.killCount) desc, k.killer.pk desc");
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResults);
		return query.list();
	}

	@Override
	public List<KillStat> findTopKilledByEntity(EntityTemplate killer, int firstResult, int maxResult) {
		Query query = getSession().createQuery(
				"select new se.spaced.server.stats.KillStat(sum(k.killCount), k.victim)" +
						"from se.spaced.server.stats.KillEntry k where k.killer = :killer group by k.victim order by sum(k.killCount) desc");
		query.setParameter("killer", killer);
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResult);
		return query.list();
	}

	@Override
	public List<KillStat> findTopEntityKilledBy(EntityTemplate victim, int firstResult, int maxResult) {
		Query query = getSession().createQuery(
				"select new se.spaced.server.stats.KillStat(sum(k.killCount), k.killer) " +
						"from se.spaced.server.stats.KillEntry k where k.victim = :victim group by k.killer order by sum(k.killCount) desc, k.killer.pk desc");
		query.setParameter("victim", victim);
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResult);
		return query.list();
	}

}
