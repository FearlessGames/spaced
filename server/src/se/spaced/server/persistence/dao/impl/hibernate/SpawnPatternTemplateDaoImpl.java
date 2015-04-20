package se.spaced.server.persistence.dao.impl.hibernate;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;
import se.spaced.server.model.spawn.SpawnPatternTemplate;
import se.spaced.server.persistence.dao.interfaces.SpawnPatternTemplateDao;

public class SpawnPatternTemplateDaoImpl extends DaoImpl<SpawnPatternTemplate> implements SpawnPatternTemplateDao {
	@Inject
	public SpawnPatternTemplateDaoImpl(SessionFactory sessionFactory) {
		super(sessionFactory, SpawnPatternTemplate.class);
	}
}
