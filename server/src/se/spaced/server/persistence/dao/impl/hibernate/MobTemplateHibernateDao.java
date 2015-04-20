package se.spaced.server.persistence.dao.impl.hibernate;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;
import se.spaced.server.model.spawn.MobTemplate;
import se.spaced.server.persistence.dao.interfaces.MobTemplateDao;

public class MobTemplateHibernateDao extends FindableDaoImpl<MobTemplate> implements MobTemplateDao {
	@Inject
	public MobTemplateHibernateDao(SessionFactory sessionFactory) {
		super(sessionFactory, MobTemplate.class);
	}
}
