package se.spaced.server.persistence.dao.impl.hibernate;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;
import se.spaced.server.model.spawn.EntityTemplate;
import se.spaced.server.persistence.dao.interfaces.EntityTemplateDao;

public class EntityTemplateHibernateDao extends FindableDaoImpl<EntityTemplate> implements EntityTemplateDao {
	@Inject
	public EntityTemplateHibernateDao(SessionFactory sessionFactory) {
		super(sessionFactory, EntityTemplate.class);
	}
}
