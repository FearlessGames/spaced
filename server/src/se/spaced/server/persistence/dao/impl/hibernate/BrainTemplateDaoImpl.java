package se.spaced.server.persistence.dao.impl.hibernate;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;
import se.spaced.server.mob.brains.templates.BrainTemplate;
import se.spaced.server.persistence.dao.interfaces.BrainTemplateDao;

public class BrainTemplateDaoImpl extends FindableDaoImpl<BrainTemplate> implements BrainTemplateDao {
	@Inject
	public BrainTemplateDaoImpl(SessionFactory sessionFactory) {
		super(sessionFactory, BrainTemplate.class);
	}
}
