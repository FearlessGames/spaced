package se.spaced.server.persistence.dao.impl.hibernate;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;
import se.spaced.server.persistence.dao.interfaces.GraveyardTemplateDao;

public class GraveyardTemplateDaoImpl extends FindableDaoImpl<GraveyardTemplate> implements GraveyardTemplateDao {
	@Inject
	public GraveyardTemplateDaoImpl(SessionFactory sessionFactory) {
		super(sessionFactory, GraveyardTemplate.class);
	}
}
