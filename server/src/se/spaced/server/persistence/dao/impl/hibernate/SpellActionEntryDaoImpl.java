package se.spaced.server.persistence.dao.impl.hibernate;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;
import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.server.persistence.dao.interfaces.SpellActionEntryDao;
import se.spaced.server.persistence.util.PageParameters;
import se.spaced.server.persistence.util.transactions.AutoTransaction;
import se.spaced.server.stats.SpellActionEntry;

import java.util.List;

public class SpellActionEntryDaoImpl extends DaoImpl<SpellActionEntry> implements SpellActionEntryDao {
	@Inject
	public SpellActionEntryDaoImpl(SessionFactory sessionFactory) {
		super(sessionFactory, SpellActionEntry.class);
	}

	@SuppressWarnings("unchecked")
	@AutoTransaction
	@Override
	public List<SpellActionEntry> findPerformersSpellActions(UUID performerPk, PageParameters pageParameters) {
		return getSession().createQuery("from SpellActionEntry where performer.pk = :performerPk order by endTime desc").
				setParameter("performerPk", performerPk).
				setFirstResult(pageParameters.getFirstResult()).
				setMaxResults(pageParameters.getMaxResults()).list();
	}
}
