package se.spaced.server.persistence.dao.impl.hibernate;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;
import se.spaced.server.model.cooldown.CooldownTemplate;
import se.spaced.server.persistence.dao.interfaces.CooldownTemplateDao;

public class CooldownTemplateDaoImpl extends FindableDaoImpl<CooldownTemplate> implements CooldownTemplateDao {
	@Inject
	public CooldownTemplateDaoImpl(SessionFactory sessionFactory) {
		super(sessionFactory, CooldownTemplate.class);
	}
}